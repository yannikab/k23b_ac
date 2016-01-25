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

@SuppressLint("SimpleDateFormat")
public class JobDao {

    private static String[] jobTableColumns = { DatabaseHandler.getKeyJId(), DatabaseHandler.getKeyJParameters(),
            DatabaseHandler.getKeyJUsername(), DatabaseHandler.getKeyJAgent_ID(), DatabaseHandler.getKeyJTimeAssigned(),
            DatabaseHandler.getKeyJPeriodic(), DatabaseHandler.getKeyJPeriod() };

    // I might need to return only an ID
    @SuppressLint("SimpleDateFormat")
    public static long createJob(String parameters, String username, long agentId, Date time_assigned, boolean periodic,
            int period) throws DaoException {

        Log.d(JobDao.class.getName(),
                "Creating Job with Parameters: " + parameters + " Username: " + username + " AgentId: " + agentId
                        + " Time assigned: " + time_assigned + " Periodic: " + periodic + " Period " + period);

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String time = dateFormat.format(time_assigned);

        ContentValues values = new ContentValues();
        values.put(DatabaseHandler.getKeyJParameters(), parameters);
        values.put(DatabaseHandler.getKeyJUsername(), username);
        values.put(DatabaseHandler.getKeyJAgent_ID(), agentId);
        values.put(DatabaseHandler.getKeyJTimeAssigned(), time);
        values.put(DatabaseHandler.getKeyJPeriodic(), (periodic ? 1 : 0));
        values.put(DatabaseHandler.getKeyJPeriod(), period);

        DatabaseHandler dbHandler = DatabaseHandler.getDBHandler();

        // Express the need for an open Database
        SQLiteDatabase db = dbHandler.openDatabase();

        // (these are checks that belong to the srv layer)
        //
        // UserDao user = UserDao.findUserbyUsername(username);
        // if (user == null) {
        // dbHandler.closeDatabase();
        // throw new DaoException("Creating Job with invalid User");
        // }
        // else if (!user.isActive()) {
        // dbHandler.closeDatabase();
        // throw new DaoException("Cannot create a Job from an Inactive User");
        // }

        long rowId;

        try {

            rowId = db.insertOrThrow(DatabaseHandler.getJobsTable(), null, values);

            if (rowId < 0) {
                // Database not needed anymore
                dbHandler.closeDatabase();

                String message = "Error while inserting Job Job with Parameters: " + parameters + " Username: "
                        + username + " AgentId: " + agentId + " Time assigned: " + time_assigned + " Periodic: "
                        + periodic + " Period " + period;

                Log.e(JobDao.class.getName(), message);

                throw new DaoException(message);
            }

        } catch (SQLException e) {
            // Database not needed anymore
            dbHandler.closeDatabase();

            Log.e(JobDao.class.getName(), e.getMessage());

            throw new DaoException("Error while inserting Job Job with Parameters: " + parameters + " Username: "
                    + username + " AgentId: " + agentId + " Time assigned: " + time_assigned + " Periodic: " + periodic
                    + " Period " + period + " | " + e.getMessage());
        }

        Cursor cursor = db.query(DatabaseHandler.getJobsTable(), jobTableColumns,
                DatabaseHandler.getKeyJId() + " = " + rowId, null, null, null, null);

        if (cursor.getCount() > 1) {
            cursor.close();
            // Database not needed anymore
            dbHandler.closeDatabase();

            Log.e(JobDao.class.getName(), "More than one Jobs with Id: " + rowId);

            throw new DaoException("More than one Jobs with Id: " + rowId);
        }

        if (cursor.moveToFirst()) {

            Log.d(JobDao.class.getName(),

                    "Created Job with Id: " + cursor.getLong(0) + " Parameters: " + cursor.getString(1) + " Username: "
                            + cursor.getString(2) + " AgentId: " + cursor.getLong(3) + " Time assigned: "
                            + cursor.getString(4) + " Periodic: " + cursor.getInt(5) + " Period: " + cursor.getInt(6)
                            + " successfully!");

            // job = new JobDao(cursor.getInt(0), cursor.getString(1),
            // cursor.getString(2), cursor.getInt(3),
            // cursor.getString(4), (cursor.getInt(5) == 1 ? true : false),
            // cursor.getInt(6));

            long id = cursor.getLong(0);

            cursor.close();
            // Database not needed anymore
            dbHandler.closeDatabase();

            return id;
        }

        cursor.close();

        // Database not needed anymore
        dbHandler.closeDatabase();

        String message = "Created Job with Id: " + cursor.getLong(0) + " Parameters: " + parameters + " Username: "
                + username + " AgentId: " + agentId + " Time assigned: " + time_assigned + " Periodic: " + periodic
                + " Period " + period + "NOT FOUND !";

        Log.e(JobDao.class.getName(), message);

        throw new DaoException(message);
    }

