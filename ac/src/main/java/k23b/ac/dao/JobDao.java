package k23b.ac.dao;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.util.Log;

public class JobDao {

    private SQLiteDatabase db;
    private DatabaseHandler dbHandler;

    private String[] jobTableColumns = { DatabaseHandler.getKeyJId(), DatabaseHandler.getKeyJParameters(),
            DatabaseHandler.getKeyJUsername(), DatabaseHandler.getKeyJTimeAssigned(), DatabaseHandler.getKeyJPeriodic(),
            DatabaseHandler.getKeyJPeriod() };

    public JobDao(Context context) {
        dbHandler = DatabaseHandler.getDBHandler(context);
    }

    // I might need to return only an ID
    public Job createJob(String parameters, String username, Date time_assigned, boolean periodic, int period)
            throws DaoException {

        Log.d(JobDao.class.getName(), "Creating Job with Parameters: " + parameters + " Username: " + username
                + " Time assigned: " + time_assigned + " Periodic: " + periodic + " Period " + period);

        Job job = null;
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String time = dateFormat.format(time_assigned);

        ContentValues values = new ContentValues();
        values.put(DatabaseHandler.getKeyJParameters(), parameters);
        values.put(DatabaseHandler.getKeyJUsername(), username);
        values.put(DatabaseHandler.getKeyJTimeAssigned(), time);
        values.put(DatabaseHandler.getKeyJPeriodic(), (periodic ? 1 : 0));
        values.put(DatabaseHandler.getKeyJPeriod(), period);

        // Express the need for an open Database
        db = dbHandler.openDatabase();
        long rowId;
        try {
            rowId = db.insertOrThrow(DatabaseHandler.getJobsTable(), null, values);

            if (rowId < 0) {
                // Database not needed anymore
                dbHandler.closeDatabase();

                String message = "Error while inserting Job Job with Parameters: " + parameters + " Username: "
                        + username + " Time assigned: " + time_assigned + " Periodic: " + periodic + " Period "
                        + period;
                Log.e(JobDao.class.getName(), message);
                throw new DaoException(message);
            }
        } catch (SQLiteException e) {
            // Database not needed anymore
            dbHandler.closeDatabase();

            Log.e(JobDao.class.getName(), e.getMessage());
            throw new DaoException("Error while inserting Job Job with Parameters: " + parameters + " Username: "
                    + username + " Time assigned: " + time_assigned + " Periodic: " + periodic + " Period " + period
                    + " | " + e.getMessage());
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
                            + cursor.getString(2) + " Time assigned: " + cursor.getString(3) + " Periodic: "
                            + cursor.getInt(4) + " Period: " + cursor.getInt(5) + " successfully!");
            job = new Job(cursor.getInt(0), cursor.getString(1), cursor.getString(2), cursor.getString(3),
                    (cursor.getInt(4) == 1 ? true : false), cursor.getInt(5));

            cursor.close();
            // Database not needed anymore
            dbHandler.closeDatabase();
            return job;
        }

        cursor.close();

        // Database not needed anymore
        dbHandler.closeDatabase();
        String message = "Created Job with Id: " + cursor.getInt(0) + " Parameters: " + parameters + " Username: "
                + username + " Time assigned: " + time_assigned + " Periodic: " + periodic + " Period " + period
                + "NOT FOUND !";
        Log.e(JobDao.class.getName(), message);
        throw new DaoException(message);

    }

    public Job findJobById(int jobId) throws DaoException {

        Job job = null;
        Log.d(JobDao.class.getName(), "Searching Job with Id: " + jobId);
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
            job = new Job(cursor.getInt(0), cursor.getString(1), cursor.getString(2), cursor.getString(3),
                    (cursor.getInt(4) == 1 ? true : false), cursor.getInt(5));
        } else
            Log.d(JobDao.class.getName(), "0 rows selected");

        cursor.close();
        // Database not needed anymore
        dbHandler.closeDatabase();

        return job;

    }

    public void deleteJob(int jobId)  throws DaoException {
        Log.d(JobDao.class.getName(), "Deleting Job with Id: " + jobId);

        // Express the need for an open Database
        db = dbHandler.openDatabase();

        int rowsAffected = db.delete(DatabaseHandler.getJobsTable(), DatabaseHandler.getKeyJId() + " = " + jobId, null);
        Log.d(JobDao.class.getName(), "Rows affected: " + rowsAffected);
        // Database not needed anymore
        dbHandler.closeDatabase();
        Log.d(JobDao.class.getName(), "Deleted Job with Id: " + jobId);
    }

    public Set<Job> findAllJobsFromUsername(String username) throws DaoException {

        Log.d(JobDao.class.getName(), "Searching all Jobs with Username: " + username);
        Set<Job> jobSet = new HashSet<Job>();

        // Express the need for an open Database
        db = dbHandler.openDatabase();

        Cursor cursor = db.query(DatabaseHandler.getJobsTable(), jobTableColumns,
                DatabaseHandler.getKeyJUsername() + " = '" + username + "'", null, null, null, null);

        int rows = 0;

        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {

                jobSet.add(new Job(cursor.getInt(0), cursor.getString(1), cursor.getString(2), cursor.getString(3),
                        (cursor.getInt(4) == 1 ? true : false), cursor.getInt(5)));

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

    public void sentTimeAssigned(int jobId, Date timeAssigned) {

        Log.d(JobDao.class.getName(), "Updating Time Assigned of Job with Id: " + jobId);

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String time = dateFormat.format(timeAssigned);

        ContentValues values = new ContentValues();
        values.put(DatabaseHandler.getKeyJTimeAssigned(), time);
        // Express the need for an open Database
        db = dbHandler.openDatabase();

        int rowsAffected = db.update(DatabaseHandler.getJobsTable(), values,
                DatabaseHandler.getKeyJId() + " = " + jobId, null);

        Log.d(JobDao.class.getName(), "Rows affected: " + rowsAffected);

        // Database not needed anymore
        dbHandler.closeDatabase();
        Log.d(JobDao.class.getName(), "Time Assigned of Job with Id: " + jobId + " Updated");

    }

}
