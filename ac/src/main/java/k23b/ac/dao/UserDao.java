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
import k23b.ac.MainActivity;
import k23b.ac.dao.DaoException;

public class UserDao {

	private static final Context userDaoContext = MainActivity.context;
	private static DatabaseHandler dbHandler;
	private static SQLiteDatabase db;

	private static String[] userTableColumns = { DatabaseHandler.getKeyUUsername(), DatabaseHandler.getKeyUPassword(),
			DatabaseHandler.getKeyUActive() };

	public static User createUser(String username, String password, boolean active) throws DaoException {

		Log.d(UserDao.class.getName(),
				"Creating User with Username: " + username + ", Password: " + password + " and Active: " + active);

		User user = null;
		ContentValues values = new ContentValues();
		values.put(DatabaseHandler.getKeyUUsername(), username);
		values.put(DatabaseHandler.getKeyUPassword(), password);
		values.put(DatabaseHandler.getKeyUActive(), (active ? 1 : 0));

		dbHandler = DatabaseHandler.getDBHandler(userDaoContext);
		// Express the need for an open Database
		db = dbHandler.openDatabase();

		try {
			long rowId = db.insertOrThrow(DatabaseHandler.getUsersTable(), null, values);

			if (rowId < 0) {
				dbHandler.closeDatabase();
				Log.e(UserDao.class.getName(), "Error while inserting User with Username: " + username + ", Password: "
						+ password + " and Active: " + active);
				throw new DaoException("Error while inserting User with Username: " + username + ", Password: "
						+ password + " and Active: " + active);
			}
		} catch (SQLiteException e) {
			dbHandler.closeDatabase();
			Log.e(UserDao.class.getName(), e.getMessage());
			throw new DaoException("Error while inserting User with Username: " + username + ", Password: " + password
					+ " and Active: " + active + " | " + e.getMessage());
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
			if (cursor.getString(1).compareTo(password) == 0) {

				Log.d(UserDao.class.getName(),
						"Created User with Username: " + username + " and Password: " + password + " successfully!");
				user = new User(cursor.getString(0), cursor.getString(1), (cursor.getInt(2) == 1 ? true : false));

				cursor.close();
				// Database not needed anymore
				dbHandler.closeDatabase();

				return user;
			}
			Log.d(UserDao.class.getName(), "Password: " + cursor.getString(1));

		}
		cursor.close();
		// Database not needed anymore
		dbHandler.closeDatabase();

		Log.e(UserDao.class.getName(), "Created User with Username: " + username + ", Password: " + password
				+ " and Active: " + active + " NOT FOUND !");
		throw new DaoException("Created User with Username: " + username + ", Password: " + password + " and Active: "
				+ active + " NOT FOUND !");

	}

	public static User findUserbyUsername(String username) throws DaoException {

		User user = null;
		Log.d(UserDao.class.getName(), "Searching User with Username: " + username);

		dbHandler = DatabaseHandler.getDBHandler(userDaoContext);
		// Express the need for an open Database
		db = dbHandler.openDatabase();

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
			user = new User(cursor.getString(0), cursor.getString(1), (cursor.getInt(2) == 1 ? true : false));
		} else
			Log.d(UserDao.class.getName(), "0 rows selected");

		cursor.close();

		// Database not needed anymore
		dbHandler.closeDatabase();

