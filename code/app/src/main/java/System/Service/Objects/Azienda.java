package System.Service.Objects;

// Classe Pre-Inizializzata da ShiftSystem per il DB (non cancellabili)
public class Azienda {

    // Attributi:
    private final int id;
    private final String nome;
    private final String logoDrawable;

    // Costruttori:

    // Completo (per il Database!)
    public Azienda(final int id, final String nome, final String logo) {
        this.id = id;
        this.nome = nome;
        this.logoDrawable = logo;
    }

    // Metodi: Getter
    public int getId() { return id; }
    public String getNome() { return nome; }
    public String getLogoDrawable() { return logoDrawable; }

}