package activity.ui.agentshifts;

import android.app.DatePickerDialog;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import android.app.AlertDialog;
import android.view.LayoutInflater;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.LinearLayout;
import android.widget.ImageView;
import android.content.ClipboardManager;
import android.content.ClipData;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.ContextCompat;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.applandeo.materialcalendarview.CalendarView;
import com.applandeo.materialcalendarview.EventDay;
import com.applandeo.materialcalendarview.listeners.OnDayClickListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import System.Service.Objects.Azienda;
import System.Service.Objects.Shift;
import System.Service.ShiftSystem;
import System.Service.Objects.User;
import System.Service.Objects.WorkingDay;

public class HomeActivity extends AppCompatActivity {

    private TextView textBenvenuto;
    private CalendarView calendarView;
    private TextView textDataSelezionata;
    private TextView textStatoGiornata;
    private LinearLayout dettagliTurno;
    private TextView textOrariTurno;
    private TextView textAllunghi;
    private TextView textStraordinari;
    private TextView textContoVoli;
    private LinearLayout layoutAzioniGiornata;

    private ShiftSystem shiftSystem;
    private ImageView logoAzienda;
    private Date dataSelezionataAttuale;

    private RecyclerView recyclerViewVoli;
    private FlightAdapter flightAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        shiftSystem = ShiftSystem.getInstance(this);

        User utente = shiftSystem.getUtenteLoggato();
        if (utente == null) {
            finish();
            return;
        }

        textBenvenuto = findViewById(R.id.textBenvenuto);
        calendarView = findViewById(R.id.calendarView);
        textDataSelezionata = findViewById(R.id.textDataSelezionata);
        textStatoGiornata = findViewById(R.id.textStatoGiornata);
        dettagliTurno = findViewById(R.id.layoutDettagliTurno);
        textOrariTurno = findViewById(R.id.textOrariTurno);
        textAllunghi = findViewById(R.id.textAllungo);
        textStraordinari = findViewById(R.id.textStraordinario);
        textContoVoli = findViewById(R.id.textContoVoli);
        layoutAzioniGiornata = findViewById(R.id.layoutAzioniGiornata);
        logoAzienda = findViewById(R.id.logoAzienda);
        recyclerViewVoli = findViewById(R.id.recyclerViewVoli);
        recyclerViewVoli.setLayoutManager(new LinearLayoutManager(this));

        Calendar orarioAttuale = Calendar.getInstance();
        int oraDelGiorno = orarioAttuale.get(Calendar.HOUR_OF_DAY);
        int giorno = orarioAttuale.get(Calendar.DAY_OF_MONTH);
        int mese = orarioAttuale.get(Calendar.MONTH);

        String saluto;

        /* Msg GirlFriend:
        if (giorno == 24 && mese == Calendar.JANUARY
                && "Federica".equalsIgnoreCase(utente.getNome())
                && "Tomarchio".equalsIgnoreCase(utente.getCognome())) {
            saluto = "Tanti auguri amore mio";
            textBenvenuto.setText(saluto + "!");
        } else {
            if (oraDelGiorno >= 5 && oraDelGiorno <= 12)
                saluto = "Buongiorno";
            else if (oraDelGiorno >= 13 && oraDelGiorno <= 17)
                saluto = "Buon pomeriggio";
            else
                saluto = "Buonasera";
            textBenvenuto.setText(saluto + ", " + utente.getNome() + "!");
        }
        */

        if (oraDelGiorno >= 5 && oraDelGiorno <= 12)
            saluto = "Buongiorno";
        else if (oraDelGiorno >= 13 && oraDelGiorno <= 17)
            saluto = "Buon pomeriggio";
        else
            saluto = "Buonasera";
        textBenvenuto.setText(saluto + ", " + utente.getNome() + "!");

        for (Azienda azienda : shiftSystem.getAziendeDisponibili()) {
            if (azienda.getId() == utente.getAziendaID()) {
                String nomeFileImmagine = azienda.getLogoDrawable();
                int imageResource = getResources().getIdentifier(nomeFileImmagine, "drawable", getPackageName());

                if (imageResource != 0)
                    logoAzienda.setImageResource(imageResource);
                break;
            }
        }

