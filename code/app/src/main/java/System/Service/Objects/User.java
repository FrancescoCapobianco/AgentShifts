package System.Service.Objects;

import java.util.Date;

public class User {

    // Attributi:

    private int id;
    private int aziendaID;            // Foreign Key di Azienda nel DB
    private String nome;
    private String cognome;
    private Date dataDiNascita;
    private String username;
    private String digestPassw;       // Digest Finale
    private String saltPassw;         // Salt per ogni Utente
    private float pagaBase;           // tot Euro all'ora da Contratto
    private float pagaAllungo;        // tot Euro x Contratto e/o Leggi Italiane
    private float pagaStraordinari;
    private float pagaNotturna;
    private float pagaFestivo;

    // Costruttori:
    public User() {}

    // Completo (per il Database!)
    public User(final int id, final int aziendaID, final String nome, final String cognome,
                final Date dDn, final String usn, final String dP, final String sP,
                final float pB, final float pA, final float pS, final float pN, final float pF) {
        this.id = id;
        this.aziendaID = aziendaID;
        this.nome = nome;
        this.cognome = cognome;
        this.dataDiNascita = dDn;
        this.username = usn;
        this.digestPassw = dP;
        this.saltPassw = sP;
        this.pagaBase = pB;
        this.pagaAllungo = pA;
        this.pagaStraordinari = pS;
        this.pagaNotturna = pN;
        this.pagaFestivo = pF;
    }

    // NO ID (per le Activity quando viene creato!)
    public User(final String nome, final String cognome, final Date dataDiNascita,
                final String username, final String digestPassw, final String saltPassw, final float pagaBase,
                final float pagaAllungo, final float pagaStraordinari, final float pagaNotturna, final float pagaFestivo) {
        this.nome = nome;
        this.cognome = cognome;
        this.dataDiNascita = dataDiNascita;
        this.username = username;
        this.digestPassw = digestPassw;
        this.saltPassw = saltPassw;
        this.pagaBase = pagaBase;
        this.pagaAllungo = pagaAllungo;
        this.pagaStraordinari = pagaStraordinari;
        this.pagaNotturna = pagaNotturna;
        this.pagaFestivo = pagaFestivo;
    }


    // Metodi: Getter & Setter
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getAziendaID() { return aziendaID; }
    public void setAziendaID(int aziendaID) { this.aziendaID = aziendaID; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getCognome() { return cognome; }
    public void setCognome(String cognome) { this.cognome = cognome; }

    public Date getDataDiNascita() { return dataDiNascita; }
    public void setDataDiNascita(Date dataDiNascita) { this.dataDiNascita = dataDiNascita; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getDigestPassw() { return digestPassw; }
    public void setDigestPassw(String digestPassw) { this.digestPassw = digestPassw; }

    public String getSaltPassw() { return saltPassw; }
    public void setSaltPassw(String saltPassw) { this.saltPassw = saltPassw; }

    public float getPagaBase() { return pagaBase; }
    public void setPagaBase(float pagaBase) { this.pagaBase = pagaBase; }

    public float getPagaAllungo() { return pagaAllungo; }
    public void setPagaAllungo(float pagaAllungo) { this.pagaAllungo = pagaAllungo; }

    public float getPagaStraordinari() { return pagaStraordinari; }
    public void setPagaStraordinari(float pagaStraordinari) { this.pagaStraordinari = pagaStraordinari; }

    public float getPagaNotturna() { return pagaNotturna; }
    public void setPagaNotturna(float pagaNotturna) { this.pagaNotturna = pagaNotturna; }

    public float getPagaFestivo() { return pagaFestivo; }
    public void setPagaFestivo(float pagaFestivo) { this.pagaFestivo = pagaFestivo; }

}