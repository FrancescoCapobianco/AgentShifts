package System.DAO;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.Date;

import System.DAO.DatabaseHelper;
import System.Service.Objects.WorkingDay;

public class WorkingDayDB {

    public static final String TABLE_WORKING_DAY = "workingday";

    public static final String COLUMN_ID = "id";
    public static final String COLUMN_DATA = "data";
    public static final String COLUMN_USER_ID = "userID";                       // Foreign Key
    public static final String COLUMN_IS_RIPOSO = "isRiposo";                   // Flag per Riposo Giornaliero

    private SQLiteDatabase database;
    private DatabaseHelper dbHelper;

    public WorkingDayDB(Context context) {
        dbHelper = new DatabaseHelper(context);
    }

    public void open() {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    // --- OPERAZIONI CRUD ---

    // CREATE: Nuova giornata lavorativa nel DB
    public long insertWorkingDay(WorkingDay workingDay) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_IS_RIPOSO, workingDay.isRiposo() ? 1 : 0);

        if (workingDay.getData() != null) {
            values.put(COLUMN_DATA, workingDay.getData().getTime());
        }
        values.put(COLUMN_USER_ID, workingDay.getUserID());

        return database.insert(TABLE_WORKING_DAY, null, values);
    }

    // READ 1: Giornata specifica tramite il suo ID
    public WorkingDay getWorkingDayById(int id) {
        WorkingDay workingDay = null;

        Cursor cursor = database.query(TABLE_WORKING_DAY, null,
                COLUMN_ID + " = ?",
                new String[]{String.valueOf(id)},
                null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            workingDay = cursorToWorkingDay(cursor);
            cursor.close();
        }
        return workingDay;
    }

    // READ 2: Giornata di un utente per una SPECIFICA DATA (x UI)
    public WorkingDay getWorkingDayByDateAndUser(Date data, int userID) {
        WorkingDay workingDay = null;

        long dataInMillis = data.getTime();

        Cursor cursor = database.query(TABLE_WORKING_DAY, null,
                COLUMN_DATA + " = ? AND " + COLUMN_USER_ID + " = ?",
                new String[]{String.valueOf(dataInMillis), String.valueOf(userID)},
                null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            workingDay = cursorToWorkingDay(cursor);
            cursor.close();
        }
        return workingDay;
    }

    // DELETE: Cancella un'intera giornata lavorativa
    // ATTENZIONE: Questo cancellerà SOLO la riga in "workingday". I voli e i turni associati
    // dovranno essere cancellati manualmente da ShiftSystem prima di chiamare questo metodo.
    public void deleteWorkingDay(int id) {
        database.delete(TABLE_WORKING_DAY, COLUMN_ID + " = ?", new String[]{String.valueOf(id)});
    }

    // Converte la riga del Cursor in un oggetto WorkingDay
    private WorkingDay cursorToWorkingDay(Cursor cursor) {

        int id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID));
        long dataMillis = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_DATA));
        int userID = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_USER_ID));
        int riposoInt = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_IS_RIPOSO));
        boolean isRiposo = (riposoInt == 1);

        Date dateObj = new Date(dataMillis);
        WorkingDay workingDay = new WorkingDay(id, dateObj, userID, isRiposo);

        return workingDay;
    }
}
