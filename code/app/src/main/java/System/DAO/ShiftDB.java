package System.DAO;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import java.util.List;
import java.util.ArrayList;

import System.DAO.DatabaseHelper;
import System.Service.Objects.Shift;

public class ShiftDB {

    public static final String TABLE_SHIFT = "shift";

    public static final String COLUMN_ID = "id";
    public static final String COLUMN_WORKINGDAY_ID = "workingDayID";               // Foreign Key
    public static final String COLUMN_INIZIO = "inizioTurno";
    public static final String COLUMN_FINE = "fineTurno";
    public static final String COLUMN_MIN_ALLUNGO = "minutiAllungo";
    public static final String COLUMN_MIN_STRAORD = "minutiStraordinario";
    public static final String COLUMN_P_BASE_APP = "pagaBaseApplicata";
    public static final String COLUMN_P_NOTTURNA_APP = "pagaNotturnaApplicata";
    public static final String COLUMN_P_FESTIVO_APP = "pagaFestivoApplicata";

    private SQLiteDatabase database;
    private DatabaseHelper dbHelper;

    public ShiftDB(Context context) {
        dbHelper = new DatabaseHelper(context);
    }

    public void open() {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    // --- OPERAZIONI CRUD ---

    // CREATE: Nuovo turno nel DB
    public long insertShift(Shift shift) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_WORKINGDAY_ID, shift.getWorkingDayID());
        values.put(COLUMN_INIZIO, shift.getInizioTurno());
        values.put(COLUMN_FINE, shift.getFineTurno());
        values.put(COLUMN_MIN_ALLUNGO, shift.getMinutiAllungo());
        values.put(COLUMN_MIN_STRAORD, shift.getMinutiStraordinario());
        values.put(COLUMN_P_BASE_APP, shift.getPagaBaseApplicata());
        values.put(COLUMN_P_NOTTURNA_APP, shift.getPagaNotturnaApplicata());
        values.put(COLUMN_P_FESTIVO_APP, shift.getPagaFestivoApplicata());

        return database.insert(TABLE_SHIFT, null, values);
    }

    // READ: Tutti i Turni associati a una specifica giornata lavorativa
    public List<Shift> getShiftsByWorkingDay(int workingDayID) {
        List<Shift> listaTurni = new ArrayList<>();

        Cursor cursor = database.query(TABLE_SHIFT, null,
                COLUMN_WORKINGDAY_ID + " = ?",
                new String[]{String.valueOf(workingDayID)},
                null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            do {
                listaTurni.add(cursorToShift(cursor));
            } while (cursor.moveToNext());
            cursor.close();
        }
        return listaTurni;
    }

    // UPDATE: Aggiorna un turno esistente nel DB
    public int updateShift(Shift shift) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_INIZIO, shift.getInizioTurno());
        values.put(COLUMN_FINE, shift.getFineTurno());
        values.put(COLUMN_MIN_ALLUNGO, shift.getMinutiAllungo());
        values.put(COLUMN_MIN_STRAORD, shift.getMinutiStraordinario());

        return database.update(TABLE_SHIFT, values, COLUMN_ID + " = ?", new String[]{String.valueOf(shift.getId())});
    }

    // DELETE: Cancella un turno
    public void deleteShift(int shiftID) {
        database.delete(TABLE_SHIFT, COLUMN_ID + " = ?", new String[]{String.valueOf(shiftID)});
    }

    // Converte il Cursor in un oggetto Shift
    private Shift cursorToShift(Cursor cursor) {
        Shift shift = new Shift();
        shift.setId(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)));
        shift.setWorkingDayID(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_WORKINGDAY_ID)));
        shift.setInizioTurno(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_INIZIO)));
        shift.setFineTurno(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_FINE)));
        shift.setMinutiAllungo(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_MIN_ALLUNGO)));
        shift.setMinutiStraordinario(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_MIN_STRAORD)));
        shift.setPagaBaseApplicata(cursor.getFloat(cursor.getColumnIndexOrThrow(COLUMN_P_BASE_APP)));
        shift.setPagaNotturnaApplicata(cursor.getFloat(cursor.getColumnIndexOrThrow(COLUMN_P_NOTTURNA_APP)));
        shift.setPagaFestivoApplicata(cursor.getFloat(cursor.getColumnIndexOrThrow(COLUMN_P_FESTIVO_APP)));
        return shift;
    }

}