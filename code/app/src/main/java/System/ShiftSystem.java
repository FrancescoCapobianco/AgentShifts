package System.Service;

import android.content.Context;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import System.DAO.FlightDB;
import System.DAO.ShiftDB;
import System.DAO.UserDB;
import System.DAO.WorkingDayDB;
import System.Service.Objects.Azienda;
import System.Service.Objects.Flight;
import System.Service.Objects.Shift;
import System.Service.Objects.User;
import System.Service.Objects.WorkingDay;

public class ShiftSystem {

    private static ShiftSystem instance;

    private User utenteLoggato;
    private List<Azienda> Aziende;

    private UserDB userDB;
    private WorkingDayDB workingDayDB;
    private ShiftDB shiftDB;
    private FlightDB flightDB;

    private ShiftSystem(Context context) {
        Context appContext = context.getApplicationContext();

        userDB = new UserDB(appContext);
        workingDayDB = new WorkingDayDB(appContext);
        shiftDB = new ShiftDB(appContext);
        flightDB = new FlightDB(appContext);

        userDB.open();
        workingDayDB.open();
        shiftDB.open();
        flightDB.open();

        inizializzaAziende();
    }

    public static synchronized ShiftSystem getInstance(Context context) {
        if (instance == null) instance = new ShiftSystem(context);
        return instance;
    }

    private void inizializzaAziende() {
        Aziende = new ArrayList<>();

        Aziende.add(new Azienda(1, "Aviapartner", "aviapartner_logo"));
        Aziende.add(new Azienda(2, "GH", "gh_logo"));
        Aziende.add(new Azienda(3, "Aviation Services", "aviation_logo"));
    }

    public List<Azienda> getAziendeDisponibili() {
        return Aziende;
    }

    // --- GESTIONE App ---
    public boolean registraUtente(User nuovoUtente, String passwordInChiaro) {
        String salt = SecurityTools.generateSalt();
        String hash = SecurityTools.hashPassword(passwordInChiaro, salt);

        nuovoUtente.setSaltPassw(salt);
        nuovoUtente.setDigestPassw(hash);

        long result = userDB.insertUser(nuovoUtente);
        return result != -1;
    }

    public boolean login(String username, String passwordInserita) {

        User user = userDB.getUserByUsername(username);

        if (user != null) {
            boolean isPasswordCorrect = SecurityTools.checkPassword(
                    passwordInserita,
                    user.getSaltPassw(),
                    user.getDigestPassw()
            );

            if (isPasswordCorrect) {
                this.utenteLoggato = user;
                return true;
            }
        }
        return false;
    }

    public boolean autoLogin(String username) {
        User user = userDB.getUserByUsername(username);
        if (user != null) {
            this.utenteLoggato = user;
            return true;
        }
        return false;
    }

    public void logout() {
        this.utenteLoggato = null;
    }

    public User getUtenteLoggato() {
        return utenteLoggato;
    }


    public WorkingDay getGiornataLavorativa(Date data) {
        if (utenteLoggato == null) return null;

        Date dataNormalizzata = normalizzaData(data);

        WorkingDay giornata = workingDayDB.getWorkingDayByDateAndUser(dataNormalizzata, utenteLoggato.getId());

        if (giornata != null) {

            List<Shift> turni = shiftDB.getShiftsByWorkingDay(giornata.getId());
            giornata.setTurni(turni);

            List<Flight> voli = flightDB.getFlightsByWorkingDay(giornata.getId());
            giornata.setVoliGiornalieri(voli);
        }

        return giornata;
    }


    // --- GESTIONE LOGHI COMPAGNIE AEREE ---

    public String getLogoCompagniaDaVolo(String codiceVolo) {

        if (codiceVolo == null || codiceVolo.trim().length() < 2) {
            return "ic_default_airline";
        }

        String codiceIATA = codiceVolo.trim().substring(0, 2).toUpperCase();

        switch (codiceIATA) {
            case "FR": return "ic_ryanair_logo";
            case "U2": return "ic_easyjet_logo";
            case "W4":
            case "W6": return "ic_wizzair_logo";
            case "AZ": return "ic_ita_logo";
            case "LH": return "ic_lufthansa_logo";
            case "BA": return "ic_british_logo";
            case "AF": return "ic_airfrance_logo";
            case "VY": return "ic_vueling_logo";

            case "A3": return "ic_aegean_logo";
            case "SM": return "ic_aircairo_logo";
            case "AC": return "ic_aircanada_logo";
            case "JU": return "ic_airserbia_logo";
            case "SN": return "ic_brussels_logo";
            case "DX": return "ic_dat_logo";
            case "WK": return "ic_edelweiss_logo";
            case "AY": return "ic_finnair_logo";
            case "IB": return "ic_iberia_logo";
            case "KL": return "ic_klm_logo";
            case "LG": return "ic_luxair_logo";

            case "DY":
            case "D8": return "ic_norwegian_logo";
            case "AT": return "ic_royal";
            case "SK": return "ic_sas_logo";
            case "QS": return "ic_smartwings_logo";
            case "LX": return "ic_swiss_logo";
            case "HV":
            case "TO": return "ic_transavia_logo";
            case "TK": return "ic_turkish_logo";

            default:   return "ic_default_airline";
        }
    }

    // --- METODI DI SCRITTURA (CRUD - CREATE) ---

