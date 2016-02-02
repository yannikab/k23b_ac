package k23b.am.srv;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.DriverManager;

import org.apache.log4j.PropertyConfigurator;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import junit.framework.Assert;
import k23b.am.ScriptRunner;
import k23b.am.Settings;
import k23b.am.cc.AdminCC;
import k23b.am.cc.RequestCC;
import k23b.am.dao.AdminDao;
import k23b.am.dao.ConnectionSingleton;

public class DbTest {

    @Before
    public void setUp() throws Exception {

        System.out.println("Working directory: " + System.getProperty("user.dir"));

        PropertyConfigurator.configure("log4j.properties");

        try {

            Connection c = DriverManager.getConnection("jdbc:mysql://localhost:3306/amdb_test", "root", "s3cr3t");

            new ScriptRunner(c).runScript("drop-test.sql");
            new ScriptRunner(c).runScript("amdb.sql");

            c.close();

        } catch (Exception e) {
            Assert.fail(e.getMessage());
        }

        Settings.load();

        ConnectionSingleton.setDbUrl("jdbc:mysql://localhost:3306/amdb_test");
        ConnectionSingleton.setDbUser("root");
        ConnectionSingleton.setDbPass("s3cr3t");

        if (Settings.getCacheEnabled()) {
            RequestCC.initCache();
            AdminCC.initCache();
        }
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testCreateRequestsAndAdmin() {

        String deviceName = "deviceName";
        String interfaceIP = "interfaceIP";
        String interfaceMAC = "interfaceMAC";
        String osVersion = "osVersion";
        String nmapVersion = "nmapVersion";

        int requests = 0;

        try {

            for (int i = 0; i < requests; i++) {

                MessageDigest md;

                try {

                    md = MessageDigest.getInstance("SHA-256");

                    String s = new Integer(i).toString();

                    md.update(s.getBytes());

                    byte[] digest = md.digest();

                    RequestSrv.create(bytesToHex(digest), deviceName, interfaceIP, interfaceMAC, osVersion, nmapVersion);

                } catch (NoSuchAlgorithmException e) {
                    e.printStackTrace();
                }
            }

        } catch (SrvException e) {
            // e.printStackTrace();
            Assert.fail(e.getMessage());
        }

        String username = "Yannis";
        String password = "12345";

        AdminDao a;
        try {

            a = AdminSrv.create(username, password);

            Assert.assertNotNull(a);
            Assert.assertEquals(username, a.getUsername());
            Assert.assertEquals(false, a.getActive());

        } catch (SrvException e) {
            // e.printStackTrace();
            Assert.fail(e.getMessage());
        }
    }

    final protected static char[] hexArray = "0123456789ABCDEF".toCharArray();

    public static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }
}
