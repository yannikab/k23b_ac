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
import k23b.am.cc.AgentCC;
import k23b.am.cc.RequestCC;
import k23b.am.dao.AdminDao;
import k23b.am.dao.AgentDao;
import k23b.am.dao.ConnectionSingleton;
import k23b.am.dao.RequestDao;
import k23b.am.dao.RequestStatus;

public class AgentSrvTest {

    @Before
    public void setUp() throws Exception {

        PropertyConfigurator.configure("log4j.properties");

        try {

            Connection c = DriverManager.getConnection("jdbc:mysql://localhost:3306/amdb", "root", "s3cr3t");

            new ScriptRunner(c).runScript("src/main/resources/sql/drop.sql");
            new ScriptRunner(c).runScript("src/main/resources/sql/amdb.sql");

            c.close();

        } catch (Exception e) {
            Assert.fail(e.getMessage());
        }

        Settings.load();
        
        ConnectionSingleton.setDbUrl(Settings.getDbUrl());
        ConnectionSingleton.setDbUser(Settings.getDbUser());
        ConnectionSingleton.setDbPass(Settings.getDbPass());

        if (Settings.getCacheEnabled()) {
            RequestCC.initCache();
            AdminCC.initCache();
            AgentCC.initCache();
        }
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void findNonExistentAgent() {

        try {

            Assert.assertNull(AgentSrv.findById(326));

        } catch (SrvException e) {
            // e.printStackTrace();
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void testCreateAgent() {

        try {

            RequestDao request = createRequest();

            AdminDao admin = createAdmin();

            AgentDao agent = AgentSrv.create(request.getRequestId(), admin.getAdminId());

            Assert.assertNotNull(agent);
            Assert.assertEquals(request.getRequestId(), agent.getRequestId());
            Assert.assertEquals(admin.getAdminId(), agent.getAdminId());
            Assert.assertNull(agent.getTimeJobRequest());

        } catch (SrvException e) {
            // e.printStackTrace();
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void testCreateAgentWithoutRequest() {

        try {

            AdminDao AdminDao = createAdmin();

            AgentSrv.create(326, AdminDao.getAdminId());

            Assert.fail("Created agent without request.");

        } catch (SrvException e) {
            // e.printStackTrace();
            System.out.println(e.getMessage());
        }
    }

    @Test
    public void testCreateAgentWithoutAdmin() {

        try {

            RequestDao request = createRequest();

            AgentSrv.create(request.getRequestId(), 957);

            Assert.fail("Created agent without admin.");

        } catch (SrvException e) {
            // e.printStackTrace();
            System.out.println(e.getMessage());
        }
    }

    private RequestDao createRequest() throws SrvException {

        String hash = "AA";
        String deviceName = "deviceName";
        String interfaceIP = "interfaceIP";
        String interfaceMAC = "interfaceMAC";
        String osVersion = "osVersion";
        String nmapVersion = "nmapVersion";

        return RequestSrv.create(hash, deviceName, interfaceIP, interfaceMAC, osVersion, nmapVersion);
    }

    private AdminDao createAdmin() throws SrvException {

        String username = "Yannis";
        String password = "abcde";

        AdminSrv.create(username, password);

        AdminSrv.login(username, password);

        return AdminSrv.findByUsername(username);
    }

    @Test
    public void testAcceptRequest() {

        String hash = "AA";
        String deviceName = "deviceName";
        String interfaceIP = "interfaceIP";
        String interfaceMAC = "interfaceMAC";
        String osVersion = "osVersion";
        String nmapVersion = "nmapVersion";

        try {

            AdminDao ad = createAdmin();

            RequestDao rd = RequestSrv.create(hash, deviceName, interfaceIP, interfaceMAC, osVersion, nmapVersion);

            Assert.assertTrue(rd.getRequestStatus() == RequestStatus.PENDING);

            AgentSrv.create(rd.getRequestId(), ad.getAdminId());

            rd = RequestSrv.findById(rd.getRequestId());

            Assert.assertTrue(rd.getRequestStatus() == RequestStatus.ACCEPTED);

        } catch (SrvException e) {
            // e.printStackTrace();
            Assert.fail(e.getMessage());
        }
    }
}
