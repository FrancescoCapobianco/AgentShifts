package System.Service;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import System.Service.Objects.Shift;

public class StatsCalculator {

    public static float calcolaOre(String inizio, String fine) {
        try {
            String[] in = inizio.split(":");
            String[] out = fine.split(":");

            float hIn = Float.parseFloat(in[0]) + (Float.parseFloat(in[1]) / 60f);
            float hOut = Float.parseFloat(out[0]) + (Float.parseFloat(out[1]) / 60f);

            if (hOut < hIn) hOut += 24f;

            return hOut - hIn;
        } catch (Exception e) {
            return 0f;
        }
    }

    public static boolean isTurnoNotturno(String inizio, String fine) {
        try {
            int hIn = Integer.parseInt(inizio.split(":")[0]);
            int hOut = Integer.parseInt(fine.split(":")[0]);
            return (hIn >= 20 || hIn < 8 || hOut < 8 || (hOut < hIn));
        } catch (Exception e) {
            return false;
        }
    }

    public static float calcolaIncassoTurno(Shift turno, Date dataDelTurno) {
        float incassoTotale = 0f;
        float oreTurno = calcolaOre(turno.getInizioTurno(), turno.getFineTurno());

        boolean festivo = isFestivo(dataDelTurno);
        boolean notturno = isTurnoNotturno(turno.getInizioTurno(), turno.getFineTurno());

        // Calcolo Base Stipendio
        incassoTotale += oreTurno * turno.getPagaBaseApplicata();

        // Maggiorazioni (Applicate come extra per tutte le ore del turno per semplicità)
        if (festivo) incassoTotale += oreTurno * turno.getPagaFestivoApplicata();
        if (notturno) incassoTotale += oreTurno * turno.getPagaNotturnaApplicata();

        // Calcolo Extra (Allunghi e Straordinari)
        incassoTotale += (turno.getMinutiAllungo() / 60f) * turno.getPagaAllungoApplicata();
        incassoTotale += (turno.getMinutiStraordinario() / 60f) * turno.getPagaStraordinarioApplicata();

        return incassoTotale;
    }

    public static boolean isFestivo(Date data) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(data);

        int anno = cal.get(Calendar.YEAR);
        int mese = cal.get(Calendar.MONTH) + 1; // Calendar parte da 0
        int giorno = cal.get(Calendar.DAY_OF_MONTH);

        // Controllo Domenica
        if (cal.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) return true;

        // Festività Fisse Italiane
        if ((giorno == 1 && mese == 1) ||           // Capodanno
                (giorno == 6 && mese == 1) ||       // Epifania
                (giorno == 25 && mese == 4) ||      // Liberazione
                (giorno == 1 && mese == 5) ||       // Lavoro
                (giorno == 2 && mese == 6) ||       // Repubblica
                (giorno == 15 && mese == 8) ||      // Ferragosto
                (giorno == 4 && mese == 10) ||      // San Francesco (dal 2026)
                (giorno == 1 && mese == 11) ||      // Tutti i Santi
                (giorno == 8 && mese == 12) ||      // Immacolata
                (giorno == 25 && mese == 12) ||     // Natale
                (giorno == 26 && mese == 12)) {     // Santo Stefano
            return true;
        }

        return isPasquaOPasquetta(anno, mese, giorno);

    }

    // Algoritmo di Gauss per Stabilire il giorno e il mese per ogni anno.
    private static boolean isPasquaOPasquetta(int anno, int mese, int giorno) {
        int a = anno % 19, b = anno / 100, c = anno % 100;
        int d = b / 4, e = b % 4, f = (b + 8) / 25;
        int g = (b - f + 1) / 3, h = (19 * a + b - d - g + 15) % 30;
        int i = c / 4, k = c % 4, l = (32 + 2 * e + 2 * i - h - k) % 7;
        int m = (a + 11 * h + 22 * l) / 451;

        int mesePasqua = (h + l - 7 * m + 114) / 31;
        int giornoPasqua = ((h + l - 7 * m + 114) % 31) + 1;

        int mesePasquetta = mesePasqua;
        int giornoPasquetta = giornoPasqua + 1;

        if ((mesePasqua == 3 && giornoPasquetta > 31) || (mesePasqua == 4 && giornoPasquetta > 30)) {
            giornoPasquetta = 1;
            mesePasquetta++;
        }

        return (mese == mesePasqua && giorno == giornoPasqua) ||
                (mese == mesePasquetta && giorno == giornoPasquetta);
    }

}