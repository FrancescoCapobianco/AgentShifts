package System.DAO;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import System.DAO.DatabaseHelper;
import System.Service.Objects.Flight;

public class FlightDB {

    public static final String TABLE_FLIGHT = "flight";

    public static final String COLUMN_ID = "id";
    public static final String COLUMN_WORKINGDAY_ID = "workingDayID";               // Foreign Key
    public static final String COLUMN_COMPAGNIA = "compagnia";
    public static final String COLUMN_ARRIVO = "luogoArrivo";
    public static final String COLUMN_DESTINAZIONE = "luogoDestinazione";
    public static final String COLUMN_CODICE = "codiceVolo";
    public static final String COLUMN_LOGO = "logoDrawable";

    private SQLiteDatabase database;
    private DatabaseHelper dbHelper;

    public FlightDB(Context context) {
        dbHelper = new DatabaseHelper(context);
    }

    public void open() {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    // --- OPERAZIONI CRUD ---

    // CREATE: Nuovo volo nel DB
    public long insertFlight(Flight flight) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_WORKINGDAY_ID, flight.getWorkingDayID());
        values.put(COLUMN_COMPAGNIA, flight.getCompagnia());
        values.put(COLUMN_ARRIVO, flight.getLuogoArrivo());
        values.put(COLUMN_DESTINAZIONE, flight.getLuogoDestinazione());
        values.put(COLUMN_CODICE, flight.getCodiceVolo());
        values.put(COLUMN_LOGO, flight.getLogoDrawable());

        return database.insert(TABLE_FLIGHT, null, values);
    }

    // READ: TUTTI i voli associati a una specifica giornata lavorativa
    public List<Flight> getFlightsByWorkingDay(int workingDayID) {
        List<Flight> voliGiornalieri = new ArrayList<>();

        Cursor cursor = database.query(TABLE_FLIGHT, null,
                COLUMN_WORKINGDAY_ID + " = ?",
                new String[]{String.valueOf(workingDayID)},
                null, null, null);

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    Flight volo = cursorToFlight(cursor);
                    voliGiornalieri.add(volo);
                } while (cursor.moveToNext());
            }
            cursor.close();
        }

        return voliGiornalieri;
    }

    // DELETE: Cancella un volo
    public void deleteFlight(int flightID) {
        database.delete(TABLE_FLIGHT, COLUMN_ID + " = ?", new String[]{String.valueOf(flightID)});
    }

    // Converte la riga attuale del Cursor in un oggetto Flight
    private Flight cursorToFlight(Cursor cursor) {
        Flight flight = new Flight();
        flight.setId(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)));
        flight.setWorkingDayID(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_WORKINGDAY_ID)));
        flight.setCompagnia(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_COMPAGNIA)));
        flight.setLuogoArrivo(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ARRIVO)));
        flight.setLuogoDestinazione(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DESTINAZIONE)));
        flight.setCodiceVolo(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CODICE)));
        flight.setLogoDrawable(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_LOGO)));
        return flight;
    }
}