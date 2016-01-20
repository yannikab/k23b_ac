package k23b.sa.AgentState;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Enumeration;

import javax.ws.rs.client.ClientBuilder;

import org.apache.log4j.Logger;

import k23b.sa.AgentStats;
import k23b.sa.Settings;
import k23b.sa.JobProvider.LineParser;
import k23b.sa.Threads.MainThread;
import k23b.sa.rest.JobContainerMessageBodyReader;
import k23b.sa.rest.ResultContainerMessageBodyWriter;

/**
 * The State in which the SA starts up and initializes its settings and statistics.
 */
public class StartupState extends AgentState {

    private static final Logger log = Logger.getLogger(StartupState.class);

    public StartupState(MainThread mainThread) {
        super(mainThread);
    }

    @Override
    public void handleState() {

        log.info("");
        log.info("Starting Software Agent.");
        log.info("Working directory: " + System.getProperty("user.dir"));

        // load settings
        getMainThread().setSettings(new Settings("sa.properties"));
        getMainThread().getSettings().load();

        // configure line parser to run nmap with sudo if needed
        LineParser.setRunNmapAsRoot(getMainThread().getSettings().getRunNmapAsRoot());

        // Create the client
        getMainThread().setClient(ClientBuilder.newClient().register(JobContainerMessageBodyReader.class).register(ResultContainerMessageBodyWriter.class));

        try {

            getMainThread().setAgentStats(getAgentStats());

            setCurrentState(new RegisterState(getMainThread()));

        } catch (IOException | InterruptedException | NoSuchAlgorithmException e) {
            // e.printStackTrace();
            log.error(e.getMessage());

            setCurrentState(new ShutdownState(getMainThread()));
        }
    }

    private AgentStats getAgentStats() throws IOException, InterruptedException, NoSuchAlgorithmException {

        AgentStats agentInfo = new AgentStats();

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
                    agentInfo.setNmapVersion(lineparts[2]);
            }
        }

        agentInfo.setOsVersion(System.getProperty("os.version"));

        Enumeration<NetworkInterface> e = NetworkInterface.getNetworkInterfaces();

        if (e.hasMoreElements()) {
            NetworkInterface ni = e.nextElement();
            byte[] mac;

            if ((mac = ni.getHardwareAddress()) != null) {
                Enumeration<InetAddress> ia = ni.getInetAddresses();
                while (ia.hasMoreElements()) {
                    InetAddress ias = ia.nextElement();
                    if (ias.isSiteLocalAddress()) {
                        agentInfo.setInterfaceIp(ias.getHostAddress());
                        agentInfo.setDeviceName(ias.getHostName());
                    }
                }
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < mac.length; i++)
                    sb.append(String.format("%02X%s", mac[i], (i < mac.length - 1) ? "-" : ""));

                // System.out.println();
                agentInfo.setInterfaceMac(sb.toString());
            }
        }

        MessageDigest md;

        md = MessageDigest.getInstance("SHA-256");

        StringBuilder sb = new StringBuilder()
                .append(agentInfo.getNmapVersion())
                .append(agentInfo.getOsVersion())
                .append(agentInfo.getInterfaceMac())
                .append(agentInfo.getInterfaceIp())
                .append(agentInfo.getDeviceName());

        md.update(sb.toString().getBytes());

        byte[] digest = md.digest();

        agentInfo.setHash(bytesToHex(digest));

        return agentInfo;
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