    private Date normalizzaData(Date data) {
        java.util.Calendar cal = java.util.Calendar.getInstance();
        cal.setTime(data);
        cal.set(java.util.Calendar.HOUR_OF_DAY, 0);
        cal.set(java.util.Calendar.MINUTE, 0);
        cal.set(java.util.Calendar.SECOND, 0);
        cal.set(java.util.Calendar.MILLISECOND, 0);
        return cal.getTime();
    }


    public WorkingDay creaO_OttieniGiornataLavorativa(Date data) {
        if (utenteLoggato == null) return null;

        Date dataNormalizzata = normalizzaData(data);

        WorkingDay giornataEsistente = workingDayDB.getWorkingDayByDateAndUser(dataNormalizzata, utenteLoggato.getId());

        if (giornataEsistente != null)
            return getGiornataLavorativa(dataNormalizzata);

        WorkingDay nuovaGiornata = new WorkingDay(dataNormalizzata, utenteLoggato.getId(), false);
        long newId = workingDayDB.insertWorkingDay(nuovaGiornata);

        if (newId != -1) {
            nuovaGiornata.setId((int) newId);
            nuovaGiornata.setVoliGiornalieri(new ArrayList<>());
            return nuovaGiornata;
        }

        return null;
    }


    public boolean impostaRiposo(Date data) {
        if (utenteLoggato == null) return false;

        Date dataNormalizzata = normalizzaData(data);

        WorkingDay giornataEsistente = workingDayDB.getWorkingDayByDateAndUser(dataNormalizzata, utenteLoggato.getId());

        if (giornataEsistente != null)
            workingDayDB.deleteWorkingDay(giornataEsistente.getId());

        WorkingDay giornoDiRiposo = new WorkingDay(dataNormalizzata, utenteLoggato.getId(), true);

        long resultId = workingDayDB.insertWorkingDay(giornoDiRiposo);
        return resultId != -1;
    }


    public boolean aggiungiTurno(WorkingDay giornata, String inizio, String fine, int allungo, int straordinario) {
        if (utenteLoggato == null || giornata == null)
            return false;

        if (giornata.getTurni() != null && giornata.getTurni().size() >= 2)
            return false;

        Shift nuovoTurno = new Shift(
                giornata.getId(), inizio, fine, allungo, straordinario,
                utenteLoggato.getPagaBase(), utenteLoggato.getPagaAllungo(),
                utenteLoggato.getPagaNotturna(), utenteLoggato.getPagaStraordinari(), utenteLoggato.getPagaFestivo()
        );

        long resultId = shiftDB.insertShift(nuovoTurno);

        if (resultId != -1) {
            nuovoTurno.setId((int) resultId);
            if (giornata.getTurni() == null) giornata.setTurni(new java.util.ArrayList<>());
            giornata.getTurni().add(nuovoTurno);
            return true;
        }
        return false;
    }


    public boolean aggiungiVolo(WorkingDay giornata, String compagnia, String arrivo, String destinazione, String codiceVolo) {
        if (giornata == null) return false;

        String logoDrawable = getLogoCompagniaDaVolo(codiceVolo);

        Flight nuovoVolo = new Flight(
                giornata.getId(),
                compagnia,
                arrivo,
                destinazione,
                codiceVolo,
                logoDrawable
        );

        long resultId = flightDB.insertFlight(nuovoVolo);

        if (resultId != -1) {
            nuovoVolo.setId((int) resultId);

            if (giornata.getVoliGiornalieri() == null) {
                giornata.setVoliGiornalieri(new ArrayList<>());
            }
            giornata.getVoliGiornalieri().add(nuovoVolo);
            return true;
        }
        return false;
    }


    public boolean modificaTurno(Shift turnoDaModificare, String nuovoInizio, String nuovaFine, int nuovoAllungo, int nuovoStraord) {
        if (turnoDaModificare == null) return false;

        turnoDaModificare.setInizioTurno(nuovoInizio);
        turnoDaModificare.setFineTurno(nuovaFine);
        turnoDaModificare.setMinutiAllungo(nuovoAllungo);
        turnoDaModificare.setMinutiStraordinario(nuovoStraord);

        int righeModificate = shiftDB.updateShift(turnoDaModificare);

        return righeModificate > 0;
    }

    public boolean eliminaGiornataLavorativa(Date data) {
        if (utenteLoggato == null) return false;

        Date dataNormalizzata = normalizzaData(data);
        WorkingDay giornata = workingDayDB.getWorkingDayByDateAndUser(dataNormalizzata, utenteLoggato.getId());

        if (giornata != null) {
            workingDayDB.deleteWorkingDay(giornata.getId());
            return true;
        }
        return false;
    }


    public float calcolaStipendioMeseCorrente() {
        if (utenteLoggato == null) return 0f;

        float stipendioTotale = 0f;
        Calendar cal = Calendar.getInstance();

        cal.set(Calendar.DAY_OF_MONTH, 1);
        int giorniNelMese = cal.getActualMaximum(Calendar.DAY_OF_MONTH);

        for (int i = 1; i <= giorniNelMese; i++) {
            cal.set(Calendar.DAY_OF_MONTH, i);

            WorkingDay wd = getGiornataLavorativa(cal.getTime());

            if (wd != null && wd.getTurni() != null)
                for (Shift turno : wd.getTurni())
                    stipendioTotale += StatsCalculator.calcolaIncassoTurno(turno, cal.getTime());
        }
        return stipendioTotale;
    }

    // Quando viene chiusa l'app:
    public void closeSystem() {
        userDB.close();
        workingDayDB.close();
        shiftDB.close();
        flightDB.close();
    }

}
