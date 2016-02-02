package k23b.am.srv;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Random;

import org.apache.log4j.PropertyConfigurator;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import junit.framework.Assert;
import k23b.am.ScriptRunner;
import k23b.am.dao.RequestDao;

public class ThreadTest {

    @Before
    public void setUp() throws Exception {

        PropertyConfigurator.configure("log4j.properties");

        try {

            Connection c = DriverManager.getConnection("jdbc:mysql://localhost:3306/amdb_test", "root", "s3cr3t");

            new ScriptRunner(c).runScript("drop-test.sql");
            new ScriptRunner(c).runScript("amdb.sql");

            c.close();

        } catch (Exception e) {
            Assert.fail(e.getMessage());
        }
    }

    @After
    public void tearDown() throws Exception {
    }

    private static Random random = new Random(System.currentTimeMillis());

    private String hashGenerator() {

        StringBuilder sb = new StringBuilder();

        while (sb.length() < 64) {
            sb.append(Integer.toHexString(random.nextInt()));
        }

        sb.setLength(64);

        return sb.toString();
    }

    @Test
    public void twoThreadsCreatingRequests() {

        Thread t1 = new Thread(new Runnable() {

            @Override
            public void run() {

                for (int i = 0; i < 100; i++) {
                    try {

                        createRequest(hashGenerator(), "1");

                        Thread.sleep(100);

                    } catch (SrvException e) {
                        // e.printStackTrace();

                    } catch (InterruptedException e) {
                        // e.printStackTrace();
                        Assert.fail("Thread interrupted.");
                    }
                }
            }
        });

        Thread t2 = new Thread(new Runnable() {

            @Override
            public void run() {

                for (int i = 0; i < 100; i++) {
                    try {

                        createRequest(hashGenerator(), "2");

                        Thread.sleep(100);

                    } catch (SrvException e) {
                        // e.printStackTrace();

                    } catch (InterruptedException e) {
                        // e.printStackTrace();
                        Assert.fail("Thread interrupted.");
                    }
                }
            }
        });

        t1.start();
        t2.start();

        try {
            t1.join();
            t2.join();
        } catch (InterruptedException e) {
            // e.printStackTrace();
        }
    }

    private RequestDao createRequest(String hash, String deviceName) throws SrvException {

        String interfaceIP = "interfaceIP";
        String interfaceMAC = "interfaceMAC";
        String osVersion = "osVersion";
        String nmapVersion = "nmapVersion";

        return RequestSrv.create(hash, deviceName, interfaceIP, interfaceMAC, osVersion, nmapVersion);
    }
}
