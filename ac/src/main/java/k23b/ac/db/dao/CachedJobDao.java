package k23b.ac.db.dao;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

/**
 * The Data Access Object class for the manipulation of the Cached Jobs Table and the retrieval of information from it.
 *
 */
public class CachedJobDao {

    private static String[] cachedjobTableColumns = { DatabaseHandler.getKeyCjId(), DatabaseHandler.getKeyCjAgentId(),
            DatabaseHandler.getKeyCjTimeAssigned(), DatabaseHandler.getKeyCjTimeSent(), DatabaseHandler.getKeyCjParameters(),
            DatabaseHandler.getKeyCjPeriodic(), DatabaseHandler.getKeyCjPeriod(), DatabaseHandler.getKeyCjJobStatus() };

    /**
     * The creation of a new Cached Job row in the Cached Jobs Table
     * 
     * @param jobId
     * @param agentId
     * @param timeAssigned
     * @param timeSent
     * @param parameters
     * @param periodic
     * @param period
     * @param status
     * @throws DaoException
     */
    // this method assumes that a job does not exist with this id, and blindly creates it
    @SuppressLint("SimpleDateFormat")
    public static void create(long jobId, long agentId, Date timeAssigned, Date timeSent, String parameters, boolean periodic, int period, CachedJobStatus status) throws DaoException {

        Log.d(CachedJobDao.class.getName(), "Creating Cached Job with JobId: " + jobId + " AgentId: "
                + agentId + " TimeAssigned: " + timeAssigned + " Time sent: " + timeSent + " Parameters: " + parameters
                + " Periodic: " + periodic + " Period " + period + " JobStatus: " + status.toString());

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        ContentValues values = new ContentValues();
        values.put(DatabaseHandler.getKeyCjId(), jobId);
        values.put(DatabaseHandler.getKeyCjAgentId(), agentId);
        values.put(DatabaseHandler.getKeyCjTimeAssigned(), timeAssigned != null ? dateFormat.format(timeAssigned) : null);
        values.put(DatabaseHandler.getKeyCjTimeSent(), timeSent != null ? dateFormat.format(timeSent) : null);
        values.put(DatabaseHandler.getKeyCjParameters(), parameters);
        values.put(DatabaseHandler.getKeyCjPeriodic(), (periodic ? 1 : 0));
        values.put(DatabaseHandler.getKeyCjPeriod(), period);
        values.put(DatabaseHandler.getKeyCjJobStatus(), status.toString());

        DatabaseHandler dbHandler = DatabaseHandler.getDBHandler();

        // Express the need for an open Database
        SQLiteDatabase db = dbHandler.openDatabase();
        long rowId;

        try {

            rowId = db.insertOrThrow(DatabaseHandler.getCachedJobsTable(), null, values);

            if (rowId < 0) {
                // Database not needed anymore
                dbHandler.closeDatabase();

                String message = "Error while inserting Cached Job with JobId: " + jobId + " AgentId: "
                        + agentId + " TimeAssigned: " + timeAssigned + " Time sent: " + timeSent + " Parameters: " + parameters
                        + " Periodic: " + periodic + " Period " + period + " JobStatus: " + status.toString();

                Log.e(CachedJobDao.class.getName(), message);

                throw new DaoException(message);
            }
        } catch (SQLException e) {
            // Database not needed anymore
            dbHandler.closeDatabase();

            Log.e(CachedJobDao.class.getName(), e.getMessage());

            throw new DaoException("Error while inserting Cached Job with JobId: " + jobId + " AgentId: "
                    + agentId + " TimeAssigned: " + timeAssigned + " Time sent: " + timeSent + " Parameters: " + parameters
                    + " Periodic: " + periodic + " Period " + period + " JobStatus: " + status.toString() + " | " + e.getMessage());
        }

        Cursor cursor = db.query(DatabaseHandler.getCachedJobsTable(), cachedjobTableColumns, DatabaseHandler.getKeyCjId() + " = " + jobId, null, null, null, null);
        if (cursor.getCount() > 1) {
            cursor.close();
            // Database not needed anymore
            dbHandler.closeDatabase();

            Log.e(CachedJobDao.class.getName(), "More than one Cached Jobs with Id: " + jobId);
            throw new DaoException("More than one Cached Jobs with Id: " + jobId);
        }

        if (cursor.moveToFirst()) {

            Log.d(CachedJobDao.class.getName(), "Created Cached Job with JobId: " + cursor.getLong(0) + " AgentId: "
                    + cursor.getLong(1) + " TimeAssigned: " + cursor.getString(2) + " Time sent: " + cursor.getString(3) + " Parameters: " + cursor.getString(4)
                    + " Periodic: " + cursor.getInt(5) + " Period " + cursor.getInt(6) + " JobStatus: " + cursor.getString(7)
                    + " successfully!");

            cursor.close();
            // Database not needed anymore
            dbHandler.closeDatabase();
            return;
        }

        String message = "Cached Job with JobId: " + jobId + " AgentId: "
                + agentId + " TimeAssigned: " + timeAssigned + " Time sent: " + timeSent + " Parameters: " + parameters
                + " Periodic: " + periodic + " Period " + period + " JobStatus: " + status.toString() + " created but NOT FOUND";

        cursor.close();
        // Database not needed anymore
        dbHandler.closeDatabase();

        Log.e(CachedJobDao.class.getName(), message);
        throw new DaoException(message);
    }

