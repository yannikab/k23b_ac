package k23b.am.srv;

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
import k23b.am.dao.AdminDao;
import k23b.am.dao.ConnectionSingleton;

public class AdminSrvTest {

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

        Settings.load();

        ConnectionSingleton.setDbUrl("jdbc:mysql://localhost:3306/amdb_test");
        ConnectionSingleton.setDbUser("root");
        ConnectionSingleton.setDbPass("s3cr3t");

        if (Settings.getCacheEnabled()) {
            AdminCC.initCache();
        }
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void findNonExistentAdmin() {

        try {

            Assert.assertNull(AdminSrv.findById(326));

        } catch (SrvException e) {
            // e.printStackTrace();
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void testCreateAdmin() {

        try {

            String username = "Yannis";
            String password = "abcde";

            AdminDao a = AdminSrv.create(username, password);

            Assert.assertNotNull(a);
            Assert.assertEquals(username, a.getUsername());
            Assert.assertEquals(false, a.getActive());

        } catch (SrvException e) {
            // e.printStackTrace();
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void testCreateAndFindAdmin() {

        String username = "Yannis";
        String password = "abcde";

        try {

            AdminSrv.create(username, password);

            AdminDao a = AdminSrv.findByUsername(username);

            Assert.assertNotNull(a);
            Assert.assertEquals(username, a.getUsername());
            Assert.assertEquals(false, a.getActive());

        } catch (SrvException e) {
            // e.printStackTrace();
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void testLoginAdmin() {

        String username = "Yannis";
        String password = "abcde";

        try {

            AdminSrv.create(username, password);

            Assert.assertTrue(!AdminSrv.isLoggedIn(username));

            AdminSrv.login(username, password);

            Assert.assertTrue(AdminSrv.isLoggedIn(username));

        } catch (SrvException e) {
            // e.printStackTrace();
            Assert.fail(e.getMessage());
        }
    }
}