        dataSelezionataAttuale = new Date();
        aggiornaDettagliGiornata(dataSelezionataAttuale);

        // Listener Calendar
        calendarView.setOnDayClickListener(new OnDayClickListener() {
            @Override
            public void onDayClick(EventDay eventDay) {
                Calendar clickedDayCalendar = eventDay.getCalendar();
                dataSelezionataAttuale = clickedDayCalendar.getTime();

                List<Calendar> selectedDates = new ArrayList<>();
                selectedDates.add(clickedDayCalendar);
                calendarView.setSelectedDates(selectedDates);

                int dayOfMonth = clickedDayCalendar.get(Calendar.DAY_OF_MONTH);
                int month = clickedDayCalendar.get(Calendar.MONTH);
                int year = clickedDayCalendar.get(Calendar.YEAR);

                Calendar oggi = Calendar.getInstance();
                if (dayOfMonth == oggi.get(Calendar.DAY_OF_MONTH) &&
                        month == oggi.get(Calendar.MONTH) && year == oggi.get(Calendar.YEAR))
                    textDataSelezionata.setText("Dettagli: Oggi");
                else
                    textDataSelezionata.setText("Dettagli: " + dayOfMonth + "/" + (month + 1));

                aggiornaDettagliGiornata(dataSelezionataAttuale);
            }
        });

        // Selezione "Oggi" all'avvio dell'app
        try {
            Calendar oggi = Calendar.getInstance();
            oggi.set(Calendar.HOUR_OF_DAY, 0);
            oggi.set(Calendar.MINUTE, 0);
            oggi.set(Calendar.SECOND, 0);
            oggi.set(Calendar.MILLISECOND, 0);

            calendarView.setDate(oggi);
            List<Calendar> selezIniziale = new ArrayList<>();
            selezIniziale.add(oggi);
            calendarView.setSelectedDates(selezIniziale);

            dataSelezionataAttuale = oggi.getTime();
        } catch (Exception e) {
            e.printStackTrace();
        }

        calendarView.setOnForwardPageChangeListener(() -> ricaricaEventiCalendario());
        calendarView.setOnPreviousPageChangeListener(() -> ricaricaEventiCalendario());