    /**
     * The update of a Cached Job row in the Cached Jobs Table.
     * 
     * @param jobId
     * @param agentId
     * @param timeAssigned
     * @param timeSent
     * @param parameters
     * @param periodic
     * @param period
     * @param status
     * @throws DaoException
     */
    // this method assumes that a job exists with this id, and blindly updates it
    @SuppressLint("SimpleDateFormat")
    public static void update(long jobId, long agentId, Date timeAssigned, Date timeSent, String parameters, boolean periodic, int period, CachedJobStatus status) throws DaoException {

        Log.d(CachedJobDao.class.getName(), "Updating Cached Job with JobId: " + jobId + " AgentId: "
                + agentId + " TimeAssigned: " + timeAssigned + " Time sent: " + timeSent + " Parameters: " + parameters
                + " Periodic: " + periodic + " Period " + period + " JobStatus: " + status.toString());

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        ContentValues values = new ContentValues();
        values.put(DatabaseHandler.getKeyCjId(), jobId);
        values.put(DatabaseHandler.getKeyCjAgentId(), agentId);
        values.put(DatabaseHandler.getKeyCjTimeAssigned(), timeAssigned != null ? dateFormat.format(timeAssigned) : null);
        values.put(DatabaseHandler.getKeyCjTimeSent(), timeSent != null ? dateFormat.format(timeSent) : null);
        values.put(DatabaseHandler.getKeyCjParameters(), parameters);
        values.put(DatabaseHandler.getKeyCjPeriodic(), (periodic ? 1 : 0));
        values.put(DatabaseHandler.getKeyCjPeriod(), period);
        values.put(DatabaseHandler.getKeyCjJobStatus(), status.toString());

        DatabaseHandler dbHandler = DatabaseHandler.getDBHandler();

        // Express the need for an open Database
        SQLiteDatabase db = dbHandler.openDatabase();

        int rowsAffected = db.update(DatabaseHandler.getCachedJobsTable(), values, DatabaseHandler.getKeyCjId() + " = " + jobId, null);

        if (rowsAffected != 1) {
            // Database not needed anymore
            dbHandler.closeDatabase();
            throw new DaoException("None or more than one Rows affected");
        }

        // Database not needed anymore
        dbHandler.closeDatabase();
        Log.d(CachedJobDao.class.getName(), "CachedJob with JobId: " + jobId + " updated.");
    }

