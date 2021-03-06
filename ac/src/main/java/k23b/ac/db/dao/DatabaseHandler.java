package k23b.ac.db.dao;

import java.util.concurrent.atomic.AtomicInteger;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * A singleton class extending SQLiteOpenHelper for the implementation of the application's Database.
 *
 */
public class DatabaseHandler extends SQLiteOpenHelper {

    private static DatabaseHandler instance;
    private SQLiteDatabase db;
    private static AtomicInteger dbOpenCounter;

    private static Context context;

    public static void setContext(Context context) {
        DatabaseHandler.context = context;
    }

    /**
     * Acquire the DatabaseHandler instance. Calls for the initialization of DatabaseHandler if it has not been yet initialized
     * 
     * @return The instance of the DatabaseHandler
     */
    public static synchronized DatabaseHandler getDBHandler() {

        if (instance == null) {
            instance = new DatabaseHandler(context);
            dbOpenCounter = new AtomicInteger(0);
        }

        return instance;
    }

    /**
     * Expresses the need for an open Database by the method who calls this.
     * 
     * @return The SQLiteDatabase instance of the DatabaseHandler
     * @throws SQLiteException
     */
    public synchronized SQLiteDatabase openDatabase() throws SQLiteException {

        if (dbOpenCounter.incrementAndGet() == 1) {
            // Opening new database
            // Log.d(DatabaseHandler.class.getName(), "Opening DB");
            try {
                db = instance.getWritableDatabase();
            } catch (SQLiteException e) {
                Log.e(DatabaseHandler.class.getName(), e.getMessage());
                throw new SQLiteException("Error while opening DB");
            }
        }
        return db;
    }

    /**
     * Releases the need for an open Database by the method who calls this.
     * 
     */
    public synchronized void closeDatabase() {

        if (dbOpenCounter.decrementAndGet() == 0) {
            // Log.d(DatabaseHandler.class.getName(), "Closing DB");
            // Closing database
            db.close();
        }
    }

    /**
     * Creates the Users, Jobs, Cached jobs and Cached Agents Tables.This method is invoked on the first openDatabase() call
     * 
     */
    @Override
    public void onCreate(SQLiteDatabase db) {

        Log.d(DatabaseHandler.class.getName(), "Creating the DB tables");
        // Creating required tables
        db.execSQL(USERS_TABLE_CREATE);
        db.execSQL(JOBS_TABLE_CREATE);
        db.execSQL(CACHED_AGENTS_TABLE_CREATE);
        db.execSQL(CACHED_JOBS_TABLE_CREATE);
        
        Log.d(DatabaseHandler.class.getName(), "DB tables Created");
    }

    /**
     * Updating sequence for the DatabaseHandler
     * 
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        Log.d(DatabaseHandler.class.getName(), "Upgrading from " + oldVersion + " to " + newVersion);
        // on upgrade drop older tables
        db.execSQL("DROP TABLE IF EXISTS " + USERS_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + JOBS_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + CACHED_AGENTS_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + CACHED_JOBS_TABLE);
        
        // create new tables
        onCreate(db);
        Log.d(DatabaseHandler.class.getName(), "Upgrading done");
    }

    /**
     * Drops the Tables of this Application
     * 
     * @param db The SQLiteDatabase instance of DatabaseHandler
     */
    public void dropTables(SQLiteDatabase db) {

        Log.d(DatabaseHandler.class.getName(), "Dropping Tables");

        db.execSQL("DROP TABLE IF EXISTS " + USERS_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + JOBS_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + CACHED_AGENTS_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + CACHED_JOBS_TABLE);

        Log.d(DatabaseHandler.class.getName(), "Tables Dropped");

    }

