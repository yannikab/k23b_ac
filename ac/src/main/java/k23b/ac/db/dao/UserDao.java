package k23b.ac.db.dao;

import java.util.HashSet;
import java.util.Set;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

/**
 * The Data Access Object class for the manipulation of the Users Table and the retrieval of information from it
 *
 */
public class UserDao {

    private static String[] userTableColumns = { DatabaseHandler.getKeyUUsername(), DatabaseHandler.getKeyUPassword(),
            DatabaseHandler.getKeyUActive() };
    
    /**
     * The creation of a new User row in the Users Table
     * 
     * @param username
     * @param password
     * @return The username of the newly created User
     * @throws DaoException
     */
    public static String createUser(String username, String password) throws DaoException {

        boolean active = false;

        Log.d(UserDao.class.getName(),
                "Creating UserDao with Username: " + username + ", Password: " + password + " and Active: " + active);

        ContentValues values = new ContentValues();
        values.put(DatabaseHandler.getKeyUUsername(), username);
        values.put(DatabaseHandler.getKeyUPassword(), password);
        values.put(DatabaseHandler.getKeyUActive(), (active ? 1 : 0));

        DatabaseHandler dbHandler = DatabaseHandler.getDBHandler();
        // Express the need for an open Database
        SQLiteDatabase db = dbHandler.openDatabase();

        try {

            long rowId = db.insertOrThrow(DatabaseHandler.getUsersTable(), null, values);

            if (rowId < 0) {
                dbHandler.closeDatabase();
                Log.e(UserDao.class.getName(), "Error while inserting UserDao with Username: " + username
                        + ", Password: " + password + " and Active: " + active);
                throw new DaoException("Error while inserting UserDao with Username: " + username + ", Password: "
                        + password + " and Active: " + active);
            }

        } catch (SQLException e) {
            dbHandler.closeDatabase();
            Log.e(UserDao.class.getName(), e.getMessage());
            throw new DaoException("Error while inserting UserDao with Username: " + username + ", Password: "
                    + password + " and Active: " + active + " | " + e.getMessage());
        }

        Cursor cursor = db.query(DatabaseHandler.getUsersTable(), userTableColumns,
                DatabaseHandler.getKeyUUsername() + " = '" + username + "'", null, null, null, null);

        if (cursor.getCount() > 1) {
            cursor.close();
            // Database not needed anymore
            dbHandler.closeDatabase();
            Log.e(UserDao.class.getName(), "More than one Users with Username: " + username);
            throw new DaoException("More than one Users with Username: " + username);
        }

        if (cursor.moveToFirst()) {

            if (cursor.getString(0).compareTo(username) == 0) {

                Log.d(UserDao.class.getName(),
                        "Created UserDao with Username: " + username + " and Password: " + password + " successfully!");

                cursor.close();
                // Database not needed anymore
                dbHandler.closeDatabase();

                return username;
            }
            Log.d(UserDao.class.getName(), "Password: " + cursor.getString(1));

        }
        cursor.close();
        // Database not needed anymore
        dbHandler.closeDatabase();

        Log.e(UserDao.class.getName(), "Created UserDao with Username: " + username + ", Password: " + password
                + " and Active: " + active + " NOT FOUND !");
        throw new DaoException("Created UserDao with Username: " + username + ", Password: " + password
                + " and Active: " + active + " NOT FOUND !");

    }
    
    /**
     * Search for a User row based on a username
     * 
     * @param username
     * @return An instance of UserDao which includes the info from the selected row from the Users Table; null if no such username exists
     * @throws DaoException
     */
    public static UserDao findUserByUsername(String username) throws DaoException {

        UserDao user = null;
        Log.d(UserDao.class.getName(), "Searching UserDao with Username: " + username);

        DatabaseHandler dbHandler = DatabaseHandler.getDBHandler();
        // Express the need for an open Database
        SQLiteDatabase db = dbHandler.openDatabase();

        Cursor cursor = db.query(DatabaseHandler.getUsersTable(), userTableColumns,
                DatabaseHandler.getKeyUUsername() + " = '" + username + "'", null, null, null, null);

        if (cursor.getCount() > 1) {
            cursor.close();

            // Database not needed anymore
            dbHandler.closeDatabase();

            Log.e(UserDao.class.getName(), "More than one Users with Username: " + username);
            throw new DaoException("More than one Users with Username: " + username);
        }

        if (cursor.moveToFirst()) {
            Log.d(UserDao.class.getName(), "1 row selected");
            user = new UserDao(cursor.getString(0), cursor.getString(1), (cursor.getInt(2) == 1 ? true : false));
        } else
            Log.d(UserDao.class.getName(), "0 rows selected");

        cursor.close();

        // Database not needed anymore
        dbHandler.closeDatabase();

        return user;
    }
    
    /**
     * Deletes a User row based on a username 
     * 
     * @param username
     * @throws DaoException
     */
    public static void deleteUser(String username) throws DaoException {

        Log.d(UserDao.class.getName(), "Deleting UserDao with Username: " + username);
        DatabaseHandler dbHandler = DatabaseHandler.getDBHandler();

        // Express the need for an open Database
        SQLiteDatabase db = dbHandler.openDatabase();

        int rowsAffected = db.delete(DatabaseHandler.getUsersTable(),
                DatabaseHandler.getKeyUUsername() + " = '" + username + "'", null);
        Log.d(UserDao.class.getName(), "Rows affected: " + rowsAffected);

        if (rowsAffected == 0)
            throw new DaoException("Error while deleting user with username: " + username);

        // Database not needed anymore
        dbHandler.closeDatabase();
        Log.d(UserDao.class.getName(), "Deleted UserDao with Username: " + username);

    }
    