    /**
     * Search if the Cached Job with jobId exists.
     * 
     * @param jobId
     * @return true if it exists , false otherwise.
     * @throws DaoException
     */
    public static boolean exists(long jobId) throws DaoException {

        Log.d(CachedJobDao.class.getName(), "Searching for the existance of CachedJob with JobId: " + jobId);
        DatabaseHandler dbHandler = DatabaseHandler.getDBHandler();
        // Express the need for an open Database
        SQLiteDatabase db = dbHandler.openDatabase();

        Cursor cursor = db.query(DatabaseHandler.getCachedJobsTable(), cachedjobTableColumns,
                DatabaseHandler.getKeyCjId() + " = " + jobId, null, null, null, null);

        if (cursor.getCount() > 1) {
            cursor.close();
            // Database not needed anymore
            dbHandler.closeDatabase();

            Log.e(CachedJobDao.class.getName(), "More than one CachedJobs with Id: " + jobId);
            throw new DaoException("More than one CachedJobs with Id: " + jobId);
        }

        if (cursor.moveToFirst()) {
            Log.d(CachedJobDao.class.getName(), "1 row selected");
            cursor.close();
            // Database not needed anymore
            dbHandler.closeDatabase();
            return true;
        }

        Log.d(CachedJobDao.class.getName(), "0 row selected");
        cursor.close();
        // Database not needed anymore
        dbHandler.closeDatabase();
        return false;
    }

    /**
     * Search for all the Cached Jobs with agentId.
     * 
     * @param agentId
     * @return A set containing all CachedJobDao with agentId.
     * @throws DaoException
     */
    public static Set<CachedJobDao> findAllWithAgentId(long agentId) throws DaoException {

        Log.d(CachedJobDao.class.getName(), "Searching all CachedJobs from Agent with AgentId: " + agentId);
        Set<CachedJobDao> jobSet = new HashSet<CachedJobDao>();

        DatabaseHandler dbHandler = DatabaseHandler.getDBHandler();
        // Express the need for an open Database
        SQLiteDatabase db = dbHandler.openDatabase();

        Cursor cursor = db.query(DatabaseHandler.getCachedJobsTable(), cachedjobTableColumns,
                DatabaseHandler.getKeyCjAgentId() + " = " + agentId, null, null, null, null);

        int rows = 0;

        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {

                jobSet.add(new CachedJobDao(cursor.getLong(0), cursor.getLong(1), cursor.getString(2), cursor.getString(3),
                        cursor.getString(4), (cursor.getInt(5) == 1 ? true : false), cursor.getInt(6), CachedJobStatus.valueOf(cursor.getString(7))));

                rows++;
                cursor.moveToNext();
            }
        }
        Log.d(CachedJobDao.class.getName(), rows + (rows == 1 ? " row " : " rows ") + "selected.");

        cursor.close();
        // Database not needed anymore
        dbHandler.closeDatabase();

        return jobSet;
    }

    private long jobId;
    private long agentId;
    private Date timeAssigned;
    private Date timeSent;
    private String parameters;
    private boolean periodic;
    private int period;
    private CachedJobStatus status;

    public long getJobId() {
        return jobId;
    }

    public long getAgentId() {
        return agentId;
    }

    public Date getTimeAssigned() {
        return timeAssigned;
    }

    public Date getTimeSent() {
        return timeSent;
    }

    public String getParameters() {
        return parameters;
    }

    public boolean getPeriodic() {
        return periodic;
    }

    public int getPeriod() {
        return period;
    }

    public CachedJobStatus getStatus() {
        return status;
    }

    public CachedJobDao(long jobId, long agentId, Date timeAssigned, Date timeSent, String parameters, boolean periodic, int period, CachedJobStatus status) {
        super();
        this.jobId = jobId;
        this.agentId = agentId;
        this.timeAssigned = timeAssigned;
        this.timeSent = timeSent;
        this.parameters = parameters;
        this.periodic = periodic;
        this.period = period;
        this.status = status;
    }

    @SuppressLint("SimpleDateFormat")
    public CachedJobDao(long jobId, long agentId, String timeAssigned, String timeSent, String parameters, boolean periodic, int period, CachedJobStatus status) {
        super();

        DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        this.jobId = jobId;
        this.agentId = agentId;
        try {
            this.timeAssigned = timeAssigned != null ? format.parse(timeAssigned) : null;
            this.timeSent = timeSent != null ? format.parse(timeSent) : null;
        } catch (ParseException e) {
            Log.e(CachedJobDao.class.getName(), "Parse error on Date parsing");
        }
        this.parameters = parameters;
        this.periodic = periodic;
        this.period = period;
        this.status = status;
    }
}