    private DatabaseHandler(Context context) {

        super(context.getApplicationContext(), DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Database Version
    private static final int DATABASE_VERSION = 2;

    // Database Name
    private static final String DATABASE_NAME = "acdb";

    // Table Names
    private static final String USERS_TABLE = "acdb_users";
    private static final String JOBS_TABLE = "acdb_jobs";
    private static final String CACHED_AGENTS_TABLE = "acdb_cached_agents";
    private static final String CACHED_JOBS_TABLE = "acdb_cached_jobs";

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

    // Cached Agents Table Columns names
    private static final String KEY_CA_AGENT_ID = "AGENT_ID";
    private static final String KEY_CA_AGENT_HASH = "AGENT_HASH";
    private static final String KEY_CA_TIME_ACCEPTED = "TIME_ACCEPTED";
    private static final String KEY_CA_TIME_JOB_REQUEST = "TIME_JOB_REQUEST";
    private static final String KEY_CA_TIME_TERMINATED = "TIME_TERMINATED";
    private static final String KEY_CA_AGENT_STATUS = "AGENT_STATUS";

    // Cached Jobs Table Columns names
    private static final String KEY_CJ_ID = "JOB_ID";
    private static final String KEY_CJ_AGENT_ID = "AGENT_ID";
    private static final String KEY_CJ_TIME_ASSIGNED = "TIME_ASSIGNED";
    private static final String KEY_CJ_TIME_SENT = "TIME_SENT";
    private static final String KEY_CJ_PARAMETERS = "PARAMETERS";
    private static final String KEY_CJ_PERIODIC = "PERIODIC";
    private static final String KEY_CJ_PERIOD = "PERIOD";
    private static final String KEY_CJ_JOB_STATUS = "JOB_STATUS";

    // Create Queries
    private static final String USERS_TABLE_CREATE = "CREATE TABLE IF NOT EXISTS " + USERS_TABLE + "(" + KEY_U_USERNAME
            + " TEXT PRIMARY KEY NOT NULL," + KEY_U_PASSWORD + " TEXT NOT NULL," + KEY_U_ACTIVE + " INTEGER NOT NULL" + ")";

    private static final String JOBS_TABLE_CREATE = "CREATE TABLE IF NOT EXISTS " + JOBS_TABLE + "(" + KEY_J_ID
            + " INTEGER PRIMARY KEY NOT NULL," + KEY_J_PARAMETERS + " TEXT," + KEY_J_USERNAME
            + " TEXT NOT NULL," + KEY_J_AGENT_ID + " INTEGER NOT NULL," + KEY_J_TIME_ASSIGNED + " TEXT NOT NULL,"
            + KEY_J_PERIODIC + " INTEGER NOT NULL," + KEY_J_PERIOD + " INTEGER," + " FOREIGN KEY (" + KEY_J_USERNAME
            + ") REFERENCES " + USERS_TABLE + " (" + KEY_U_USERNAME + "))";

    private static final String CACHED_AGENTS_TABLE_CREATE = "CREATE TABLE IF NOT EXISTS " + CACHED_AGENTS_TABLE + "(" + KEY_CA_AGENT_ID
            + " TEXT PRIMARY KEY NOT NULL," + KEY_CA_AGENT_HASH + " TEXT NOT NULL," + KEY_CA_TIME_ACCEPTED + " TEXT," + KEY_CA_TIME_JOB_REQUEST
            + " TEXT," + KEY_CA_TIME_TERMINATED + " TEXT," + KEY_CA_AGENT_STATUS + " TEXT NOT NULL" + ")";

    private static final String CACHED_JOBS_TABLE_CREATE = "CREATE TABLE IF NOT EXISTS " + CACHED_JOBS_TABLE + "(" + KEY_CJ_ID
            + " INTEGER PRIMARY KEY NOT NULL," + KEY_CJ_AGENT_ID + " INTEGER NOT NULL," + KEY_CJ_TIME_ASSIGNED + " TEXT NOT NULL," + KEY_CJ_TIME_SENT
            + " TEXT," + KEY_CJ_PARAMETERS + " TEXT," + KEY_CJ_PERIODIC + " INTEGER NOT NULL," + KEY_CJ_PERIOD + " INTEGER," + KEY_CJ_JOB_STATUS 
            + " TEXT NOT NULL" +")";

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

    public static String getKeyJAgentId() {
        return KEY_J_AGENT_ID;
    }

    public static String getKeyCaAgentId() {
        return KEY_CA_AGENT_ID;
    }

    public static String getKeyCaAgentHash() {
        return KEY_CA_AGENT_HASH;
    }

    public static String getKeyCaTimeAccepted() {
        return KEY_CA_TIME_ACCEPTED;
    }

    public static String getKeyCaTimeJobRequest() {
        return KEY_CA_TIME_JOB_REQUEST;
    }

    public static String getKeyCaTimeTerminated() {
        return KEY_CA_TIME_TERMINATED;
    }

    public static String getKeyCaAgentStatus() {
        return KEY_CA_AGENT_STATUS;
    }

    public static String getKeyCjId() {
        return KEY_CJ_ID;
    }

    public static String getKeyCjAgentId() {
        return KEY_CJ_AGENT_ID;
    }

    public static String getKeyCjTimeAssigned() {
        return KEY_CJ_TIME_ASSIGNED;
    }

    public static String getKeyCjTimeSent() {
        return KEY_CJ_TIME_SENT;
    }

    public static String getKeyCjParameters() {
        return KEY_CJ_PARAMETERS;
    }

    public static String getKeyCjPeriodic() {
        return KEY_CJ_PERIODIC;
    }

    public static String getKeyCjPeriod() {
        return KEY_CJ_PERIOD;
    }

    public static String getKeyCjJobStatus() {
        return KEY_CJ_JOB_STATUS;
    }

    public static String getUsersTable() {
        return USERS_TABLE;
    }

    public static String getJobsTable() {
        return JOBS_TABLE;
    }
    
    public static String getCachedAgentsTable() {
        return CACHED_AGENTS_TABLE;
    }

    public static String getCachedJobsTable() {
        return CACHED_JOBS_TABLE;
    }
}