		return user;
	}

	public static void deleteUser(String username) throws DaoException {

		Log.d(UserDao.class.getName(), "Deleting User with Username: " + username);
		dbHandler = DatabaseHandler.getDBHandler(userDaoContext);

		// Express the need for an open Database
		db = dbHandler.openDatabase();

		Set<Job> jobSet = JobDao.findAllJobsFromUsername(username);
		if (!jobSet.isEmpty()) {
			dbHandler.closeDatabase();
			throw new DaoException(
					"Trying to delete User with Username: " + username + " while there are some jobs from him left.");
		}

		int rowsAffected = db.delete(DatabaseHandler.getUsersTable(),
				DatabaseHandler.getKeyUUsername() + " = '" + username + "'", null);
		Log.d(UserDao.class.getName(), "Rows affected: " + rowsAffected);

		// Database not needed anymore
		dbHandler.closeDatabase();
		Log.d(UserDao.class.getName(), "Deleted User with Username: " + username);

	}

	public static Set<User> findAll() {

		Log.d(UserDao.class.getName(), "Searching all Users");
		Set<User> userSet = new HashSet<User>();
		dbHandler = DatabaseHandler.getDBHandler(userDaoContext);

		// Express the need for an open Database
		db = dbHandler.openDatabase();

		Cursor cursor = db.query(DatabaseHandler.getUsersTable(), userTableColumns, null, null, null, null, null);

		int rows = 0;

		if (cursor.moveToFirst()) {
			while (!cursor.isAfterLast()) {

				userSet.add(new User(cursor.getString(0), cursor.getString(1), (cursor.getInt(2) == 1 ? true : false)));
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

	public static Set<User> findAllActive() {

		Log.d(UserDao.class.getName(), "Searching all Active Users");
		Set<User> userSet = new HashSet<User>();
		dbHandler = DatabaseHandler.getDBHandler(userDaoContext);

		// Express the need for an open Database
		db = dbHandler.openDatabase();

		Cursor cursor = db.query(DatabaseHandler.getUsersTable(), userTableColumns,
				DatabaseHandler.getKeyUActive() + " = " + 1 , null, null, null, null);

		int rows = 0;

		if (cursor.moveToFirst()) {
			while (!cursor.isAfterLast()) {

				userSet.add(new User(cursor.getString(0), cursor.getString(1), (cursor.getInt(2) == 1 ? true : false)));
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
	
	public static void setActive(String username) throws DaoException{

		Log.d(UserDao.class.getName(), "Setting Active: " + username);

		ContentValues values = new ContentValues();
		values.put(DatabaseHandler.getKeyUActive(), 1);

		dbHandler = DatabaseHandler.getDBHandler(userDaoContext);
		// Express the need for an open Database
		db = dbHandler.openDatabase();
		
		User u = UserDao.findUserbyUsername(username);
		if(u ==null){
			dbHandler.closeDatabase();
			throw new DaoException("No such User: "+username+" to set Active");
		}
		if(u.isActive()){
			dbHandler.closeDatabase();
			throw new DaoException("User: "+username+" already Active");
		}
		
		int rowsAffected = db.update(DatabaseHandler.getUsersTable(), values,
				DatabaseHandler.getKeyUUsername() + " = '" + username +"'", null);

		Log.d(JobDao.class.getName(), "Rows affected: " + rowsAffected);

		// Database not needed anymore
		dbHandler.closeDatabase();
		Log.d(JobDao.class.getName(), "User: " + username + " set to Active");

	}

	public static void setInactive(String username) throws DaoException{

		Log.d(UserDao.class.getName(), "Setting Inactive: " + username);

		ContentValues values = new ContentValues();
		values.put(DatabaseHandler.getKeyUActive(), 0);

		dbHandler = DatabaseHandler.getDBHandler(userDaoContext);
		// Express the need for an open Database
		db = dbHandler.openDatabase();
		
		User u = UserDao.findUserbyUsername(username);
		if(u ==null){
			dbHandler.closeDatabase();
			throw new DaoException("No such User: "+username+" to set Inactive");
		}
		if(!u.isActive()){
			dbHandler.closeDatabase();
			throw new DaoException("User: "+username+" already Inactive");
		}
		
		int rowsAffected = db.update(DatabaseHandler.getUsersTable(), values,
				DatabaseHandler.getKeyUUsername() + " = '" + username +"'", null);

		Log.d(JobDao.class.getName(), "Rows affected: " + rowsAffected);

		// Database not needed anymore
		dbHandler.closeDatabase();
		Log.d(JobDao.class.getName(), "User: " + username + " set to Inactive");

	}
	
}
