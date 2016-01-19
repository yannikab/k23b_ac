package k23b.ac.test;

import java.util.Date;
import java.util.Set;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.test.AndroidTestCase;
import k23b.ac.dao.DaoException;
import k23b.ac.dao.DatabaseHandler;
import k23b.ac.dao.Job;
import k23b.ac.dao.JobDao;
import k23b.ac.dao.User;
import k23b.ac.dao.UserDao;

public class JobDaoTest extends AndroidTestCase {

    private Context context;

    protected void setUp() throws Exception {
        super.setUp();

        context = getContext();

        assertNotNull(context);

        SQLiteDatabase db = DatabaseHandler.getDBHandler(context).openDatabase();

        DatabaseHandler.getDBHandler(context).onUpgrade(db, 1, 1);

        DatabaseHandler.getDBHandler(context).closeDatabase();
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public void testFindNonExistentJob() {

        JobDao jd = new JobDao(context);

        try {

            Job j = jd.findJobById(326);

            assertNull(j);

        } catch (DaoException e) {
            // e.printStackTrace();
            fail(e.getMessage());
        }
    }

    public void testDeleteNonExistentJob() {

        JobDao jd = new JobDao(context);

        try {

            jd.deleteJob(345);

            fail("Succesfully deleted non existent job.");

        } catch (DaoException e) {
            // e.printStackTrace();
        }
    }

    public void testCreateJobWithoutUser() {

        JobDao jd = new JobDao(context);

        try {

            jd.createJob("params", "957", new Date(System.currentTimeMillis()), true, 60);

            fail("Succesfully created job without corresponding user.");

        } catch (DaoException e) {
            // e.printStackTrace();
        }
    }

    public void testCreateJob() {

        String username = "username";
        String password = "password";

        UserDao ud = new UserDao(context);
        JobDao jd = new JobDao(context);

        try {

            User u = ud.createUser(username, password);

            jd.createJob("params", u.getUsername(), new Date(System.currentTimeMillis()), true, 60);

        } catch (DaoException e) {
            // e.printStackTrace();
            fail(e.getMessage());
        }
    }

    public void testCreateAndFindJob() {

        String username = "username";
        String password = "password";

        UserDao ud = new UserDao(context);
        JobDao jd = new JobDao(context);

        try {

            User u = ud.createUser(username, password);

            Job j1 = jd.createJob("params", u.getUsername(), new Date(System.currentTimeMillis()), true, 60);

            Job j2 = jd.findJobById(j1.getId());

            assertEquals(j1.getId(), j2.getId());
            assertEquals(j1.getUsername(), j2.getUsername());
            assertEquals(j1.getParameters(), j2.getParameters());
            assertEquals(j1.getPeriodic(), j2.getPeriodic());
            assertEquals(j1.getPeriod(), j2.getPeriod());
            assertEquals(j1.getTime_assigned(), j2.getTime_assigned());

        } catch (DaoException e) {
            // e.printStackTrace();
            fail(e.getMessage());
        }
    }

    public void testCreateJobAndDeleteUser() {

        String username = "username";
        String password = "password";

        UserDao ud = new UserDao(context);
        JobDao jd = new JobDao(context);

        try {

            User u = ud.createUser(username, password);

            jd.createJob("params", u.getUsername(), new Date(System.currentTimeMillis()), true, 60);

            ud.deleteUser(username);

            fail("Succesfully deleted user which has a corresponding job.");

        } catch (DaoException e) {
            // e.printStackTrace();
        }
    }

    public void testFindAllJobsFromNonExistentUser() {

        JobDao jd = new JobDao(context);

        try {

            jd.findAllJobsFromUsername("326");

            fail("Succesfully found jobs for non existent user.");

        } catch (DaoException e) {
            // e.printStackTrace();
        }
    }

    public void testFindAllJobsFromUser() {

        String username = "username";
        String password = "password";

        UserDao ud = new UserDao(context);
        JobDao jd = new JobDao(context);

        try {

            User u = ud.createUser(username, password);

            assertEquals(0, jd.findAllJobsFromUsername(username).size());

            jd.createJob("params", u.getUsername(), new Date(System.currentTimeMillis()), true, 60);

            assertEquals(1, jd.findAllJobsFromUsername(username).size());

            jd.createJob("params", u.getUsername(), new Date(System.currentTimeMillis()), true, 60);

            assertEquals(2, jd.findAllJobsFromUsername(username).size());

            jd.createJob("params", u.getUsername() + u.getUsername(), new Date(System.currentTimeMillis()), true, 60);

            assertEquals(2, jd.findAllJobsFromUsername(username).size());

        } catch (DaoException e) {
            // e.printStackTrace();
        }
    }

    public void testCreateManyJobs() {

        String username = "username";
        String password = "password";

        UserDao ud = new UserDao(context);
        JobDao jd = new JobDao(context);

        int count = 100;

        try {

            ud.createUser(username, password);

            for (int i = 0; i < count; i++)
                jd.createJob("params", username, new Date(System.currentTimeMillis()), true, 60);

            assertEquals(count, jd.findAllJobsFromUsername(username).size());

        } catch (DaoException e) {
            // e.printStackTrace();
            fail(e.getMessage());
        }
    }
}
