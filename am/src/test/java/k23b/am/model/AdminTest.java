package k23b.am.model;

import java.sql.Connection;
import java.sql.DriverManager;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import junit.framework.Assert;
import k23b.am.ScriptRunner;

public class AdminTest {

    private static final Logger log = Logger.getLogger(AdminTest.class);

    @Before
    public void setUp() throws Exception {

        PropertyConfigurator.configure("log4j.properties");

        log.info("");
        log.info("Working directory: " + System.getProperty("user.dir"));

        try {

            Connection c = DriverManager.getConnection("jdbc:mysql://localhost:3306/amdb", "root", "s3cr3t");

            new ScriptRunner(c).runScript("src/main/resources/sql/drop.sql");
            new ScriptRunner(c).runScript("src/main/resources/sql/amdb.sql");

        } catch (Exception e) {
            Assert.fail(e.getMessage());
        }
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void test() {
        //
        // try {
        //
        // Admin a = AdminSrv.create("Yannis", "pass", false);
        //
        // Request r1 = RequestSrv.create("AA", "", "", "", "", "");
        // Request r2 = RequestSrv.create("BB", "", "", "", "", "");
        //
        // Agent agent1 = a.assignAgent(r1);
        // Agent agent2 = a.assignAgent(r2);
        //
        // Assert.assertNotNull(agent1);
        // Assert.assertNotNull(agent2);
        //
        // Assert.assertTrue(a.getAgentsAssigned().contains(agent1));
        // Assert.assertTrue(a.getAgentsAssigned().contains(agent2));
        //
        // Assert.assertEquals(2, a.getAgentsAssigned().size());
        //
        // } catch (SrvException e) {
        // // e.printStackTrace();
        // Assert.fail(e.getMessage());
        // }
    }
}
