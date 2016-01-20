package k23b.ac.test;

import java.util.Set;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.test.AndroidTestCase;
import k23b.ac.dao.DaoException;
import k23b.ac.dao.DatabaseHandler;
import k23b.ac.dao.User;
import k23b.ac.dao.UserDao;

public class UserDaoTest extends AndroidTestCase {

    protected void setUp() throws Exception {
        super.setUp();

        Context context = getContext();

        // setContext(context);

        assertNotNull(context);

        DatabaseHandler.setContext(context);
        
        SQLiteDatabase db = DatabaseHandler.getDBHandler().openDatabase();

        DatabaseHandler.getDBHandler().onUpgrade(db, 1, 1);

        DatabaseHandler.getDBHandler().closeDatabase();
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public void testFindNonExistentUser() {

        try {

            User u = UserDao.findUserbyUsername("Yannis");

            assertNull(u);

        } catch (DaoException e) {
            // e.printStackTrace();

            fail(e.getMessage());
        }
    }

    public void testCreateUser() {

        try {

            String username = "Yannis";
            String password = "abcde";

            User u = UserDao.createUser(username, password, false);

            assertNotNull(u);
            assertEquals(username, u.getUsername());
            assertEquals(password, u.getPassword());

        } catch (DaoException e) {
            // e.printStackTrace();
            fail(e.getMessage());
        }
    }

    public void testCreateAndFindAdmin() {

        String username = "Yannis";
        String password = "abcde";

        try {

            UserDao.createUser(username, password, false);

            User u = UserDao.findUserbyUsername(username);

            assertNotNull(u);
            assertEquals(username, u.getUsername());
            assertEquals(password, u.getPassword());

        } catch (DaoException e) {
            // e.printStackTrace();
            fail(e.getMessage());
        }
    }

    public void testCreateDuplicateUser() {

        String username = "Yannis";

        try {

            UserDao.createUser(username, "pass1", false);

            UserDao.createUser(username, "pass2", false);

            fail("Succesfully created two users with the same username.");

        } catch (DaoException e) {
            // e.printStackTrace();
        }
    }

    public void testDeleteNonExistentUser() {

        try {

            UserDao.deleteUser("Yannis");

            fail("Succesfully deleted non existent user.");

        } catch (DaoException e) {
            // e.printStackTrace();
        }
    }

    public void testDeleteExistingUser() {

        String username = "Yannis";
        String password = "abcde";

        try {

            UserDao.createUser(username, password, false);

            assertNotNull(UserDao.findUserbyUsername(username));

            UserDao.deleteUser(username);

            assertNull(UserDao.findUserbyUsername(username));

        } catch (DaoException e) {
            // e.printStackTrace();
            fail(e.getMessage());
        }
    }

    public void testFindAll() {

        try {

            Set<User> users = UserDao.findAll();

            assertNotNull(users);
            assertEquals(0, users.size());

        } catch (DaoException e) {
            // e.printStackTrace();
        }
    }

    public void testCreateAndFindAll() {

        String username = "Yannis";
        String password = "abcde";

        try {

            UserDao.createUser(username, password, false);

            Set<User> users = UserDao.findAll();

            assertNotNull(users);
            assertEquals(1, users.size());

            for (User u : users) {
                assertEquals(username, u.getUsername());
                assertEquals(password, u.getPassword());
            }

        } catch (DaoException e) {
            // e.printStackTrace();
            fail(e.getMessage());
        }
    }

    public void testDeleteAndFindAll() {

        String username = "Yannis";
        String password = "abcde";

        try {

            UserDao.createUser(username, password, false);

            UserDao.deleteUser(username);

            Set<User> users = UserDao.findAll();

            assertNotNull(users);
            assertEquals(0, users.size());

        } catch (DaoException e) {
            // e.printStackTrace();
            fail(e.getMessage());
        }
    }

    public void testCreateManyUsers() {

        int count = 100;

        try {

            for (int i = 0; i < count; i++)
                UserDao.createUser(String.valueOf(i), String.valueOf(i), false);

            Set<User> users = UserDao.findAll();

            assertNotNull(users);
            assertEquals(count, users.size());

        } catch (DaoException e) {
            // e.printStackTrace();
            fail(e.getMessage());
        }
    }
}
