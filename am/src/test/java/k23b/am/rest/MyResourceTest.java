package k23b.am.rest;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Enumeration;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import k23b.am.dao.DaoException;
import k23b.am.srv.SrvException;

public class MyResourceTest {

    // private HttpServer server;

    // private Client client;
    @Before
    public void setUp() throws Exception {
        // // start the server
        // server = App.startServer();
        // System.out.println(String.format("Jersey app started with WADL available at " + "%sapplication.wadl", App.BASE_URI));
        // // client = Client.create();

    }

    @After
    public void tearDown() throws Exception {
        // server.stop();
    }

    @Test
    public void test() {
    }

    /**
     * Test to see that the message "Got it!" is sent in the response.
     * 
     * @throws IOException
     * @throws SrvException
     * @throws DaoException
     */
    public void requestGet() throws IOException, SrvException, DaoException {

        // Client client = Client.create();
        // String[] saStats = null;
        // try {
        // saStats = getSAStats();
        // } catch (Exception e1) {
        // System.out.println("Exception on getSAStats. Exception type: " + e1.getCause());
        // }
        //
        // WebResource webResource = client.resource(App.BASE_URI + "handle/request/send/" + saStats[5] + "/" + saStats[0] + "/" + saStats[1] + "/" + saStats[2] + "/"
        // + saStats[3] + "/" + saStats[4]);
        // ClientResponse response = webResource.accept(MediaType.TEXT_PLAIN).get(ClientResponse.class);
        // if (response.getStatus() != 200) {
        // throw new RuntimeException("Failed : HTTP error code : " + response.getStatus());
        // }
        //
        // assertEquals("Pending", response.getEntity(String.class));
        //
        // statusCheck();
    }

    public void statusCheck() throws IOException, SrvException, DaoException {

        // Client client = Client.create();
        // String[] saStats = null;
        // try {
        // saStats = getSAStats();
        // } catch (Exception e1) {
        // System.out.println("Exception on getSAStats. Exception type: " + e1.getCause());
        // }
        //
        // AgentDao agent = createAgent();
        // RequestDao req = RequestDao.findByHash(saStats[5]);
        // RequestDao.setStatus(req.getRequestId(), RequestStatus.ACCEPTED);
        //
        // WebResource webResource = client.resource(App.BASE_URI + "handle/request/check/" + saStats[5] + "/");
        // ClientResponse response = webResource.accept(MediaType.TEXT_PLAIN).get(ClientResponse.class);
        // if (response.getStatus() != 200) {
        // throw new RuntimeException("Failed : HTTP error code : " + response.getStatus());
        // }
        // // assertEquals("Pending",response.getEntity(String.class));
        // assertEquals("Accepted", response.getEntity(String.class));
        //
        // JobSrv.create(agent.getAgentId(), agent.getAdminId(), "p1 p2 p3 p4 p5 p6", true, 30);
        // JobSrv.create(agent.getAgentId(), agent.getAdminId(), "p7 p8 p9 p10 p11", false, 0);
        // JobSrv.create(agent.getAgentId(), agent.getAdminId(), "p12 p13 p14", true, 31);
        // JobSrv.create(agent.getAgentId(), agent.getAdminId(), "p15 p16 p17 p18 p19", false, 0);
        //
        // jobsGet();
    }

    public void jobsGet() {

        // Client client = Client.create();
        //
        // String[] saStats = null;
        // try {
        // saStats = getSAStats();
        // } catch (Exception e1) {
        // System.out.println("Exception on getSAStats. Exception type: " + e1.getCause());
        // }
        //
        // List<String> jobList = new ArrayList<String>();
        //
        // try {
        //
        // WebResource webResource = client.resource(App.BASE_URI + "handle/jobs/get/" + saStats[5] + "/");
        // ClientResponse response = webResource.accept(MediaType.APPLICATION_XML).get(ClientResponse.class);
        // if (response.getStatus() != 200) {
        // throw new RuntimeException("Failed : HTTP error code : " + response.getStatus());
        // }
        //
        // ResultList out = response.getEntity(ResultList.class);
        // if (out.getResult().isEmpty()) {
        // System.out.println("AM sent an empty Job List");
        // Assert.fail("AM sent an empty Job List");
        // } else {
        // for (Result c : out.getResult()) {
        //
        // System.out.println(c.toString());
        // }
        // }
        // } catch (Exception e) {
        //
        // System.out.println("JaxbException .Exception type: " + e.getMessage());
        //
        // }
        //
        // if (!jobList.isEmpty())
        // while (!jobList.isEmpty())
        // System.out.println(jobList.remove(0));
    }

    // private RequestDao createRequest() throws SrvException {
    //
    // String[] saStats = null;
    // try {
    // saStats = getSAStats();
    // } catch (Exception e1) {
    // System.out.println("Exception on getSAStats. Exception type: " + e1.getCause());
    // }
    //
    // return RequestSrv.findByHash(saStats[5]);
    // }

    // private AdminDao createAdmin() throws SrvException {
    //
    // String username = "Yannis";
    // String password = "abcde";
    //
    // AdminDao ad = AdminSrv.findByUsername("Yannis");
    // if (ad != null)
    // return ad;
    //
    // return AdminSrv.create(username, password);
    // }

    // private AgentDao createAgent() throws SrvException {
    //
    // RequestDao request = createRequest();
    //
    // AdminDao AdminDao = createAdmin();
    //
    // return AgentSrv.create(request.getRequestId(), AdminDao.getAdminId());
    // }

    public static String[] getSAStats() throws Exception {

        String[] saStats = new String[6];// [0]=nmapVersion ,[1]=osVersion,[2]=macAddress , [3]=ipAddress, [4]=hsotName , [5]=hashKey

        Process proc = Runtime.getRuntime().exec("nmap -V");
        int exitVal = proc.waitFor();

        if (exitVal == 0) {
            InputStream stderr = proc.getInputStream();
            InputStreamReader isr = new InputStreamReader(stderr);
            BufferedReader br = new BufferedReader(isr);

            String versionLine = null;
            String[] lineparts = null;

            while ((versionLine = br.readLine()) != null) {
                lineparts = versionLine.split(" ");
                if (lineparts[0].compareTo("Nmap") == 0)
                    saStats[0] = lineparts[2];
            }
        }
        saStats[1] = System.getProperty("os.version");

        Enumeration<NetworkInterface> e = NetworkInterface.getNetworkInterfaces();

        if (e.hasMoreElements()) {
            NetworkInterface ni = e.nextElement();
            byte[] mac;

            if ((mac = ni.getHardwareAddress()) != null) {
                Enumeration<InetAddress> ia = ni.getInetAddresses();
                while (ia.hasMoreElements()) {
                    InetAddress ias = ia.nextElement();
                    if (ias.isSiteLocalAddress()) {
                        saStats[3] = ias.getHostAddress();
                        saStats[4] = ias.getHostName();
                    }
                }
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < mac.length; i++)
                    sb.append(String.format("%02X%s", mac[i], (i < mac.length - 1) ? "-" : ""));

                System.out.println();
                saStats[2] = sb.toString();
            }
        }
        MessageDigest md;
        try {

            md = MessageDigest.getInstance("SHA-256");

            String s = saStats[0] + saStats[1] + saStats[2] + saStats[3] + saStats[4];

            md.update(s.getBytes());

            byte[] digest = md.digest();

            saStats[5] = bytesToHex(digest);

        } catch (NoSuchAlgorithmException ex) {
            ex.printStackTrace();
        }

        return saStats;
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
