package k23b.ac.test;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.test.AndroidTestCase;
import k23b.ac.db.dao.DatabaseHandler;
import k23b.ac.db.dao.JobDao;
import k23b.ac.db.dao.UserDao;
import k23b.ac.db.srv.SrvException;
import k23b.ac.db.srv.UserSrv;
import k23b.ac.rest.Job;
import k23b.ac.rest.User;
import k23b.ac.util.JobDaoFactory;
import k23b.ac.util.UserDaoFactory;

public class UserSrvTest extends AndroidTestCase {

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

            UserDao u = UserSrv.find("Yannis");

            assertNull(u);

        } catch (SrvException e) {
            // e.printStackTrace();

            fail(e.getMessage());
        }
    }

    public void testCreateUser() {

        try {

            String username = "Yannis";
            String password = "abcde";

            UserDao u = UserSrv.create(username, password);

            assertNotNull(u);
            assertEquals(username, u.getUsername());
            assertEquals(password, u.getPassword());

        } catch (SrvException e) {
            // e.printStackTrace();
            fail(e.getMessage());
        }
    }

    public void testCreateAndFindUser() {

        String username = "Yannis";
        String password = "abcde";

        try {

            UserSrv.create(username, password);

            UserDao u = UserSrv.find(username);

            assertNotNull(u);
            assertEquals(username, u.getUsername());
            assertEquals(password, u.getPassword());

        } catch (SrvException e) {
            // e.printStackTrace();
            fail(e.getMessage());
        }
    }

    public void testCreateDuplicateUser() {

        String username = "Yannis";

        try {

            UserSrv.create(username, "pass1");

            UserDao ud = UserSrv.create(username, "pass2");

            if (ud != null)
                fail("Succesfully created two users with the same username.");

        } catch (SrvException e) {
            // e.printStackTrace();
        }
    }

    public void testDeleteNonExistentUser() {

        try {

            UserSrv.delete("Yannis");

        } catch (SrvException e) {
            // e.printStackTrace();
            fail(e.getMessage());
        }
    }

    public void testDeleteExistingUser() {

        String username = "Yannis";
        String password = "abcde";

        try {

            UserSrv.create(username, password);

            assertNotNull(UserSrv.find(username));

            UserSrv.delete(username);

            assertNull(UserSrv.find(username));

        } catch (SrvException e) {
            // e.printStackTrace();
            fail(e.getMessage());
        }
    }
    
    public void testTryDelete(){
    	
    	try{
    		
    		UserSrv.create("Thanos", "12345");
    		
    		assertNotNull(UserSrv.find("Thanos"));
    		
    		UserSrv.tryDelete("Thanos");
    		
    		assertNull(UserSrv.find("Thanos"));

    		UserDao ud = UserDaoFactory.fromUser(new User("Yannis","12345"));
    		assertNotNull(ud);
    		
    		List<JobDao> jobList = new LinkedList<JobDao>();
    		Job j = new Job();
    		j.setJobId(-1);
    		j.setAgentId(1);
    		j.setParams("nothing");
    		j.setTimeAssigned(new Date());
    		j.setPeriodic(false);
    		j.setPeriod(0);
    		JobDao jd = JobDaoFactory.fromJob(j);
    		assertNotNull(jd);
    		jobList.add(jd);
    		
    		UserSrv.createUserWithJobs(ud, jobList);
    		
    		assertNotNull(UserSrv.find("Yannis"));
    		
    		UserSrv.tryDelete("Yannis");
    		
    		assertNotNull(UserSrv.find("Yannis"));
    		
    		
    	}catch (SrvException e){
    		fail(e.getMessage());
    	}
    	
    }

    public void testFindAll() {

        try {

            Set<UserDao> users = UserSrv.findAll();

            assertNotNull(users);
            assertEquals(0, users.size());

        } catch (SrvException e) {
            // e.printStackTrace();
        }
    }

    public void testCreateAndFindAll() {

        String username = "Yannis";
        String password = "abcde";

        try {

            UserSrv.create(username, password);

            Set<UserDao> users = UserSrv.findAll();

            assertNotNull(users);
            assertEquals(1, users.size());

            for (UserDao u : users) {
                assertEquals(username, u.getUsername());
                assertEquals(password, u.getPassword());
            }

        } catch (SrvException e) {
            // e.printStackTrace();
            fail(e.getMessage());
        }
    }

    public void testDeleteAndFindAll() {

        String username = "Yannis";
        String password = "abcde";

        try {

            UserSrv.create(username, password);

            UserSrv.delete(username);

            Set<UserDao> users = UserSrv.findAll();

            assertNotNull(users);
            assertEquals(0, users.size());

        } catch (SrvException e) {
            // e.printStackTrace();
            fail(e.getMessage());
        }
    }

    public void testCreateManyUsers() {

        int count = 100;

        try {

            for (int i = 0; i < count; i++)
                UserSrv.create(String.valueOf(i), String.valueOf(i));

            Set<UserDao> users = UserSrv.findAll();

            assertNotNull(users);
            assertEquals(count, users.size());

        } catch (SrvException e) {
            // e.printStackTrace();
            fail(e.getMessage());
        }
    }
}
