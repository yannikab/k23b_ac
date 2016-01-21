package k23b.ac.test;

import java.util.Set;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.test.AndroidTestCase;
import k23b.ac.dao.DatabaseHandler;
import k23b.ac.dao.UserDao;
import k23b.ac.srv.SrvException;
import k23b.ac.srv.UserSrv;

public class UserThreadTest extends AndroidTestCase {

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

    public void testProduceConsumeUsers() {

        Thread producer = new Thread(new Runnable() {

            @Override
            public void run() {

                int count = 1000;

                try {

                    for (int i = 0; i < count; i++)
                        UserSrv.create(String.valueOf(i), String.valueOf(i));

                } catch (SrvException e) {
                    // e.printStackTrace();
                    fail(e.getMessage());
                }
            }
        });

        Thread consumer = new Thread(new Runnable() {

            @Override
            public void run() {

                while (true) {

                    try {

                        Thread.sleep(500);

                        Set<UserDao> allUsers = UserSrv.findAll();

                        for (UserDao u : allUsers)
                            UserSrv.delete(u.getUsername());

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

        } catch (InterruptedException e) {
            // e.printStackTrace();
        }
    }
}
