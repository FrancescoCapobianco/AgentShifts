package System.DAO;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import System.DAO.DatabaseHelper;
import java.util.Date;

import System.Service.Objects.User;

public class UserDB {

    public static final String TABLE_USER = "user";

    public static final String COLUMN_ID = "id";
    public static final String COLUMN_AZIENDA_ID = "aziendaID";                 // Foreign Key
    public static final String COLUMN_NOME = "nome";
    public static final String COLUMN_COGNOME = "cognome";
    public static final String COLUMN_DOB = "dataDiNascita";
    public static final String COLUMN_USERNAME = "username";
    public static final String COLUMN_DIGEST = "digestPassw";
    public static final String COLUMN_SALT = "saltPassw";
    public static final String COLUMN_P_BASE = "pagaBase";
    public static final String COLUMN_P_ALLUNGO = "pagaAllungo";
    public static final String COLUMN_P_STRAORD = "pagaStraordinari";
    public static final String COLUMN_P_NOTTURNA = "pagaNotturna";
    public static final String COLUMN_P_FESTIVO = "pagaFestivo";

    private SQLiteDatabase database;
    private DatabaseHelper dbHelper;

    public UserDB(Context context) {
        dbHelper = new DatabaseHelper(context);
    }

    public void open() {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    // --- OPERAZIONI CRUD ---

    // CREATE: Inserisce e restituisce il nuovo ID (o -1 se fallisce)
    public long insertUser(User user) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_AZIENDA_ID, user.getAziendaID());
        values.put(COLUMN_NOME, user.getNome());
        values.put(COLUMN_COGNOME, user.getCognome());

        if (user.getDataDiNascita() != null) {
            values.put(COLUMN_DOB, user.getDataDiNascita().getTime());
        }

        values.put(COLUMN_USERNAME, user.getUsername());
        values.put(COLUMN_DIGEST, user.getDigestPassw());
        values.put(COLUMN_SALT, user.getSaltPassw());
        values.put(COLUMN_P_BASE, user.getPagaBase());
        values.put(COLUMN_P_ALLUNGO, user.getPagaAllungo());
        values.put(COLUMN_P_STRAORD, user.getPagaStraordinari());
        values.put(COLUMN_P_NOTTURNA, user.getPagaNotturna());
        values.put(COLUMN_P_FESTIVO, user.getPagaFestivo());

        // nullColumnHack : restituisce l'id.
        return database.insert(TABLE_USER, null, values);

    }

    // READ: Ricerca in base all'username:
    public User getUserByUsername(String username) {
        User user = null;

        Cursor cursor = database.query(TABLE_USER, null,
                COLUMN_USERNAME + " = ?",
                new String[]{username},
                null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            user = cursorToUser(cursor);
            cursor.close();
        }
        return user;
    }

    // Trasforma la riga del DB (Cursor) in un oggetto User Java
    private User cursorToUser(Cursor cursor) {
        User user = new User();
        user.setId(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)));
        user.setAziendaID(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_AZIENDA_ID)));
        user.setNome(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NOME)));
        user.setCognome(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_COGNOME)));

        // Riconvertiamo i millisecondi in un oggetto Date
        long dobMillis = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_DOB));
        user.setDataDiNascita(new Date(dobMillis));

        user.setUsername(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USERNAME)));
        user.setDigestPassw(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DIGEST)));
        user.setSaltPassw(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_SALT)));
        user.setPagaBase(cursor.getFloat(cursor.getColumnIndexOrThrow(COLUMN_P_BASE)));
        user.setPagaAllungo(cursor.getFloat(cursor.getColumnIndexOrThrow(COLUMN_P_ALLUNGO)));
        user.setPagaStraordinari(cursor.getFloat(cursor.getColumnIndexOrThrow(COLUMN_P_STRAORD)));
        user.setPagaNotturna(cursor.getFloat(cursor.getColumnIndexOrThrow(COLUMN_P_NOTTURNA)));
        user.setPagaFestivo(cursor.getFloat(cursor.getColumnIndexOrThrow(COLUMN_P_FESTIVO)));

        return user;
    }
}