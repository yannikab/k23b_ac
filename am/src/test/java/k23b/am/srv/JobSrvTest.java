package k23b.am.srv;

import java.sql.Connection;
import java.sql.DriverManager;
import java.time.Instant;
import java.util.Date;

import org.apache.log4j.PropertyConfigurator;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import junit.framework.Assert;
import k23b.am.ScriptRunner;
import k23b.am.Settings;
import k23b.am.cc.AdminCC;
import k23b.am.cc.AgentCC;
import k23b.am.cc.JobCC;
import k23b.am.cc.RequestCC;
import k23b.am.dao.AdminDao;
import k23b.am.dao.AgentDao;
import k23b.am.dao.ConnectionSingleton;
import k23b.am.dao.JobDao;
import k23b.am.dao.RequestDao;

public class JobSrvTest {

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
            RequestCC.initCache();
            AdminCC.initCache();
            AgentCC.initCache();
            JobCC.initCache();
        }
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void findNonExistentJob() {

        try {

            Assert.assertNull(JobSrv.findById(957));

        } catch (SrvException e) {
            // e.printStackTrace();
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void testCreateJob() {

        try {

            String params = "params";
            boolean periodic = true;
            int period = 300;
            Date date = Date.from(Instant.now());

            AgentDao agent = createAgent();

            JobDao job = JobSrv.create(agent.getAgentId(), agent.getAdminId(), date, params, periodic, period);

            Assert.assertNotNull(job);
            Assert.assertEquals(job.getAgentId(), agent.getAgentId());
            Assert.assertEquals(job.getAdminId(), agent.getAdminId());
            // Assert.assertEquals(date, job.getTimeAssigned());
            Assert.assertEquals(params, job.getParams());
            Assert.assertEquals(periodic, job.getPeriodic());
            Assert.assertEquals(period, job.getPeriod());
            Assert.assertEquals(false, job.getSent());

        } catch (SrvException e) {
            // e.printStackTrace();
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void testCreateAndFindJob() {

        try {

            String params = "params";
            boolean periodic = true;
            int period = 300;
            Date date = Date.from(Instant.now());

            AgentDao agent = createAgent();

            JobDao job1 = JobSrv.create(agent.getAgentId(), agent.getAdminId(), date, params + 1, periodic, period);
            job1 = JobSrv.create(agent.getAgentId(), agent.getAdminId(), date, params + 2, periodic, period);
            job1 = JobSrv.create(agent.getAgentId(), agent.getAdminId(), date, params + 3, periodic, period);

            JobDao job2 = JobSrv.findById(job1.getJobId());

            Assert.assertNotNull(job2);
            Assert.assertEquals(job2.getJobId(), job1.getJobId());
            Assert.assertEquals(job2.getAgentId(), job1.getAgentId());
            Assert.assertEquals(job2.getAdminId(), job1.getAdminId());
            Assert.assertEquals(params + 3, job1.getParams());
            Assert.assertEquals(job2.getTimeAssigned(), job1.getTimeAssigned());
            Assert.assertEquals(job2.getPeriodic(), job1.getPeriodic());
            Assert.assertEquals(job2.getPeriod(), job1.getPeriod());
            Assert.assertEquals(job2.getSent(), job1.getSent());

        } catch (SrvException e) {
            // e.printStackTrace();
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void testCreateJobWithoutAgent() {

        try {

            String params = "params";
            boolean periodic = true;
            int period = 300;

            AdminDao admin = createAdmin();

            JobSrv.create(326, admin.getAdminId(), Date.from(Instant.now()), params, periodic, period);

            Assert.fail("Created agent without admin.");

        } catch (SrvException e) {
            // e.printStackTrace();
            System.out.println("Correctly did not create job without agent.");
        }
    }

    @Test
    public void testCreateJobWithoutAdmin() {

        try {

            String params = "params";
            boolean periodic = true;
            int period = 300;

            AgentDao agent = createAgent();

            JobSrv.create(agent.getAgentId(), 957, Date.from(Instant.now()), params, periodic, period);

            Assert.fail("Created agent without admin.");

        } catch (SrvException e) {
            // e.printStackTrace();
            System.out.println("Correctly did not create job without agent.");
        }
    }

    @Test
    public void testFindAgentJobsNotSent() {

        try {

            String params = "params";
            boolean periodic = true;
            int period = 300;

            AgentDao agent = createAgent();

            Assert.assertEquals(0, JobSrv.findAllWithAgentId(agent.getAgentId(), true).size());
            Assert.assertEquals(0, JobSrv.findAllWithAgentId(agent.getAgentId(), false).size());

            JobDao job = JobSrv.create(agent.getAgentId(), agent.getAdminId(), Date.from(Instant.now()), params, periodic, period);

            Assert.assertEquals(false, job.getSent());

            Assert.assertEquals(0, JobSrv.findAllWithAgentId(agent.getAgentId(), true).size());
            Assert.assertEquals(1, JobSrv.findAllWithAgentId(agent.getAgentId(), false).size());

            JobSrv.send(job.getJobId());

            job = JobSrv.findById(job.getJobId());

            Assert.assertEquals(true, job.getSent());

            Assert.assertEquals(1, JobSrv.findAllWithAgentId(agent.getAgentId(), true).size());
            Assert.assertEquals(0, JobSrv.findAllWithAgentId(agent.getAgentId(), false).size());

        } catch (SrvException e) {
            // e.printStackTrace();
            Assert.fail(e.getMessage());
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

    private AgentDao createAgent() throws SrvException {

        RequestDao request = createRequest();

        AdminDao AdminDao = createAdmin();

        return AgentSrv.create(request.getRequestId(), AdminDao.getAdminId());
    }
}