        ricaricaEventiCalendario();
    }

    // Turni dentro il Calendario:
    private Drawable creaPillolaTesto(String testo, int coloreSfondo) {
        float density = getResources().getDisplayMetrics().density;

        Paint paintTesto = new Paint(Paint.ANTI_ALIAS_FLAG);
        paintTesto.setColor(Color.WHITE);
        paintTesto.setTextSize(8.5f * density);
        paintTesto.setTextAlign(Paint.Align.CENTER);
        paintTesto.setTypeface(Typeface.DEFAULT_BOLD);

        float textWidth = paintTesto.measureText(testo);
        int width = (int) (textWidth + (12 * density));
        if (width < (int) (52 * density))
            width = (int) (52 * density);

        int height = (int) (14 * density);

        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);

        // Sfondo Colorato:
        Paint paintSfondo = new Paint(Paint.ANTI_ALIAS_FLAG);
        paintSfondo.setColor(coloreSfondo);
        RectF rect = new RectF(0, 0, width, height);
        canvas.drawRoundRect(rect, height / 2f, height / 2f, paintSfondo);

        // Testo:
        Paint.FontMetrics fm = paintTesto.getFontMetrics();
        float y = (height / 2f) + ((fm.descent - fm.ascent) / 2f) - fm.descent;

        canvas.drawText(testo, width / 2f, y, paintTesto);

        return new BitmapDrawable(getResources(), bitmap);
    }

    // btnIconaProfilo:
    public void actionShowProfileMenu(View v) {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        View view = LayoutInflater.from(this).inflate(R.layout.layout_bottom_menu, null);

        view.findViewById(R.id.menuSalary).setOnClickListener(v1 -> {
            bottomSheetDialog.dismiss();
            actionShowSalary(null);
        });
        view.findViewById(R.id.menuWeekly).setOnClickListener(v1 -> {
            bottomSheetDialog.dismiss();
            actionShowWeeklySchedule(null);
        });
        view.findViewById(R.id.menuTheme).setOnClickListener(v1 -> {
            bottomSheetDialog.dismiss();
            actionToggleTheme(null);
        });
        view.findViewById(R.id.menuLogout).setOnClickListener(v1 -> {
            bottomSheetDialog.dismiss();
            actionLogout(null);
        });

        bottomSheetDialog.setContentView(view);
        bottomSheetDialog.show();
    }

    // RicaricaCalendario:
    private void ricaricaEventiCalendario() {
        List<EventDay> events = new ArrayList<>();

        Calendar cal = calendarView.getCurrentPageDate();
        cal.set(Calendar.DAY_OF_MONTH, 1);
        int giorniNelMese = cal.getActualMaximum(Calendar.DAY_OF_MONTH);

        for (int i = 1; i <= giorniNelMese; i++) {
            cal.set(Calendar.DAY_OF_MONTH, i);
            WorkingDay wd = shiftSystem.getGiornataLavorativa(cal.getTime());

            if (wd != null) {
                Calendar eventCal = Calendar.getInstance();
                eventCal.setTime(cal.getTime());

                if (wd.isFerie())
                    events.add(new EventDay(eventCal, R.drawable.ic_dot_ferie));
                else if (wd.isRiposo())
                    events.add(new EventDay(eventCal, R.drawable.ic_dot_riposo));
                else if (wd.getTurni() != null && !wd.getTurni().isEmpty()) {

                    Shift primoTurno = wd.getTurni().get(0);
                    String testoPillola = primoTurno.getInizioTurno() + "-" + primoTurno.getFineTurno();

                    // RIENTRI:
                    if (wd.getTurni().size() > 1) {
                        Shift secondoTurno = wd.getTurni().get(1);
                        testoPillola += " | " + secondoTurno.getInizioTurno() + "-" + secondoTurno.getFineTurno();
                    }

                    int oraInizio = 0;
                    try {
                        oraInizio = Integer.parseInt(primoTurno.getInizioTurno().split(":")[0]);
                    } catch (Exception e) {}

                    int colore;

                    if (wd.getTurni().size() > 1)
                        colore = Color.parseColor("#00897B"); // Ottanio (Rientri)
                    else if (oraInizio >= 4 && oraInizio < 12)
                        colore = Color.parseColor("#E53935"); // Rosso (Mattina)
                    else if (oraInizio >= 12 && oraInizio <= 16)
                        colore = Color.parseColor("#F9A825"); // Giallo Scuro (Centrale)
                    else
                        colore = Color.parseColor("#8E24AA"); // Viola (Sera)

                    Drawable pillola = creaPillolaTesto(testoPillola, colore);
                    events.add(new EventDay(eventCal, pillola));
                }
            }
        }
        calendarView.setEvents(events);

        if (dataSelezionataAttuale != null) {
            List<Calendar> selectedDates = new ArrayList<>();
            Calendar calSel = Calendar.getInstance();
            calSel.setTime(dataSelezionataAttuale);

            calSel.set(Calendar.HOUR_OF_DAY, 0);
            calSel.set(Calendar.MINUTE, 0);
            calSel.set(Calendar.SECOND, 0);
            calSel.set(Calendar.MILLISECOND, 0);

            selectedDates.add(calSel);
            calendarView.setSelectedDates(selectedDates);
        }

    }

    private void aggiornaDettagliGiornata(Date data) {
        WorkingDay giornata = shiftSystem.getGiornataLavorativa(data);

        dettagliTurno.setVisibility(View.GONE);
        textContoVoli.setVisibility(View.GONE);
        layoutAzioniGiornata.setVisibility(View.GONE);
        recyclerViewVoli.setVisibility(View.GONE);

        textStatoGiornata.setTextColor(ContextCompat.getColor(this, android.R.color.darker_gray));

        if (giornata == null) {
            textStatoGiornata.setText("Nessun dato inserito");
            return;
        }

        layoutAzioniGiornata.setVisibility(View.VISIBLE);

        boolean haTurni = giornata.getTurni() != null && !giornata.getTurni().isEmpty();
        boolean haVoli = giornata.getVoliGiornalieri() != null && !giornata.getVoliGiornalieri().isEmpty();

        if (giornata.isFerie()) {
            textStatoGiornata.setText("FERIE");
            textStatoGiornata.setTextColor(ContextCompat.getColor(this, android.R.color.holo_red_dark));
        }
        else if (giornata.isRiposo() && !haTurni) {
            textStatoGiornata.setText("RIPOSO");
            textStatoGiornata.setTextColor(ContextCompat.getColor(this, android.R.color.holo_green_dark));
        }
        else if (giornata.isRiposo() && haTurni) {
            textStatoGiornata.setText("MANCATO RIPOSO");
            textStatoGiornata.setTextColor(ContextCompat.getColor(this, android.R.color.holo_orange_dark));
        }
        else if (!giornata.isRiposo() && haTurni) {
            textStatoGiornata.setText("TURNO");
            textStatoGiornata.setTextColor(ContextCompat.getColor(this, android.R.color.holo_blue_dark));
        }
        else {
            textStatoGiornata.setText("SENZA ORARIO");
        }

        if (haTurni) {
            dettagliTurno.setVisibility(View.VISIBLE);

            int totAllungo = 0;
            int totStraord = 0;

            if (giornata.getTurni().size() == 1) {
                Shift t = giornata.getTurni().get(0);
                textOrariTurno.setText("Orario: " + t.getInizioTurno() + " - " + t.getFineTurno());
                totAllungo = t.getMinutiAllungo();
                totStraord = t.getMinutiStraordinario();
            } else {
                Shift t1 = giornata.getTurni().get(0);
                Shift t2 = giornata.getTurni().get(1);
                textOrariTurno.setText("1° Turno: " + t1.getInizioTurno() + " - " + t1.getFineTurno() + "\n" +
                        "2° Turno: " + t2.getInizioTurno() + " - " + t2.getFineTurno());
                totAllungo = t1.getMinutiAllungo() + t2.getMinutiAllungo();
                totStraord = t1.getMinutiStraordinario() + t2.getMinutiStraordinario();
            }

            if (totAllungo > 0) {
                textAllunghi.setVisibility(View.VISIBLE);
                textAllunghi.setText("Allungo: " + formattaMinuti(totAllungo));
            } else {
                textAllunghi.setVisibility(View.GONE);
            }

            if (totStraord > 0) {
                textStraordinari.setVisibility(View.VISIBLE);
                textStraordinari.setText("Strd: " + formattaMinuti(totStraord));
            } else {
                textStraordinari.setVisibility(View.GONE);
            }
        }

        if (haVoli) {
            textContoVoli.setVisibility(View.VISIBLE);
            textContoVoli.setText("Voli gestiti: " + giornata.getVoliGiornalieri().size());

            recyclerViewVoli.setVisibility(View.VISIBLE);
            flightAdapter = new FlightAdapter(giornata.getVoliGiornalieri(), this);
            recyclerViewVoli.setAdapter(flightAdapter);
        }
    }

    // btnElimina
    public void actionEliminaGiornata(View v) {
        new AlertDialog.Builder(this)
                .setTitle("Attenzione")
                .setMessage("Vuoi eliminare tutti i dati di questa giornata?")
                .setPositiveButton("Elimina", (dialog, which) -> {
                    boolean successo = shiftSystem.eliminaGiornataLavorativa(dataSelezionataAttuale);
                    if (successo) {
                        Toast.makeText(this, "Giornata eliminata", Toast.LENGTH_SHORT).show();
                        aggiornaDettagliGiornata(dataSelezionataAttuale);
                        ricaricaEventiCalendario();
                    } else {
                        Toast.makeText(this, "Errore durante l'eliminazione", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Annulla", null)
                .show();
    }

    // btnModifica
    public void actionModificaGiornata(View v) {
        WorkingDay giornataAttuale = shiftSystem.getGiornataLavorativa(dataSelezionataAttuale);
        if (giornataAttuale == null) return;

        if (giornataAttuale.isRiposo() && (giornataAttuale.getTurni() == null || giornataAttuale.getTurni().isEmpty())) {
            Toast.makeText(this, "Riposo puro. Clicca su 'Turno' per un Mancato Riposo.", Toast.LENGTH_LONG).show();
            return;
        }

        List<Shift> turni = giornataAttuale.getTurni();
        if (turni == null || turni.isEmpty()) {
            Toast.makeText(this, "Nessun turno inserito da poter modificare!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (turni.size() == 1)
            mostraDialogModificaTurno(turni.get(0));
        else {
            String[] opzioni = {"1° Turno (" + turni.get(0).getInizioTurno() + ")", "2° Turno (" + turni.get(1).getInizioTurno() + ")"};
            new AlertDialog.Builder(this)
                    .setTitle("Quale turno vuoi modificare?")
                    .setItems(opzioni, (dialog, which) -> mostraDialogModificaTurno(turni.get(which)))
                    .show();
        }
    }

    private void mostraDialogModificaTurno(@NonNull Shift turnoAttuale) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_turno, null);
        builder.setView(dialogView);

        EditText editInizio = dialogView.findViewById(R.id.editInizioTurno);
        EditText editFine = dialogView.findViewById(R.id.editFineTurno);
        EditText editAllungo = dialogView.findViewById(R.id.editMinutiAllungo);
        EditText editStraord = dialogView.findViewById(R.id.editMinutiStraord);

        editInizio.setText(turnoAttuale.getInizioTurno());
        editFine.setText(turnoAttuale.getFineTurno());
        if (turnoAttuale.getMinutiAllungo() > 0) editAllungo.setText(String.valueOf(turnoAttuale.getMinutiAllungo()));
        if (turnoAttuale.getMinutiStraordinario() > 0) editStraord.setText(String.valueOf(turnoAttuale.getMinutiStraordinario()));

        builder.setPositiveButton("Aggiorna", (dialog, which) -> {
            String inizio = editInizio.getText().toString().trim();
            String fine = editFine.getText().toString().trim();
            int allungo = editAllungo.getText().toString().isEmpty() ? 0 : Integer.parseInt(editAllungo.getText().toString());
            int straord = editStraord.getText().toString().isEmpty() ? 0 : Integer.parseInt(editStraord.getText().toString());

            if (inizio.isEmpty() || fine.isEmpty()) {
                Toast.makeText(this, "Orari obbligatori!", Toast.LENGTH_SHORT).show();
                return;
            }

            if (shiftSystem.modificaTurno(turnoAttuale, inizio, fine, allungo, straord)) {
                Toast.makeText(this, "Turno aggiornato con successo!", Toast.LENGTH_SHORT).show();
                aggiornaDettagliGiornata(dataSelezionataAttuale);
                ricaricaEventiCalendario();
            } else {
                Toast.makeText(this, "Errore durante l'aggiornamento.", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("Annulla", null).create().show();
    }

    // btnTurno
    public void actionAggiungiTurno(View v) {
        WorkingDay giornataAttuale = shiftSystem.creaO_OttieniGiornataLavorativa(dataSelezionataAttuale);

        if (giornataAttuale == null) {
            Toast.makeText(this, "Errore nel caricamento della giornata", Toast.LENGTH_SHORT).show();
            return;
        }

        if (giornataAttuale.getTurni() != null && giornataAttuale.getTurni().size() >= 2) {
            Toast.makeText(this, "Limite massimo di 2 turni raggiunto per questa giornata!", Toast.LENGTH_LONG).show();
            return;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_turno, null);
        builder.setView(dialogView);

        EditText editInizio = dialogView.findViewById(R.id.editInizioTurno);
        EditText editFine = dialogView.findViewById(R.id.editFineTurno);
        EditText editAllungo = dialogView.findViewById(R.id.editMinutiAllungo);
        EditText editStraord = dialogView.findViewById(R.id.editMinutiStraord);

        builder.setPositiveButton("Salva", (dialog, which) -> {
            String inizio = editInizio.getText().toString().trim();
            String fine = editFine.getText().toString().trim();

            int allungo = editAllungo.getText().toString().isEmpty() ? 0 : Integer.parseInt(editAllungo.getText().toString());
            int straord = editStraord.getText().toString().isEmpty() ? 0 : Integer.parseInt(editStraord.getText().toString());

            if (inizio.isEmpty() || fine.isEmpty()) {
                Toast.makeText(this, "Orari obbligatori!", Toast.LENGTH_SHORT).show();
                return;
            }

            boolean ok = shiftSystem.aggiungiTurno(giornataAttuale, inizio, fine, allungo, straord);
            if (ok) {
                Toast.makeText(this, "Turno salvato!", Toast.LENGTH_SHORT).show();
                aggiornaDettagliGiornata(dataSelezionataAttuale);
                ricaricaEventiCalendario();
            } else {
                Toast.makeText(this, "Errore di salvataggio", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Annulla", (dialog, which) -> dialog.dismiss());
        builder.create().show();
    }

    // btnVolo
    public void actionAggiungiVolo(View v) {
        WorkingDay giornataAttuale = shiftSystem.creaO_OttieniGiornataLavorativa(dataSelezionataAttuale);

        if (giornataAttuale == null) return;

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_volo, null);
        builder.setView(dialogView);

        Spinner spinnerCompagnia = dialogView.findViewById(R.id.spinnerCompagniaAerea);
        EditText editCodice = dialogView.findViewById(R.id.editCodiceVolo);
        EditText editArrivo = dialogView.findViewById(R.id.editArrivo);
        EditText editDestinazione = dialogView.findViewById(R.id.editDestinazione);

        // Compagnie Aeree in base all'azienda: 1:AviaPartner, 2:GH, 3:Aviation Service
        User utente = shiftSystem.getUtenteLoggato();
        int AziendaID = (utente != null) ? utente.getAziendaID() : -1;

        String[] compagnieAeree;

        switch (AziendaID) {
            case 1:
                compagnieAeree = new String[]{"Aegean", "Air Cairo", "Air Canada", "Air France", "Air Serbia", "British Airways",
                        "Brussels Airlines", "DAT", "Edelweiss", "Finnair", "Iberia", "KLM", "Luxair", "Norwegian", "Royal Air Maroc", "SAS", "Smartwings",
                        "Transavia", "Turkish Airlines"};
                break;
            case 2:
                compagnieAeree = new String[]{"Ryanair", "EasyJet"};
                break;
            case 3:
                compagnieAeree = new String[]{""};
                break;
            default:
                compagnieAeree = new String[]{"Aegean", "Air Cairo", "Air Canada", "Air France", "Air Serbia", "British Airways",
                        "Brussels Airlines", "DAT", "Edelweiss", "Finnair", "Iberia", "KLM", "Luxair", "Norwegian", "SAS", "Smartwings",
                        "Transavia", "Turkish Airlines"};
                break;
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, compagnieAeree);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCompagnia.setAdapter(adapter);

        builder.setPositiveButton("Aggiungi", (dialog, which) -> {
            String compagniaSelezionata = spinnerCompagnia.getSelectedItem().toString();
            String codiceVolo = editCodice.getText().toString().trim().toUpperCase();
            String arrivo = editArrivo.getText().toString().trim().toUpperCase();
            String destinazione = editDestinazione.getText().toString().trim().toUpperCase();

            if (codiceVolo.isEmpty() || arrivo.isEmpty() || destinazione.isEmpty()) {
                Toast.makeText(this, "Compila tutti i campi!", Toast.LENGTH_SHORT).show();
                return;
            }

            boolean ok = shiftSystem.aggiungiVolo(giornataAttuale, compagniaSelezionata, arrivo, destinazione, codiceVolo);
            if (ok) {
                Toast.makeText(this, "Volo aggiunto!", Toast.LENGTH_SHORT).show();
                aggiornaDettagliGiornata(dataSelezionataAttuale);
            }
        });

        builder.setNegativeButton("Annulla", (dialog, which) -> dialog.dismiss());
        builder.create().show();
    }

    // btnRiposo
    public void actionImpostaRiposo(View v) {
        boolean successo = shiftSystem.impostaRiposo(dataSelezionataAttuale);
        if (successo) {
            Toast.makeText(this, "Riposo impostato!", Toast.LENGTH_SHORT).show();
            aggiornaDettagliGiornata(dataSelezionataAttuale);
            ricaricaEventiCalendario();
        } else {
            Toast.makeText(this, "Errore durante il salvataggio.", Toast.LENGTH_SHORT).show();
        }
    }

    // btnLogout
    public void actionLogout(View v) {
        new AlertDialog.Builder(this)
                .setTitle("Attenzione")
                .setMessage("Vuoi davvero effettuare il logout?")
                .setPositiveButton("Esegui", (dialog, which) -> {
                    android.content.SharedPreferences.Editor editor = getSharedPreferences("AgentShiftsPrefs", MODE_PRIVATE).edit();
                    editor.clear();
                    editor.apply();
                    shiftSystem.logout();
                    startActivity(new android.content.Intent(this, LoginActivity.class));
                    finish();
                })
                .setNegativeButton("Annulla", null)
                .show();
    }

    // btnTema
    public void actionToggleTheme(View v) {
        SharedPreferences prefs = getSharedPreferences("AgentShiftsPrefs", MODE_PRIVATE);
        boolean isDarkMode = prefs.getBoolean("isDarkMode", false);

        String messaggio = isDarkMode ? "Vuoi passare al Tema Chiaro?" : "Vuoi passare al Tema Scuro?";

        new AlertDialog.Builder(this)
                .setTitle("Cambio Tema")
                .setMessage(messaggio)
                .setPositiveButton("Conferma", (dialog, which) -> {
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putBoolean("isDarkMode", !isDarkMode);
                    editor.apply();
                    if (!isDarkMode)
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                    else
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                })
                .setNegativeButton("Annulla", null)
                .show();
    }

    // btnStipendio
    public void actionShowSalary(View v) {
        float stipendio = shiftSystem.calcolaStipendioMeseCorrente();

        Calendar cal = Calendar.getInstance();
        String[] mesi = {"Gennaio", "Febbraio", "Marzo", "Aprile", "Maggio", "Giugno", "Luglio", "Agosto", "Settembre", "Ottobre", "Novembre", "Dicembre"};
        String meseCorrente = mesi[cal.get(Calendar.MONTH)];

        String messaggio = String.format(Locale.ITALY, "La stima del tuo stipendio lordo per i turni di %s è di:\n\n€ %.2f", meseCorrente, stipendio);

        new AlertDialog.Builder(this)
                .setTitle("Stima Stipendio")
                .setMessage(messaggio)
                .setPositiveButton("Chiudi", null)
                .show();
    }

    // btnWeekly
    public void actionShowWeeklySchedule(View v) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(dataSelezionataAttuale);

        cal.setFirstDayOfWeek(Calendar.MONDAY);
        cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);

        StringBuilder sb = new StringBuilder();
        String[] giorni = {"", "DOM", "LUN", "MAR", "MER", "GIO", "VEN", "SAB"};
        String[] mesi = {"JAN", "FEB", "MAR", "APR", "MAY", "JUN", "JUL", "AUG", "SEP", "OCT", "NOV", "DEC"};

        for (int i = 0; i < 7; i++) {
            Date date = cal.getTime();
            WorkingDay wd = shiftSystem.getGiornataLavorativa(date);

            String dayName = giorni[cal.get(Calendar.DAY_OF_WEEK)];
            int dayOfMonth = cal.get(Calendar.DAY_OF_MONTH);
            String monthName = mesi[cal.get(Calendar.MONTH)];

            String dayFormatted = String.format(java.util.Locale.getDefault(), "%02d", dayOfMonth);

            sb.append(dayName).append(" ").append(dayFormatted).append(" ").append(monthName).append(": ");

            if (wd == null)
                sb.append("");
            else {
                List<Shift> turni = wd.getTurni();
                boolean haTurni = (turni != null && !turni.isEmpty());

                if (wd.isRiposo() && haTurni) sb.append("M.RIPOSO ");
                else if (wd.isRiposo() && !haTurni) sb.append("RIPOSO");

                if (haTurni) {
                    for (int j = 0; j < turni.size(); j++) {
                        sb.append(turni.get(j).getInizioTurno()).append("/").append(turni.get(j).getFineTurno());
                        if (j < turni.size() - 1) sb.append(" e "); // Es: 08:00/12:00 e 15:00/19:00
                    }
                }
            }

            sb.append("\n");

            cal.add(Calendar.DAY_OF_MONTH, 1);
        }

        new AlertDialog.Builder(this)
                .setTitle("Turni della Settimana")
                .setMessage(sb.toString())
                .setPositiveButton("Copia Testo", (dialog, which) -> {
                    android.content.ClipboardManager clipboard = (android.content.ClipboardManager) getSystemService(android.content.Context.CLIPBOARD_SERVICE);
                    android.content.ClipData clip = android.content.ClipData.newPlainText("Turni", sb.toString());
                    clipboard.setPrimaryClip(clip);
                    Toast.makeText(this, "Turni copiati!", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Chiudi", null)
                .show();
    }

    // Minuti in formato leggibile (es. "30 min", "4 h", "1 h 30 min")
    private String formattaMinuti(int minutiTotali) {
        if (minutiTotali < 60) {
            return minutiTotali + " min";
        }
        int ore = minutiTotali / 60;
        int minutiRestanti = minutiTotali % 60;

        if (minutiRestanti == 0) {
            return ore + " h";
        } else {
            return ore + " h " + minutiRestanti + " min";
        }
    }

    // btnFerie
    public void actionImpostaFerie(View v) {
        new AlertDialog.Builder(this)
                .setTitle("Impostazione Ferie")
                .setMessage("Vuoi impostare le ferie solo per il giorno selezionato o per un periodo programmato?")
                .setPositiveButton("Giorno Singolo", (dialog, which) -> {
                    if (shiftSystem.impostaFerie(dataSelezionataAttuale)) {
                        Toast.makeText(this, "Ferie impostate!", Toast.LENGTH_SHORT).show();
                        aggiornaDettagliGiornata(dataSelezionataAttuale);
                        ricaricaEventiCalendario();
                    }
                })
                .setNegativeButton("Periodo", (dialog, which) -> mostraDialogFeriePeriodo())
                .setNeutralButton("Annulla", null)
                .show();
    }

    private void mostraDialogFeriePeriodo() {
        Calendar cal = Calendar.getInstance();
        DatePickerDialog startDialog = new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
            Calendar dataInizio = Calendar.getInstance();
            dataInizio.set(year, month, dayOfMonth);

            DatePickerDialog endDialog = new DatePickerDialog(this, (view2, year2, month2, dayOfMonth2) -> {
                Calendar dataFine = Calendar.getInstance();
                dataFine.set(year2, month2, dayOfMonth2);

                if (dataFine.before(dataInizio)) {
                    Toast.makeText(this, "Errore: la fine non può essere prima dell'inizio!", Toast.LENGTH_LONG).show();
                    return;
                }

                shiftSystem.impostaFeriePeriodo(dataInizio.getTime(), dataFine.getTime());
                Toast.makeText(this, "Ferie impostate!", Toast.LENGTH_SHORT).show();
                aggiornaDettagliGiornata(dataSelezionataAttuale);
                ricaricaEventiCalendario();

            }, year, month, dayOfMonth);
            endDialog.setTitle("Seleziona Data di FINE ferie");
            endDialog.show();

        }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));

        startDialog.setTitle("Seleziona Data di INIZIO ferie");
        startDialog.show();
    }

}