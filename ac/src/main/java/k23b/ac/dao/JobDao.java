package k23b.ac.dao;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.util.Log;

@SuppressLint("SimpleDateFormat")
public class JobDao {

    private static SQLiteDatabase db;

    private static String[] jobTableColumns = { DatabaseHandler.getKeyJId(), DatabaseHandler.getKeyJParameters(),
            DatabaseHandler.getKeyJUsername(), DatabaseHandler.getKeyJAgent_ID(), DatabaseHandler.getKeyJTimeAssigned(),
            DatabaseHandler.getKeyJPeriodic(), DatabaseHandler.getKeyJPeriod() };

    // I might need to return only an ID
    @SuppressLint("SimpleDateFormat")
    public static Job createJob(String parameters, String username, int agentId, Date time_assigned, boolean periodic,
            int period) throws DaoException {

        Log.d(JobDao.class.getName(),
                "Creating Job with Parameters: " + parameters + " Username: " + username + " AgentId: " + agentId
                        + " Time assigned: " + time_assigned + " Periodic: " + periodic + " Period " + period);

        Job job = null;
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
        db = dbHandler.openDatabase();

        User user = UserDao.findUserbyUsername(username);
        if (user == null) {
            dbHandler.closeDatabase();
            throw new DaoException("Creating Job with invalid User");
        }
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
        } catch (SQLiteException e) {
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
                    "Created Job with Id: " + cursor.getInt(0) + " Parameters: " + cursor.getString(1) + " Username: "
                            + cursor.getString(2) + " AgentId: " + cursor.getInt(3) + " Time assigned: "
                            + cursor.getString(4) + " Periodic: " + cursor.getInt(5) + " Period: " + cursor.getInt(6)
                            + " successfully!");
            job = new Job(cursor.getInt(0), cursor.getString(1), cursor.getString(2), cursor.getInt(3),
                    cursor.getString(4), (cursor.getInt(5) == 1 ? true : false), cursor.getInt(6));

