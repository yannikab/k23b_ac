package k23b.ac.test;

import java.util.Set;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.test.AndroidTestCase;
import k23b.ac.dao.DatabaseHandler;
import k23b.ac.dao.JobDao;
import k23b.ac.dao.UserDao;
import k23b.ac.srv.JobSrv;
import k23b.ac.srv.SrvException;
import k23b.ac.srv.UserSrv;

public class JobSrvTest extends AndroidTestCase {

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

            JobDao j = JobSrv.findById(326);

            assertNull(j);

        } catch (SrvException e) {
            // e.printStackTrace();
            fail(e.getMessage());
        }
    }

    public void testDeleteNonExistentJob() {

        try {

            JobSrv.delete(345);

        } catch (SrvException e) {
            // e.printStackTrace();
            fail(e.getMessage());
        }
    }

    public void testCreateJobWithoutUser() {

        long agentId = 1;

        try {

            JobDao jd = JobSrv.create("957", agentId, "params", true, 60);

            if (jd != null)
                fail("Succesfully created job without corresponding user.");

        } catch (SrvException e) {
            // e.printStackTrace();
        }
    }

    public void testCreateJob() {

        String username = "username";
        String password = "password";
        long agentId = 1;

        try {

            UserDao u = UserSrv.create(username, password);

            JobSrv.create(u.getUsername(), agentId, "params", true, 60);

        } catch (SrvException e) {
            // e.printStackTrace();
            fail(e.getMessage());
        }
    }

    public void testCreateAndFindJob() {

        String username = "username";
        String password = "password";
        long agentId = 1;

        try {

            UserDao u = UserSrv.create(username, password);

            JobDao j1 = JobSrv.create(u.getUsername(), agentId, "params", true, 60);

            JobDao j2 = JobSrv.findById(j1.getId());

            assertEquals(j1.getId(), j2.getId());
            assertEquals(j1.getUsername(), j2.getUsername());
            assertEquals(j1.getParameters(), j2.getParameters());
            assertEquals(j1.getPeriodic(), j2.getPeriodic());
            assertEquals(j1.getPeriod(), j2.getPeriod());
            assertEquals(j1.getTime_assigned(), j2.getTime_assigned());

        } catch (SrvException e) {
            // e.printStackTrace();
            fail(e.getMessage());
        }
    }

    public void testCreateJobAndDeleteUser() {

        String username = "username";
        String password = "password";
        long agentId = 1;

        try {

            UserDao u = UserSrv.create(username, password);

            JobSrv.create(u.getUsername(), agentId, "params", true, 60);

            UserSrv.delete(u.getUsername());

        } catch (SrvException e) {
            // e.printStackTrace();
            fail(e.getMessage());
        }
    }

    public void testFindAllJobsFromNonExistentUser() {

        try {

            Set<JobDao> jobs = JobSrv.findAllJobsFromUsername("326");

            if (!jobs.isEmpty())
                fail("Succesfully found jobs for non existent user.");

        } catch (SrvException e) {
            // e.printStackTrace();
        }
    }

    public void testFindAllJobsFromUser() {

        String username = "username";
        String password = "password";
        long agentId = 1;

        try {

            UserDao u = UserSrv.create(username, password);

            assertEquals(0, JobSrv.findAllJobsFromUsername(username).size());

            JobSrv.create(u.getUsername(), agentId, "params", true, 60);

            assertEquals(1, JobSrv.findAllJobsFromUsername(username).size());

            JobSrv.create(u.getUsername(), agentId, "params", true, 60);

            assertEquals(2, JobSrv.findAllJobsFromUsername(username).size());

            JobSrv.create(u.getUsername() + u.getUsername(), agentId, "params", true, 60);

            assertEquals(2, JobSrv.findAllJobsFromUsername(username).size());

        } catch (SrvException e) {
            // e.printStackTrace();
        }
    }

    public void testCreateManyJobs() {

        String username = "username";
        String password = "password";
        long agentId = 1;

        int count = 100;

        try {

            UserSrv.create(username, password);

            for (int i = 0; i < count; i++)
                JobSrv.create(username, agentId, "params", true, 60);

            assertEquals(count, JobSrv.findAllJobsFromUsername(username).size());

        } catch (SrvException e) {
            // e.printStackTrace();
            fail(e.getMessage());
        }
    }
}
