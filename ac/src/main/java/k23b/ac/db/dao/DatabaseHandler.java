package k23b.ac.db.dao;

import java.util.concurrent.atomic.AtomicInteger;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseHandler extends SQLiteOpenHelper {

    private static DatabaseHandler instance;
    private SQLiteDatabase db;
    private static AtomicInteger dbOpenCounter;

    private static Context context;

    public static void setContext(Context context) {
        DatabaseHandler.context = context;
    }

    public static synchronized DatabaseHandler getDBHandler() {

        if (instance == null) {
            instance = new DatabaseHandler(context);
            dbOpenCounter = new AtomicInteger(0);
        }

        return instance;
    }

    public synchronized SQLiteDatabase openDatabase() throws SQLiteException {

        if (dbOpenCounter.incrementAndGet() == 1) {
            // Opening new database
            Log.d(DatabaseHandler.class.getName(), "Opening DB");
            try {
                db = instance.getWritableDatabase();
            } catch (SQLiteException e) {
                Log.e(DatabaseHandler.class.getName(), e.getMessage());
                throw new SQLiteException("Error while opening DB");
            }
        }
        return db;
    }

    public synchronized void closeDatabase() {

        if (dbOpenCounter.decrementAndGet() == 0) {
            Log.d(DatabaseHandler.class.getName(), "Closing DB");
            // Closing database
            db.close();
        }
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        Log.d(DatabaseHandler.class.getName(), "Creating the DB tables");
        // Creating required tables
        db.execSQL(USERS_TABLE_CREATE);
        db.execSQL(JOBS_TABLE_CREATE);
        Log.d(DatabaseHandler.class.getName(), "DB tables Created");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        Log.d(DatabaseHandler.class.getName(), "Upgrading from " + oldVersion + " to " + newVersion);
        // on upgrade drop older tables
        db.execSQL("DROP TABLE IF EXISTS " + USERS_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + JOBS_TABLE);

        // create new tables
        onCreate(db);
        Log.d(DatabaseHandler.class.getName(), "Upgrading done");
    }

    public void dropTables(SQLiteDatabase db) {

        Log.d(DatabaseHandler.class.getName(), "Dropping Tables");

        db.execSQL("DROP TABLE IF EXISTS " + USERS_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + JOBS_TABLE);

        Log.d(DatabaseHandler.class.getName(), "Tables Dropped");

    }

    private DatabaseHandler(Context context) {

        super(context.getApplicationContext(), DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "acdb";

    // Table Names
    private static final String USERS_TABLE = "acdb_users";
    private static final String JOBS_TABLE = "acdb_jobs";

    // Users Table Columns names
    private static final String KEY_U_USERNAME = "USERNAME";
    private static final String KEY_U_PASSWORD = "PASSWORD";
    private static final String KEY_U_ACTIVE = "ACTIVE";

    // Jobs Table Columns names
    private static final String KEY_J_ID = "ID";
    private static final String KEY_J_PARAMETERS = "PARAMETERS";
    private static final String KEY_J_USERNAME = "USERNAME";
    private static final String KEY_J_AGENT_ID = "AGENT_ID";
    private static final String KEY_J_TIME_ASSIGNED = "TIME_ASSIGNED";
    private static final String KEY_J_PERIODIC = "PERIODIC";
    private static final String KEY_J_PERIOD = "PERIOD";

    // Create Queries
    private static final String USERS_TABLE_CREATE = "CREATE TABLE IF NOT EXISTS " + USERS_TABLE + "(" + KEY_U_USERNAME
            + " TEXT PRIMARY KEY NOT NULL," + KEY_U_PASSWORD + " TEXT NOT NULL," + KEY_U_ACTIVE + " INTEGER NOT NULL" + ")";

    private static final String JOBS_TABLE_CREATE = "CREATE TABLE IF NOT EXISTS " + JOBS_TABLE + "(" + KEY_J_ID
            + " INTEGER PRIMARY KEY NOT NULL," + KEY_J_PARAMETERS + " TEXT NOT NULL," + KEY_J_USERNAME
            + " TEXT NOT NULL," + KEY_J_AGENT_ID + " INTEGER NOT NULL," + KEY_J_TIME_ASSIGNED + " TEXT NOT NULL,"
            + KEY_J_PERIODIC + " INTEGER NOT NULL," + KEY_J_PERIOD + " INTEGER," + " FOREIGN KEY (" + KEY_J_USERNAME
            + ") REFERENCES " + USERS_TABLE + " (" + KEY_U_USERNAME + "))";

    public static String getKeyUUsername() {
        return KEY_U_USERNAME;
    }

    public static String getKeyUPassword() {
        return KEY_U_PASSWORD;
    }

    public static String getKeyUActive() {
        return KEY_U_ACTIVE;
    }

    public static String getKeyJId() {
        return KEY_J_ID;
    }

    public static String getKeyJParameters() {
        return KEY_J_PARAMETERS;
    }

    public static String getKeyJUsername() {
        return KEY_J_USERNAME;
    }

    public static String getKeyJAgent_ID() {
        return KEY_J_AGENT_ID;
    }

    public static String getKeyJTimeAssigned() {
        return KEY_J_TIME_ASSIGNED;
    }

    public static String getKeyJPeriodic() {
        return KEY_J_PERIODIC;
    }

    public static String getKeyJPeriod() {
        return KEY_J_PERIOD;
    }

    public static String getUsersTable() {
        return USERS_TABLE;
    }

    public static String getJobsTable() {
        return JOBS_TABLE;
    }
}
