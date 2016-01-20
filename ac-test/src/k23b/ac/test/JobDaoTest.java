package k23b.ac.test;

import java.util.Date;

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

    protected void setUp() throws Exception {
        super.setUp();

        Context context = getContext();

        assertNotNull(context);

        DatabaseHandler.setContext(context);

        SQLiteDatabase db = DatabaseHandler.getDBHandler().openDatabase();

        DatabaseHandler.getDBHandler().onUpgrade(db, 1, 1);

        DatabaseHandler.getDBHandler().closeDatabase();
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public void testFindNonExistentJob() {

        try {

            Job j = JobDao.findJobById(326);

            assertNull(j);

        } catch (DaoException e) {
            // e.printStackTrace();
            fail(e.getMessage());
        }
    }

    public void testDeleteNonExistentJob() {

        try {

            JobDao.deleteJob(345);

            fail("Succesfully deleted non existent job.");

        } catch (DaoException e) {
            // e.printStackTrace();
        }
    }

    public void testCreateJobWithoutUser() {

        int agentId = 1;

        try {

            JobDao.createJob("params", "957", agentId, new Date(System.currentTimeMillis()), true, 60);

            fail("Succesfully created job without corresponding user.");

        } catch (DaoException e) {
            // e.printStackTrace();
        }
    }

    public void testCreateJob() {

        String username = "username";
        String password = "password";
        int agentId = 1;

        try {

            User u = UserDao.createUser(username, password, false);

            JobDao.createJob("params", u.getUsername(), agentId, new Date(System.currentTimeMillis()), true, 60);

        } catch (DaoException e) {
            // e.printStackTrace();
            fail(e.getMessage());
        }
    }

    public void testCreateAndFindJob() {

        String username = "username";
        String password = "password";
        int agentId = 1;

        try {

            User u = UserDao.createUser(username, password, false);

            Job j1 = JobDao.createJob("params", u.getUsername(), agentId, new Date(System.currentTimeMillis()), true, 60);

            Job j2 = JobDao.findJobById(j1.getId());

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
        int agentId = 1;

        try {

            User u = UserDao.createUser(username, password, false);

            JobDao.createJob("params", u.getUsername(), agentId, new Date(System.currentTimeMillis()), true, 60);

            UserDao.deleteUser(u.getUsername());

            fail("Succesfully deleted user which has a corresponding job.");

        } catch (DaoException e) {
            // e.printStackTrace();
        }
    }

    public void testFindAllJobsFromNonExistentUser() {

        try {

            JobDao.findAllJobsFromUsername("326");

            fail("Succesfully found jobs for non existent user.");

        } catch (DaoException e) {
            // e.printStackTrace();
        }
    }

    public void testFindAllJobsFromUser() {

        String username = "username";
        String password = "password";
        int agentId = 1;

        try {

            User u = UserDao.createUser(username, password, false);

            assertEquals(0, JobDao.findAllJobsFromUsername(username).size());

            JobDao.createJob("params", u.getUsername(), agentId, new Date(System.currentTimeMillis()), true, 60);

            assertEquals(1, JobDao.findAllJobsFromUsername(username).size());

            JobDao.createJob("params", u.getUsername(), agentId, new Date(System.currentTimeMillis()), true, 60);

            assertEquals(2, JobDao.findAllJobsFromUsername(username).size());

            JobDao.createJob("params", u.getUsername() + u.getUsername(), agentId, new Date(System.currentTimeMillis()), true, 60);

            assertEquals(2, JobDao.findAllJobsFromUsername(username).size());

        } catch (DaoException e) {
            // e.printStackTrace();
        }
    }

    public void testCreateManyJobs() {

        String username = "username";
        String password = "password";
        int agentId = 1;

        int count = 100;

        try {

            UserDao.createUser(username, password, false);

            for (int i = 0; i < count; i++)
                JobDao.createJob("params", username, agentId, new Date(System.currentTimeMillis()), true, 60);

            assertEquals(count, JobDao.findAllJobsFromUsername(username).size());

        } catch (DaoException e) {
            // e.printStackTrace();
            fail(e.getMessage());
        }
    }
}