    /**
     * Searches for all User rows
     * 
     * @return A set of UserDao instances which include the info from the selected rows from the Users Table
     * @throws DaoException
     */
    public static Set<UserDao> findAll() throws DaoException {

        Log.d(UserDao.class.getName(), "Searching all Users");
        Set<UserDao> userSet = new HashSet<UserDao>();
        DatabaseHandler dbHandler = DatabaseHandler.getDBHandler();

        // Express the need for an open Database
        SQLiteDatabase db = dbHandler.openDatabase();

        Cursor cursor = db.query(DatabaseHandler.getUsersTable(), userTableColumns, null, null, null, null, null);

        int rows = 0;

        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {

                userSet.add(
                        new UserDao(cursor.getString(0), cursor.getString(1), (cursor.getInt(2) == 1 ? true : false)));
                rows++;
                cursor.moveToNext();
            }
        }
        Log.d(UserDao.class.getName(), rows + (rows == 1 ? " row " : " rows ") + "selected.");

        cursor.close();
        // Database not needed anymore
        dbHandler.closeDatabase();

        return userSet;

    }
    
    /**
     * Searches for all User rows from Active Users
     * 
     * @return A set of UserDao instances which include the info from the selected rows from the Users Table
     */
    public static Set<UserDao> findAllActive() {

        Log.d(UserDao.class.getName(), "Searching all Active Users");
        Set<UserDao> userSet = new HashSet<UserDao>();
        DatabaseHandler dbHandler = DatabaseHandler.getDBHandler();

        // Express the need for an open Database
        SQLiteDatabase db = dbHandler.openDatabase();

        Cursor cursor = db.query(DatabaseHandler.getUsersTable(), userTableColumns,
                DatabaseHandler.getKeyUActive() + " = " + 1, null, null, null, null);

        int rows = 0;

        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {

                userSet.add(
                        new UserDao(cursor.getString(0), cursor.getString(1), (cursor.getInt(2) == 1 ? true : false)));
                rows++;
                cursor.moveToNext();
            }
        }
        Log.d(UserDao.class.getName(), rows + (rows == 1 ? " row " : " rows ") + "selected.");

        cursor.close();
        // Database not needed anymore
        dbHandler.closeDatabase();

        return userSet;

    }

    /**
     * Sets a certain User as Active based on username
     * 
     * @param username
     * @throws DaoException
     */
    public static void setActive(String username) throws DaoException {

        Log.d(UserDao.class.getName(), "Setting Active: " + username);

        ContentValues values = new ContentValues();
        values.put(DatabaseHandler.getKeyUActive(), 1);

        DatabaseHandler dbHandler = DatabaseHandler.getDBHandler();
        // Express the need for an open Database
        SQLiteDatabase db = dbHandler.openDatabase();

        UserDao u = UserDao.findUserByUsername(username);
        if (u == null) {
            dbHandler.closeDatabase();
            throw new DaoException("No such UserDao: " + username + " to set Active");
        }

        int rowsAffected = db.update(DatabaseHandler.getUsersTable(), values,
                DatabaseHandler.getKeyUUsername() + " = '" + username + "'", null);

        Log.d(JobDao.class.getName(), "Rows affected: " + rowsAffected);

        // Database not needed anymore
        dbHandler.closeDatabase();
        Log.d(JobDao.class.getName(), "UserDao: " + username + " set to Active");

    }

    /**
     * Sets a certain User as Inactive based on username
     * 
     * @param username
     * @throws DaoException
     */
    public static void setInactive(String username) throws DaoException {

        Log.d(UserDao.class.getName(), "Setting Inactive: " + username);

        ContentValues values = new ContentValues();
        values.put(DatabaseHandler.getKeyUActive(), 0);

        DatabaseHandler dbHandler = DatabaseHandler.getDBHandler();
        // Express the need for an open Database
        SQLiteDatabase db = dbHandler.openDatabase();

        UserDao u = UserDao.findUserByUsername(username);
        if (u == null) {
            dbHandler.closeDatabase();
            throw new DaoException("No such UserDao: " + username + " to set Inactive");
        }

        int rowsAffected = db.update(DatabaseHandler.getUsersTable(), values,
                DatabaseHandler.getKeyUUsername() + " = '" + username + "'", null);

        Log.d(JobDao.class.getName(), "Rows affected: " + rowsAffected);

        // Database not needed anymore
        dbHandler.closeDatabase();
        Log.d(JobDao.class.getName(), "UserDao: " + username + " set to Inactive");
    }

    private String username;
    private String password;
    // private boolean active;

    protected UserDao(String username, String password, boolean active) {
        this.username = username;
        this.password = password;
        // this.active = active;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    // public boolean isActive() {
    // return active;
    // }

    @Override
    public String toString() {
        return "UserDao [username=" + username + ", password=" + password + ", active=" + "]";
    }
}