    public static JobDao findJobById(long jobId) throws DaoException {

        JobDao job = null;
        Log.d(JobDao.class.getName(), "Searching Job with Id: " + jobId);

        DatabaseHandler dbHandler = DatabaseHandler.getDBHandler();
        // Express the need for an open Database
        SQLiteDatabase db = dbHandler.openDatabase();

        Cursor cursor = db.query(DatabaseHandler.getJobsTable(), jobTableColumns,
                DatabaseHandler.getKeyJId() + " = " + jobId, null, null, null, null);

        if (cursor.getCount() > 1) {
            cursor.close();
            // Database not needed anymore
            dbHandler.closeDatabase();

            Log.e(JobDao.class.getName(), "More than one Jobs with Id: " + jobId);
            throw new DaoException("More than one Jobs with Id: " + jobId);
        }

        if (cursor.moveToFirst()) {
            Log.d(JobDao.class.getName(), "1 row selected");
            job = new JobDao(cursor.getInt(0), cursor.getString(1), cursor.getString(2), cursor.getInt(3),
                    cursor.getString(4), (cursor.getInt(5) == 1 ? true : false), cursor.getInt(6));
        } else {
            Log.d(JobDao.class.getName(), "0 rows selected");
        }

        cursor.close();
        // Database not needed anymore
        dbHandler.closeDatabase();

        return job;
    }

    public static void deleteJob(long jobId) throws DaoException {

        Log.d(JobDao.class.getName(), "Deleting Job with Id: " + jobId);

        DatabaseHandler dbHandler = DatabaseHandler.getDBHandler();
        // Express the need for an open Database
        SQLiteDatabase db = dbHandler.openDatabase();

        int rowsAffected = db.delete(DatabaseHandler.getJobsTable(), DatabaseHandler.getKeyJId() + " = " + jobId, null);

        Log.d(JobDao.class.getName(), "Rows affected: " + rowsAffected);
        // Database not needed anymore
        dbHandler.closeDatabase();

        if (rowsAffected == 0)
            throw new DaoException("No such Job to delete");

        Log.d(JobDao.class.getName(), "Deleted Job with Id: " + jobId);
    }

    public static Set<JobDao> findAllJobsFromUsername(String username) throws DaoException {

        Log.d(JobDao.class.getName(), "Searching all Jobs with Username: " + username);
        Set<JobDao> jobSet = new HashSet<JobDao>();

        DatabaseHandler dbHandler = DatabaseHandler.getDBHandler();
        // Express the need for an open Database
        SQLiteDatabase db = dbHandler.openDatabase();

        Cursor cursor = db.query(DatabaseHandler.getJobsTable(), jobTableColumns,
                DatabaseHandler.getKeyJUsername() + " = '" + username + "'", null, null, null, null);

        // check belongs to the srv layer, we assume the user key is correct
        // UserDao user = UserDao.findUserbyUsername(username);
        // if (user == null) {
        // dbHandler.closeDatabase();
        // throw new DaoException("No such Username");
        // }

        int rows = 0;

        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {

                jobSet.add(new JobDao(cursor.getInt(0), cursor.getString(1), cursor.getString(2), cursor.getInt(3),
                        cursor.getString(4), (cursor.getInt(5) == 1 ? true : false), cursor.getInt(6)));

                rows++;
                cursor.moveToNext();
            }
        }

        Log.d(JobDao.class.getName(), rows + (rows == 1 ? " row " : " rows ") + "selected.");

        cursor.close();
        // Database not needed anymore
        dbHandler.closeDatabase();

