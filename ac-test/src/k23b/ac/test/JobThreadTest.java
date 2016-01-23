package k23b.ac.test;

import java.util.Set;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.test.AndroidTestCase;
import k23b.ac.db.dao.DatabaseHandler;
import k23b.ac.db.dao.JobDao;
import k23b.ac.db.dao.UserDao;
import k23b.ac.db.srv.JobSrv;
import k23b.ac.db.srv.SrvException;
import k23b.ac.db.srv.UserSrv;

public class JobThreadTest extends AndroidTestCase {

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

    public void testProduceConsumeJobs() throws SrvException {

        final String username = "user";
        final String password = "pass";

        UserSrv.create(username, password);

        Thread producer = new Thread(new Runnable() {

            @Override
            public void run() {

                int count = 300;

                try {

                    for (int i = 0; i < count; i++)
                        JobSrv.create(username, 1, "params", false, i);

                } catch (SrvException e) {
                    // e.printStackTrace();
                    fail(e.getMessage());
                }
            }
        });

        Thread consumer = new Thread(new Runnable() {

            @Override
            public void run() {
                int count = 300;

                for (int i = 0; i < count; i++) {

                    try {

                        Thread.sleep(100);

                        Set<JobDao> allJobs = JobSrv.findAllJobsFromUsername(username);

                        for (JobDao j : allJobs)
                            JobSrv.delete(j.getId());

                    } catch (SrvException e) {
                        // e.printStackTrace();
                        fail(e.getMessage());
                    } catch (InterruptedException e) {
                        // e.printStackTrace();
                        fail(e.getMessage());
                    }
                }
            }
        });

        producer.start();
        consumer.start();

        try {

            producer.join();
            consumer.join();

        } catch (InterruptedException e) {
            // e.printStackTrace();
        }
    }

    public void testCreateDeleteJobsWithUserConsumer() throws SrvException {

        final String username = "user";
        final String password = "pass";

        Thread producer = new Thread(new Runnable() {

            @Override
            public void run() {

                int count = 100;

                try {

                    for (int i = 0; i < count; i++) {

                        UserDao u = UserSrv.create(username, password);

                        if (u != null) {

                            JobDao j = JobSrv.create(u.getUsername(), 1, "params", false, 60);

                            if (j != null)
                                JobSrv.delete(j.getId());
                        }

                        UserSrv.delete(u.getUsername());
                    }

                } catch (SrvException e) {
                    // e.printStackTrace();
                    fail(e.getMessage());
                }
            }
        });

        Thread consumer = new Thread(new Runnable() {

            @Override
            public void run() {

                int count = 100;

                try {

                    for (int i = 0; i < count; i++) {

                        Thread.sleep(100);

                        if (UserSrv.find(username) != null)
                            UserSrv.delete(username);
                    }

                } catch (SrvException e) {
                    // e.printStackTrace();
                    fail(e.getMessage());
                } catch (InterruptedException e) {
                    // e.printStackTrace();
                    fail(e.getMessage());
                }
            }
        });

        producer.start();
        consumer.start();

        try {

            producer.join();
            consumer.join();

        } catch (InterruptedException e) {
            // e.printStackTrace();
        }
    }
}