            cursor.close();
            // Database not needed anymore
            dbHandler.closeDatabase();
            return job;
        }

        cursor.close();

        // Database not needed anymore
        dbHandler.closeDatabase();
        String message = "Created Job with Id: " + cursor.getInt(0) + " Parameters: " + parameters + " Username: "
                + username + " AgentId: " + agentId + " Time assigned: " + time_assigned + " Periodic: " + periodic
                + " Period " + period + "NOT FOUND !";
        Log.e(JobDao.class.getName(), message);
        throw new DaoException(message);

    }

    public static Job findJobById(int jobId) throws DaoException {

        Job job = null;
        Log.d(JobDao.class.getName(), "Searching Job with Id: " + jobId);

        DatabaseHandler dbHandler = DatabaseHandler.getDBHandler();
        // Express the need for an open Database
        db = dbHandler.openDatabase();

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
            job = new Job(cursor.getInt(0), cursor.getString(1), cursor.getString(2), cursor.getInt(3),
                    cursor.getString(4), (cursor.getInt(5) == 1 ? true : false), cursor.getInt(6));
        } else
            Log.d(JobDao.class.getName(), "0 rows selected");

        cursor.close();
        // Database not needed anymore
        dbHandler.closeDatabase();

        return job;

    }

    public static void deleteJob(int jobId) throws DaoException {
        Log.d(JobDao.class.getName(), "Deleting Job with Id: " + jobId);

        DatabaseHandler dbHandler = DatabaseHandler.getDBHandler();
        // Express the need for an open Database
        db = dbHandler.openDatabase();

        int rowsAffected = db.delete(DatabaseHandler.getJobsTable(), DatabaseHandler.getKeyJId() + " = " + jobId, null);

        Log.d(JobDao.class.getName(), "Rows affected: " + rowsAffected);
        // Database not needed anymore
        dbHandler.closeDatabase();

        if (rowsAffected == 0)
            throw new DaoException("No such Job to delete");

        Log.d(JobDao.class.getName(), "Deleted Job with Id: " + jobId);
    }

    public static Set<Job> findAllJobsFromUsername(String username) throws DaoException {

        Log.d(JobDao.class.getName(), "Searching all Jobs with Username: " + username);
        Set<Job> jobSet = new HashSet<Job>();

        DatabaseHandler dbHandler = DatabaseHandler.getDBHandler();
        // Express the need for an open Database
        db = dbHandler.openDatabase();

        Cursor cursor = db.query(DatabaseHandler.getJobsTable(), jobTableColumns,
                DatabaseHandler.getKeyJUsername() + " = '" + username + "'", null, null, null, null);

        User user = UserDao.findUserbyUsername(username);
        if (user == null) {
            dbHandler.closeDatabase();
            throw new DaoException("No such Username");
        }

        int rows = 0;

        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {

                jobSet.add(new Job(cursor.getInt(0), cursor.getString(1), cursor.getString(2), cursor.getInt(3),
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

    public static Set<Job> findAllJobsFromAgentId(int agentId) {

        Log.d(JobDao.class.getName(), "Searching all Jobs with AgentId: " + agentId);
        Set<Job> jobSet = new HashSet<Job>();

        DatabaseHandler dbHandler = DatabaseHandler.getDBHandler();
        // Express the need for an open Database
        db = dbHandler.openDatabase();

        Cursor cursor = db.query(DatabaseHandler.getJobsTable(), jobTableColumns,
                DatabaseHandler.getKeyJAgent_ID() + " = '" + agentId + "'", null, null, null, null);

        int rows = 0;

        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {

                jobSet.add(new Job(cursor.getInt(0), cursor.getString(1), cursor.getString(2), cursor.getInt(3),
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

    public static Set<Job> findAllJobsFromActiveUsers() {

        Log.d(JobDao.class.getName(), "Searching all Jobs from Active Users");
        Set<Job> jobSet = new HashSet<Job>();

        DatabaseHandler dbHandler = DatabaseHandler.getDBHandler();
        // Express the need for an open Database
        db = dbHandler.openDatabase();

        StringBuilder sb = new StringBuilder();
        sb.append("SELECT * FROM ")
                .append(DatabaseHandler.getJobsTable())
                .append(" a INNER JOIN ")
                .append(DatabaseHandler.getUsersTable())
                .append(" b ON a.")
                .append(DatabaseHandler.getKeyJUsername())
                .append("=b.")
                .append(DatabaseHandler.getKeyUUsername())
                .append(" WHERE b.")
                .append(DatabaseHandler.getKeyUActive())
                .append("=?");

        Cursor cursor = db.rawQuery(sb.toString(), new String[] { String.valueOf(1) });

        int rows = 0;

        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {

                jobSet.add(new Job(cursor.getInt(0), cursor.getString(1), cursor.getString(2), cursor.getInt(3),
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

    public static void setTimeAssigned(int jobId, Date timeAssigned) throws DaoException {

        Log.d(JobDao.class.getName(), "Updating Time Assigned of Job with Id: " + jobId);

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String time = dateFormat.format(timeAssigned);

        ContentValues values = new ContentValues();
        values.put(DatabaseHandler.getKeyJTimeAssigned(), time);

        DatabaseHandler dbHandler = DatabaseHandler.getDBHandler();
        // Express the need for an open Database
        db = dbHandler.openDatabase();

        Job j = JobDao.findJobById(jobId);
        if (j == null) {
            dbHandler.closeDatabase();
            throw new DaoException("No such Job to set time");
        }

        int rowsAffected = db.update(DatabaseHandler.getJobsTable(), values,
                DatabaseHandler.getKeyJId() + " = " + jobId, null);

        Log.d(JobDao.class.getName(), "Rows affected: " + rowsAffected);

        // Database not needed anymore
        dbHandler.closeDatabase();
        Log.d(JobDao.class.getName(), "Time Assigned of Job with Id: " + jobId + " Updated");

    }

}
