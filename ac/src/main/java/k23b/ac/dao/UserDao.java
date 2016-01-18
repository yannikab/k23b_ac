package k23b.ac.dao;

import java.util.HashSet;
import java.util.Set;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.util.Log;
import k23b.ac.dao.DaoException;

public class UserDao {

	private SQLiteDatabase db;
	private DatabaseHandler dbHandler;

	private String[] userTableColumns = { DatabaseHandler.getKeyUUsername(), DatabaseHandler.getKeyUPassword() };

	public UserDao(Context context) {
		dbHandler = DatabaseHandler.getDBHandler(context);
	}

	public User createUser(String username, String password) throws DaoException {

		Log.d(UserDao.class.getName(), "Creating User with Username: " + username + " and Password: " + password);

		User user = null;
		ContentValues values = new ContentValues();
		values.put(DatabaseHandler.getKeyUUsername(), username);
		values.put(DatabaseHandler.getKeyUPassword(), password);

		// Express the need for an open Database
		db = dbHandler.openDatabase();

		try {
			long rowId = db.insertOrThrow(DatabaseHandler.getUsersTable(), null, values);

			if (rowId < 0) {
				dbHandler.closeDatabase();
				Log.e(UserDao.class.getName(),
						"Error while inserting User with Username: " + username + " and Password: " + password);
				throw new DaoException(
						"Error while inserting User with Username: " + username + " and Password: " + password);
			}
		} catch (SQLiteException e) {
			dbHandler.closeDatabase();
			Log.e(UserDao.class.getName(), e.getMessage());
			throw new DaoException("Error while inserting User with Username: " + username + " and Password: "
					+ password + " | " + e.getMessage());
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
				user = new User(cursor.getString(0), cursor.getString(1));

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

		Log.e(UserDao.class.getName(),
				"Created User with Username: " + username + " and Password: " + password + " NOT FOUND !");
		throw new DaoException(
				"Created User with Username: " + username + " and Password: " + password + " NOT FOUND !");

	}

	public User findUserbyUsername(String username) throws DaoException {

		User user = null;
		Log.d(UserDao.class.getName(), "Searching User with Username: " + username);
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
			user = new User(cursor.getString(0), cursor.getString(1));
		} else
			Log.d(UserDao.class.getName(), "0 rows selected");

		cursor.close();

		// Database not needed anymore
		dbHandler.closeDatabase();

		return user;
	}

	public void deleteUser(String username) {

		Log.d(UserDao.class.getName(), "Deleting User with Username: " + username);

		// Express the need for an open Database
		db = dbHandler.openDatabase();

		int rowsAffected = db.delete(DatabaseHandler.getUsersTable(),
				DatabaseHandler.getKeyUUsername() + " = '" + username + "'", null);
		Log.d(UserDao.class.getName(), "Rows affected: " + rowsAffected);

		// Database not needed anymore
		dbHandler.closeDatabase();
		Log.d(UserDao.class.getName(), "Deleted User with Username: " + username);

	}

	public Set<User> findAll() {

		Log.d(UserDao.class.getName(), "Searching all Users");
		Set<User> userSet = new HashSet<User>();

		// Express the need for an open Database
		db = dbHandler.openDatabase();

		Cursor cursor = db.query(DatabaseHandler.getUsersTable(), userTableColumns, null, null, null, null, null);

		int rows = 0;

		if (cursor.moveToFirst()) {
			while (!cursor.isAfterLast()) {

				userSet.add(new User(cursor.getString(0), cursor.getString(1)));
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

}
