package k23b.am.srv;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Date;
import java.util.Set;

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
import k23b.am.cc.ResultCC;
import k23b.am.dao.AdminDao;
import k23b.am.dao.AgentDao;
import k23b.am.dao.ConnectionSingleton;
import k23b.am.dao.JobDao;
import k23b.am.dao.RequestDao;
import k23b.am.dao.ResultDao;

public class ResultSrvTest {

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
            ResultCC.initCache();
        }
    }

    @After
    public void tearDown() throws Exception {
    }

    public void findNonExistentResult() {

        try {

            Assert.assertNull(ResultSrv.findById(326));

        } catch (SrvException e) {
            // e.printStackTrace();
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void testCreateResultForJobSent() {

        try {

            JobDao job = createAndSendJob();

            String output = "output";

            ResultDao result = ResultSrv.create(job.getJobId(), output);

            Assert.assertNotNull(result);
            Assert.assertEquals(job.getJobId(), result.getJobId());
            Assert.assertEquals(output, result.getOutput());

            System.out.println("Successfully created result for job: " + job.getJobId());

        } catch (SrvException e) {
            // e.printStackTrace();
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void testCreateResultForJobNotSent() {

        try {

            JobDao job = createJob();

            String output = "output";

            ResultDao result = ResultSrv.create(job.getJobId(), output);

            Assert.assertNotNull(result);
            Assert.assertEquals(job.getJobId(), result.getJobId());
            Assert.assertEquals(output, result.getOutput());

            Assert.fail("Created result for job that has not been sent.");

        } catch (SrvException e) {
            // e.printStackTrace();
        }
    }

    @Test
    public void testCreateResultWithoutJob() {

        try {

            ResultSrv.create(326, "output");

            Assert.fail("Created result without job.");

        } catch (SrvException e) {
            // e.printStackTrace();
        }
    }

    @Test
    @SuppressWarnings("deprecation")
    public void findResultsWithinDates() {

        try {

            JobDao job = createAndSendJob();

            Assert.assertNotNull(job);

            String output = "output";

            ResultSrv.create(job.getJobId(), output);

            ResultSrv.create(job.getJobId(), output);

            ResultSrv.create(job.getJobId(), output);

            Date startDate = new Date((Date.parse("Sat, 12 Aug 1995 13:30:00 GMT")));
            Date endDate = new Date((Date.parse("Wed, 17 Dec 2023 11:49:00 GMT")));

            Set<ResultDao> results = ResultSrv.findAllWithinDates(job.getAgentId(), startDate, endDate);

            Assert.assertTrue(results.size() == 3);

            results = ResultSrv.findAllWithinDates(startDate, endDate);

            Assert.assertTrue(results.size() == 3);

            startDate = new Date((Date.parse("Sat, 12 Aug 1995 13:30:00 GMT")));
            endDate = new Date((Date.parse("Wed, 17 Dec 2000 11:49:00 GMT")));

            results = ResultSrv.findAllWithinDates(job.getAgentId(), startDate, endDate);

            Assert.assertTrue(results.size() == 0);

            results = ResultSrv.findAllWithinDates(startDate, endDate);

            Assert.assertTrue(results.size() == 0);

        } catch (SrvException e) {
            // e.printStackTrace();
            Assert.fail(e.getMessage());
        }
    }

    private RequestDao createRequest() throws SrvException {

        String hash = "AAAAAAA";
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

    private JobDao createJob() throws SrvException {

        AgentDao agent = createAgent();

        return JobSrv.create(agent.getAgentId(), agent.getAdminId(), "params", true, 300);
    }

    private JobDao createAndSendJob() throws SrvException {

        AgentDao agent = createAgent();

        JobDao job = JobSrv.create(agent.getAgentId(), agent.getAdminId(), "params", true, 300);

        JobSrv.send(job.getJobId());

        return JobSrv.findById(job.getJobId());
    }
}
