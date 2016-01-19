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

    private Context context;

    // private Context getTestContext() {
    //
    // try {
    //
    // Method getTestContext = ServiceTestCase.class.getMethod("getTestContext");
    //
    // return (Context) getTestContext.invoke(this);
    //
    // } catch (final Exception exception) {
    // exception.printStackTrace();
    // return null;
    // }
    // }

    protected void setUp() throws Exception {
        super.setUp();

        context = getContext();

        // setContext(context);

        assertNotNull(context);

        SQLiteDatabase db = DatabaseHandler.getDBHandler(context).openDatabase();

        DatabaseHandler.getDBHandler(context).onUpgrade(db, 1, 1);

        DatabaseHandler.getDBHandler(context).closeDatabase();
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public void testFindNonExistentUser() {

        UserDao ud = new UserDao(context);

        try {

            User u = ud.findUserbyUsername("Yannis");

            assertNull(u);

        } catch (DaoException e) {
            // e.printStackTrace();

            fail(e.getMessage());
        }
    }

    public void testCreateUser() {

        UserDao ud = new UserDao(context);

        try {

            String username = "Yannis";
            String password = "abcde";

            User u = ud.createUser(username, password);

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

        UserDao ud = new UserDao(context);

        try {

            ud.createUser(username, password);

            User u = ud.findUserbyUsername(username);

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

        UserDao ud = new UserDao(context);

        try {

            ud.createUser(username, "pass1");

            ud.createUser(username, "pass2");

            fail("Succesfully created two users with the same username.");

        } catch (DaoException e) {
            // e.printStackTrace();
        }
    }

    public void testDeleteNonExistentUser() {

        UserDao ud = new UserDao(context);

        try {

            ud.deleteUser("Yannis");

            fail("Succesfully deleted non existent user.");

        } catch (DaoException e) {
            // e.printStackTrace();
        }
    }

    public void testDeleteExistingUser() {

        String username = "Yannis";
        String password = "abcde";

        UserDao ud = new UserDao(context);

        try {

            ud.createUser(username, password);

            assertNotNull(ud.findUserbyUsername(username));

            ud.deleteUser(username);

            assertNull(ud.findUserbyUsername(username));

        } catch (DaoException e) {
            // e.printStackTrace();
            fail(e.getMessage());
        }
    }

    public void testFindAll() {

        UserDao ud = new UserDao(context);

        try {

            Set<User> users = ud.findAll();

            assertNotNull(users);
            assertEquals(0, users.size());

        } catch (DaoException e) {
            // e.printStackTrace();
        }
    }

    public void testCreateAndFindAll() {

        String username = "Yannis";
        String password = "abcde";

        UserDao ud = new UserDao(context);

        try {

            ud.createUser(username, password);

            Set<User> users = ud.findAll();

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

        UserDao ud = new UserDao(context);

        try {

            ud.createUser(username, password);

            ud.deleteUser(username);

            Set<User> users = ud.findAll();

            assertNotNull(users);
            assertEquals(0, users.size());

        } catch (DaoException e) {
            // e.printStackTrace();
            fail(e.getMessage());
        }
    }

    public void testCreateManyUsers() {

        UserDao ud = new UserDao(context);

        int count = 100;

        try {

            for (int i = 0; i < count; i++)
                ud.createUser(String.valueOf(i), String.valueOf(i));

            Set<User> users = ud.findAll();

            assertNotNull(users);
            assertEquals(count, users.size());

        } catch (DaoException e) {
            // e.printStackTrace();
            fail(e.getMessage());
        }
    }
}