        return jobSet;
    }

    public static Set<JobDao> findAllJobsFromAgentId(long agentId) {

        Log.d(JobDao.class.getName(), "Searching all Jobs with AgentId: " + agentId);
        Set<JobDao> jobSet = new HashSet<JobDao>();

        DatabaseHandler dbHandler = DatabaseHandler.getDBHandler();
        // Express the need for an open Database
        SQLiteDatabase db = dbHandler.openDatabase();

        Cursor cursor = db.query(DatabaseHandler.getJobsTable(), jobTableColumns,
                DatabaseHandler.getKeyJAgent_ID() + " = '" + agentId + "'", null, null, null, null);

        int rows = 0;

        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {

                jobSet.add(new JobDao(cursor.getInt(0), cursor.getString(1), cursor.getString(2), cursor.getInt(3),
                        cursor.getString(4), (cursor.getInt(5) == 1 ? true : false), cursor.getInt(6)));

                rows++;
                cursor.moveToNext();
            }
        }

        Log.d(JobDao.class.getName(), rows + (rows == 1 ? " row " : " rows ") + "selected.");

        cursor.close();
        // Database not needed anymore
        dbHandler.closeDatabase();

        return jobSet;

    }

    public static Set<JobDao> findAllJobsFromActiveUsers() {

        Log.d(JobDao.class.getName(), "Searching all Jobs from Active Users");
        Set<JobDao> jobSet = new HashSet<JobDao>();

        DatabaseHandler dbHandler = DatabaseHandler.getDBHandler();
        // Express the need for an open Database
        SQLiteDatabase db = dbHandler.openDatabase();

        StringBuilder sb = new StringBuilder();
        sb.append("SELECT * FROM ").append(DatabaseHandler.getJobsTable()).append(" a INNER JOIN ")
                .append(DatabaseHandler.getUsersTable()).append(" b ON a.").append(DatabaseHandler.getKeyJUsername())
                .append("=b.").append(DatabaseHandler.getKeyUUsername()).append(" WHERE b.")
                .append(DatabaseHandler.getKeyUActive()).append("=?");

        Cursor cursor = db.rawQuery(sb.toString(), new String[] { String.valueOf(1) });

        int rows = 0;

        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {

                jobSet.add(new JobDao(cursor.getInt(0), cursor.getString(1), cursor.getString(2), cursor.getInt(3),
                        cursor.getString(4), (cursor.getInt(5) == 1 ? true : false), cursor.getInt(6)));

                rows++;
                cursor.moveToNext();
            }
        }
        Log.d(JobDao.class.getName(), rows + (rows == 1 ? " row " : " rows ") + "selected.");

        cursor.close();
        // Database not needed anymore
        dbHandler.closeDatabase();

        return jobSet;

    }

    // public static void setTimeAssigned(int jobId, Date timeAssigned) throws
    // DaoException {
    //
    // Log.d(JobDao.class.getName(), "Updating Time Assigned of Job with Id: " +
    // jobId);
    //
    // SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd
    // HH:mm:ss");
    // String time = dateFormat.format(timeAssigned);
    //
    // ContentValues values = new ContentValues();
    // values.put(DatabaseHandler.getKeyJTimeAssigned(), time);
    //
    // DatabaseHandler dbHandler = DatabaseHandler.getDBHandler();
    // // Express the need for an open Database
    // db = dbHandler.openDatabase();
    //
    // JobDao j = JobDao.findJobById(jobId);
    // if (j == null) {
    // dbHandler.closeDatabase();
    // throw new DaoException("No such Job to set time");
    // }
    //
    // int rowsAffected = db.update(DatabaseHandler.getJobsTable(), values,
    // DatabaseHandler.getKeyJId() + " = " + jobId, null);
    //
    // Log.d(JobDao.class.getName(), "Rows affected: " + rowsAffected);
    //
    // // Database not needed anymore
    // dbHandler.closeDatabase();
    // Log.d(JobDao.class.getName(), "Time Assigned of Job with Id: " + jobId +
    // " Updated");
    //
    // }

    private long id;
    private String parameters;
    private String username;
    private long agentId;
    private Date time_assigned;
    private boolean periodic;
    private int period;

    protected JobDao(long id, String parameters, String username, long agentId, Date time_assigned, boolean periodic,
            int period) {
        this.id = id;
        this.parameters = parameters;
        this.username = username;
        this.agentId = agentId;
        this.time_assigned = time_assigned;
        this.periodic = periodic;
        this.period = period;
    }

    @SuppressLint("SimpleDateFormat")
    private JobDao(long id, String parameters, String username, long agentId, String time_assigned, boolean periodic,
            int period) {

        DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        this.id = id;
        this.parameters = parameters;
        this.username = username;
        this.agentId = agentId;
        try {
            this.time_assigned = format.parse(time_assigned);
        } catch (ParseException e) {
            Log.e(JobDao.class.getName(), "Parse error on Date parsing");
        }
        this.periodic = periodic;
        this.period = period;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getParameters() {
        return parameters;
    }

    public void setParameters(String parameters) {
        this.parameters = parameters;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public long getAgentId() {
        return agentId;
    }

    public void setAgentId(int agentId) {
        this.agentId = agentId;
    }

    public Date getTime_assigned() {
        return time_assigned;
    }

    public void setTime_assigned(Date time_assigned) {
        this.time_assigned = time_assigned;
    }

    public boolean getPeriodic() {
        return periodic;
    }

    public void setPeriodic(boolean periodic) {
        this.periodic = periodic;
    }

    public int getPeriod() {
        return period;
    }

    public void setPeriod(int period) {
        this.period = period;
    }

    @Override
    public String toString() {
        return "Job [id=" + id + ", parameters=" + parameters + ", username=" + username + ", agentId=" + agentId
                + ", time_assigned=" + time_assigned + ", periodic=" + periodic + ", period=" + period + "]";
    }
}
