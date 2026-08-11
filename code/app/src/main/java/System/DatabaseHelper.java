package System.DAO;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "AgentShifts.db";
    private static final int DATABASE_VERSION = 1;

    // Tabella USER
    private static final String CREATE_TABLE_USER =
            "CREATE TABLE " + UserDB.TABLE_USER + " (" +
                    UserDB.COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    UserDB.COLUMN_AZIENDA_ID + " INTEGER, " +
                    UserDB.COLUMN_NOME + " TEXT, " +
                    UserDB.COLUMN_COGNOME + " TEXT, " +
                    UserDB.COLUMN_DOB + " INTEGER, " +
                    UserDB.COLUMN_USERNAME + " TEXT UNIQUE, " +
                    UserDB.COLUMN_DIGEST + " TEXT, " +
                    UserDB.COLUMN_SALT + " TEXT, " +
                    UserDB.COLUMN_P_BASE + " REAL, " +
                    UserDB.COLUMN_P_ALLUNGO + " REAL, " +
                    UserDB.COLUMN_P_STRAORD + " REAL, " +
                    UserDB.COLUMN_P_NOTTURNA + " REAL, " +
                    UserDB.COLUMN_P_FESTIVO + " REAL);";

    // Tabella WORKING_DAY
    private static final String CREATE_TABLE_WORKING_DAY =
            "CREATE TABLE " + WorkingDayDB.TABLE_WORKING_DAY + " (" +
                    WorkingDayDB.COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    WorkingDayDB.COLUMN_DATA + " INTEGER, " +
                    WorkingDayDB.COLUMN_USER_ID + " INTEGER, " +
                    WorkingDayDB.COLUMN_IS_RIPOSO + " INTEGER DEFAULT 0, " +
                    "FOREIGN KEY(" + WorkingDayDB.COLUMN_USER_ID + ") REFERENCES " +
                    UserDB.TABLE_USER + "(" + UserDB.COLUMN_ID + ") ON DELETE CASCADE);";

    // Tabella SHIFT
    private static final String CREATE_TABLE_SHIFT =
            "CREATE TABLE " + ShiftDB.TABLE_SHIFT + " (" +
                    ShiftDB.COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    ShiftDB.COLUMN_WORKINGDAY_ID + " INTEGER, " +
                    ShiftDB.COLUMN_INIZIO + " TEXT, " +
                    ShiftDB.COLUMN_FINE + " TEXT, " +
                    ShiftDB.COLUMN_MIN_ALLUNGO + " INTEGER, " +
                    ShiftDB.COLUMN_MIN_STRAORD + " INTEGER, " +
                    ShiftDB.COLUMN_P_BASE_APP + " REAL, " +
                    ShiftDB.COLUMN_P_NOTTURNA_APP + " REAL, " +
                    ShiftDB.COLUMN_P_FESTIVO_APP + " REAL, " +
                    "FOREIGN KEY(" + ShiftDB.COLUMN_WORKINGDAY_ID + ") REFERENCES " +
                    WorkingDayDB.TABLE_WORKING_DAY + "(" + WorkingDayDB.COLUMN_ID + ") ON DELETE CASCADE);";

    // Tabella FLIGHT
    private static final String CREATE_TABLE_FLIGHT =
            "CREATE TABLE " + FlightDB.TABLE_FLIGHT + " (" +
                    FlightDB.COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    FlightDB.COLUMN_WORKINGDAY_ID + " INTEGER, " +
                    FlightDB.COLUMN_COMPAGNIA + " TEXT, " +
                    FlightDB.COLUMN_ARRIVO + " TEXT, " +
                    FlightDB.COLUMN_DESTINAZIONE + " TEXT, " +
                    FlightDB.COLUMN_CODICE + " TEXT, " +
                    FlightDB.COLUMN_LOGO + " TEXT, " +
                    "FOREIGN KEY(" + FlightDB.COLUMN_WORKINGDAY_ID + ") REFERENCES " +
                    WorkingDayDB.TABLE_WORKING_DAY + "(" + WorkingDayDB.COLUMN_ID + ") ON DELETE CASCADE);";


    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // SOLO quando l'app viene installata o il DB non esiste:
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_USER);
        db.execSQL(CREATE_TABLE_WORKING_DAY);
        db.execSQL(CREATE_TABLE_SHIFT);
        db.execSQL(CREATE_TABLE_FLIGHT);
    }

    // Se cambia DATABASE_VERSION:
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        switch (oldVersion) {
            case 1:
                // aggiornamenti futuri
            case 2:
                // Per futuri passaggi dalla v2 alla v3...
                // Niente "break" alla fine dei case, così gli aggiornamenti avvengono a cascata.
        }
    }

    // Delete on Cascade:
    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        db.setForeignKeyConstraintsEnabled(true);
    }
}