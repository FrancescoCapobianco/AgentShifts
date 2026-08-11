package System.Service.Objects;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class WorkingDay {

    // Attributi:

    private int id;
    private final int userID;                      // Unique, Foreign Key di User nel DB
    private final Date data;                       // Unique
    private List<Shift> turni;                     // Possibile Turno: Normale, Rientri
    private boolean isRiposo;                      // Flag per il riposo giornaliero
    private List<Flight> voliGiornalieri;

    // Costruttori:

    // COMPLETO (per il Database)
    public WorkingDay(final int id, final Date data, final int userID, final boolean isRiposo) {
        this.id = id;
        this.data = data;
        this.userID = userID;
        this.isRiposo = isRiposo;
        this.turni = new ArrayList<>();
    }

    // NO ID (per le activity quando viene creato!)
    public WorkingDay(final Date data, final int userID, final boolean isRiposo) {
        this.data = data;
        this.userID = userID;
        this.isRiposo = isRiposo;
        this.turni = new ArrayList<>();
    }

    // Metodi: Getter & Setter
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public Date getData() { return data; }
    public int getUserID() { return userID; }

    public List<Shift> getTurni() { return turni; }
    public void setTurni(List<Shift> turni) { this.turni = turni; }

    public boolean isRiposo() { return isRiposo; }
    public void setRiposo(boolean riposo) { isRiposo = riposo; }

    public List<Flight> getVoliGiornalieri() { return voliGiornalieri; }
    public void setVoliGiornalieri(List<Flight> voliGiornalieri) { this.voliGiornalieri = voliGiornalieri; }
}