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
import k23b.am.cc.RequestCC;
import k23b.am.dao.ConnectionSingleton;
import k23b.am.dao.RequestDao;
import k23b.am.dao.RequestStatus;

public class RequestSrvTest {

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
        }
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testFindNonExistentRequest() {

        try {

            Assert.assertNull(RequestSrv.findByHash("957"));

        } catch (SrvException e) {
            // e.printStackTrace();
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void testCreateRequest() {

        String hash = "AA";
        String deviceName = "deviceName";
        String interfaceIP = "interfaceIP";
        String interfaceMAC = "interfaceMAC";
        String osVersion = "osVersion";
        String nmapVersion = "nmapVersion";
        RequestStatus status = RequestStatus.PENDING;

        try {

            RequestDao r = RequestSrv.create(hash, deviceName, interfaceIP, interfaceMAC, osVersion, nmapVersion);

            RequestSrv.create(hash + "FF", deviceName, interfaceIP, interfaceMAC, osVersion, nmapVersion);

            RequestSrv.create(hash + "GG", deviceName, interfaceIP, interfaceMAC, osVersion, nmapVersion);

            Assert.assertNotNull(r);

            Assert.assertEquals(hash, r.getHash());
            Assert.assertEquals(deviceName, r.getDeviceName());
            Assert.assertEquals(interfaceIP, r.getInterfaceIP());
            Assert.assertEquals(interfaceMAC, r.getInterfaceMAC());
            Assert.assertEquals(osVersion, r.getOsVersion());
            Assert.assertEquals(nmapVersion, r.getNmapVersion());
            Assert.assertEquals(status, r.getRequestStatus());
            System.out.println("Succesfully created request.");

            Assert.assertNotNull(RequestSrv.findByHash(hash));
            System.out.println("Found request.");

        } catch (SrvException e) {
            // e.printStackTrace();
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void testCreateAndFindRequest() {

        String hash = "AA";
        String deviceName = "deviceName";
        String interfaceIP = "interfaceIP";
        String interfaceMAC = "interfaceMAC";
        String osVersion = "osVersion";
        String nmapVersion = "nmapVersion";
        RequestStatus status = RequestStatus.PENDING;

        try {

            RequestSrv.create(hash, deviceName, interfaceIP, interfaceMAC, osVersion, nmapVersion);

            RequestDao r = RequestSrv.findByHash(hash);

            Assert.assertNotNull(r);

            Assert.assertEquals(hash, new String(r.getHash()));
            Assert.assertEquals(deviceName, r.getDeviceName());
            Assert.assertEquals(interfaceIP, r.getInterfaceIP());
            Assert.assertEquals(interfaceMAC, r.getInterfaceMAC());
            Assert.assertEquals(osVersion, r.getOsVersion());
            Assert.assertEquals(nmapVersion, r.getNmapVersion());
            Assert.assertEquals(status, r.getRequestStatus());

        } catch (SrvException e) {
            // e.printStackTrace();
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void testCreateSameHashRequest() {

        String hash = "AA";
        String deviceName = "deviceName";
        String interfaceIP = "interfaceIP";
        String interfaceMAC = "interfaceMAC";
        String osVersion = "osVersion";
        String nmapVersion = "nmapVersion";

        try {

            RequestSrv.create(hash, deviceName, interfaceIP, interfaceMAC, osVersion, nmapVersion);

            RequestSrv.create(hash, deviceName, interfaceIP, interfaceMAC, osVersion, nmapVersion);

            Assert.fail("Created second request with the same hash.");

        } catch (SrvException e) {
            // e.printStackTrace();
        }
    }
}
