package System.Service.Objects;

public class Shift {

    // Attributi:

    private int id;
    private int workingDayID;                           // Foreign Key di WorkingDay nel DB.
    private String inizioTurno;
    private String fineTurno;
    private int minutiAllungo;
    private int minutiStraordinario;
    private float pagaBaseApplicata;                    // Prelevati dall'info dello User
    private float pagaAllungoApplicata;                 // dalle 4 ore fino alle 8 ore...
    private float pagaStraordinarioApplicata;           // Dalle 8 ore in poi...
    private float pagaNotturnaApplicata;                // Dalle 20:00 alle 08:00
    private float pagaFestivoApplicata;                 // Domenica e Feste Italiane

    // Costruttori:
    public Shift() {}

    // COMPLETO (per il database!)
    public Shift(final int id, final int workingDayID, final String it, final String ft,
                 final int mA, final int mS, final float pBa, final float pAa,
                 final float pNa, final float pSa, final float pFa) {
        this.id = id;
        this.workingDayID = workingDayID;
        this.inizioTurno = it;
        this.fineTurno = ft;
        this.minutiAllungo = mA;
        this.minutiStraordinario = mS;
        this.pagaBaseApplicata = pBa;                               // Assegnati poi da ShiftSystem
        this.pagaAllungoApplicata = pAa;
        this.pagaNotturnaApplicata = pNa;
        this.pagaStraordinarioApplicata = pSa;
        this.pagaFestivoApplicata = pFa;
    }

    // No ID (per le Activity quando viene creato!)
    public Shift(final int workingDayID, final String it, final String ft, final int mA, final int mS,
                 final float pBa, final float pAa,
                 final float pNa, final float pSa, final float pFa) {
        this.workingDayID = workingDayID;
        this.inizioTurno = it;
        this.fineTurno = ft;
        this.minutiAllungo = mA;
        this.minutiStraordinario = mS;
        this.pagaBaseApplicata = pBa;                               // Assegnati poi da ShiftSystem
        this.pagaAllungoApplicata = pAa;
        this.pagaNotturnaApplicata = pNa;
        this.pagaStraordinarioApplicata = pSa;
        this.pagaFestivoApplicata = pFa;
    }

    // Metodi: Getter & Setter
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getWorkingDayID() { return workingDayID; }
    public void setWorkingDayID(int workingDayID) { this.workingDayID = workingDayID; }

    public String getInizioTurno() { return inizioTurno; }
    public void setInizioTurno(String inizioTurno) { this.inizioTurno = inizioTurno; }

    public String getFineTurno() { return fineTurno; }
    public void setFineTurno(String fineTurno) { this.fineTurno = fineTurno; }

    public int getMinutiAllungo() { return minutiAllungo; }
    public void setMinutiAllungo(int minutiAllungo) { this.minutiAllungo = minutiAllungo; }

    public int getMinutiStraordinario() { return minutiStraordinario; }
    public void setMinutiStraordinario(int minutiStraordinario) { this.minutiStraordinario = minutiStraordinario; }

    public float getPagaBaseApplicata() { return pagaBaseApplicata; }
    public void setPagaBaseApplicata(float pagaBaseApplicata) { this.pagaBaseApplicata = pagaBaseApplicata; }

    public float getPagaAllungoApplicata() { return pagaAllungoApplicata; }
    public void setPagaAllungoApplicata(float pagaAllungoApplicata) { this.pagaAllungoApplicata = pagaAllungoApplicata; }

    public float getPagaStraordinarioApplicata() { return pagaStraordinarioApplicata; }
    public void setPagaStraordinarioApplicata(float pagaStraordinarioApplicata) { this.pagaStraordinarioApplicata = pagaStraordinarioApplicata; }

    public float getPagaNotturnaApplicata() { return pagaNotturnaApplicata; }
    public void setPagaNotturnaApplicata(float pagaNotturnaApplicata) { this.pagaNotturnaApplicata = pagaNotturnaApplicata; }

    public float getPagaFestivoApplicata() { return pagaFestivoApplicata; }
    public void setPagaFestivoApplicata(float pagaFestivoApplicata) { this.pagaFestivoApplicata = pagaFestivoApplicata; }

}