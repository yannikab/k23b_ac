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
import k23b.am.cc.UserCC;
import k23b.am.dao.AdminDao;
import k23b.am.dao.ConnectionSingleton;
import k23b.am.dao.UserDao;

public class UserSrvTest {

    @Before
    public void setUp() throws Exception {

        PropertyConfigurator.configure("log4j.properties");

        try {

            Connection c = DriverManager.getConnection("jdbc:mysql://localhost:3306/amdb", "root", "s3cr3t");

            new ScriptRunner(c).runScript("drop.sql");
            new ScriptRunner(c).runScript("amdb.sql");

            c.close();

        } catch (Exception e) {
            Assert.fail(e.getMessage());
        }

        Settings.load();

        ConnectionSingleton.setDbUrl(Settings.getDbUrl());
        ConnectionSingleton.setDbUser(Settings.getDbUser());
        ConnectionSingleton.setDbPass(Settings.getDbPass());

        if (Settings.getCacheEnabled()) {
            AdminCC.initCache();
            UserCC.initCache();
        }

        createAdmin();
    }

    @After
    public void tearDown() throws Exception {
    }

    private AdminDao ad;

    @Test
    public void findNonExistentUser() {

        try {

            Assert.assertNull(UserSrv.findById(326));

        } catch (SrvException e) {
            // e.printStackTrace();
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void testCreateUser() {

        try {

            String username = "Yannis";
            String password = "abcde";

            UserDao u = UserSrv.create(username, password);

            Assert.assertNotNull(u);
            Assert.assertEquals(username, u.getUsername());
            Assert.assertEquals(false, u.getActive());
            Assert.assertEquals(0, u.getAdminId());
            Assert.assertTrue(!UserSrv.isAccepted(username));

        } catch (SrvException e) {
            // e.printStackTrace();
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void testCreateAndFindUser() {

        String username = "Yannis";
        String password = "abcde";

        try {

            UserSrv.create(username, password);

            UserDao a = UserSrv.findByUsername(username);

            Assert.assertNotNull(a);
            Assert.assertEquals(username, a.getUsername());
            Assert.assertEquals(false, a.getActive());
            Assert.assertEquals(0, a.getAdminId());
            Assert.assertTrue(!UserSrv.isAccepted(username));

        } catch (SrvException e) {
            // e.printStackTrace();
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void testLoginUser() {

        String username = "Yannis";
        String password = "abcde";

        try {

            UserSrv.create(username, password);

            Assert.assertTrue(!UserSrv.isLoggedIn(username));

            UserSrv.login(username, password);

            Assert.assertTrue(UserSrv.isLoggedIn(username));

        } catch (SrvException e) {
            // e.printStackTrace();
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void testAcceptUser() {

        String username = "Yannis";
        String password = "abcde";

        try {

            UserSrv.create(username, password);

            Assert.assertTrue(!UserSrv.isAccepted(username));

            UserSrv.accept(username, ad.getAdminId());

            Assert.assertTrue(UserSrv.isAccepted(username));

        } catch (SrvException e) {
            // e.printStackTrace();
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void testRejectUser() {

        String username = "Yannis";
        String password = "abcde";

        try {

            UserSrv.create(username, password);
            UserSrv.accept(username, ad.getAdminId());

            UserSrv.reject(username, ad.getAdminId());

            Assert.assertTrue(!UserSrv.isAccepted(username));

        } catch (SrvException e) {
            // e.printStackTrace();
            Assert.fail(e.getMessage());
        }
    }

    private void createAdmin() throws SrvException {

        String username = "Admin";
        String password = "abcde";

        AdminSrv.create(username, password);

        AdminSrv.login(username, password);

        ad = AdminSrv.findByUsername(username);
    }
}
