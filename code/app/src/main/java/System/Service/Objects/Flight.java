package System.Service.Objects;

public class Flight {

    // Attributi:

    private int id;                        // Per eventualmente eliminarlo facilmente.
    private int workingDayID;              // Foreign Key di WorkingDay nel DB.
    private String compagnia;
    private String luogoArrivo;
    private String luogoDestinazione;
    private String codiceVolo;              // Es. FR1033, U24098 ecc.
    private String logoDrawable;

    // Costruttori:
    public Flight() {}

    // COMPLETO (per il Database!)
    public Flight(final int id, final int wdID, final String comp, final String arr, final String dest,
                  final String codevolo, final String drawable) {
        this.id = id;
        this.workingDayID = wdID;
        this.compagnia = comp;
        this.luogoArrivo = arr;
        this.luogoDestinazione = dest;
        this.codiceVolo = codevolo;
        this.logoDrawable = drawable;
    }

    // No ID (per le Activity quando viene creato!)
    public Flight(final int wdID, final String comp, final String arr, final String dest,
                  final String codevolo, final String drawable) {
        this.workingDayID = wdID;
        this.compagnia = comp;
        this.luogoArrivo = arr;
        this.luogoDestinazione = dest;
        this.codiceVolo = codevolo;
        this.logoDrawable = drawable;
    }

    // Metodi: Getter & Setter
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getWorkingDayID() { return workingDayID; }
    public void setWorkingDayID(int workingDay) { this.workingDayID = workingDay; }

    public String getCompagnia() { return compagnia; }
    public void setCompagnia(String compagnia) { this.compagnia = compagnia; }

    public String getLuogoArrivo() { return luogoArrivo; }
    public void setLuogoArrivo(String luogoArrivo) { this.luogoArrivo = luogoArrivo; }

    public String getLuogoDestinazione() { return luogoDestinazione; }
    public void setLuogoDestinazione(String luogoDestinazione) { this.luogoDestinazione = luogoDestinazione; }

    public String getCodiceVolo() { return codiceVolo; }
    public void setCodiceVolo(String codiceVolo) { this.codiceVolo = codiceVolo; }

    public String getLogoDrawable() { return logoDrawable; }
    public void setLogoDrawable(String logoDrawable) { this.logoDrawable = logoDrawable; }

}