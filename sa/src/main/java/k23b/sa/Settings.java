package k23b.sa;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

import org.apache.log4j.Logger;

/**
 * Get/Set the configurable parameters of the application
 *
 */
public class Settings {

    private static final Logger log = Logger.getLogger(Settings.class);

    private String baseURI;
    private int connectionAttempts;
    private int registrationRetryInterval;
    private int registrationCheckInterval;
    private int jobRequestInterval;
    private int senderThreadInterval;
    private int threadPoolSize;
    private int resultsQueueMax;
    private boolean runNmapAsRoot;

    public String getBaseURI() {
        return baseURI;
    }

    private void setBaseURI(String baseURI) {
        this.baseURI = baseURI;
    }

    public int getConnectionAttempts() {
        return this.connectionAttempts;
    }

    private void setConnectionAttempts(int connectionAttempts) {
        if (connectionAttempts <= 0)
            connectionAttempts = 1;
        this.connectionAttempts = connectionAttempts;
    }

    public int getRegistrationRetryInterval() {
        return this.registrationRetryInterval;
    }

    private void setRegistrationRetryInterval(int registrationRetryInterval) {
        if (registrationRetryInterval <= 0)
            registrationRetryInterval = 60;
        this.registrationRetryInterval = registrationRetryInterval;
    }

    public int getRegistrationCheckInterval() {
        return registrationCheckInterval;
    }

    public void setRegistrationCheckInterval(int registrationCheckInterval) {
        if (registrationCheckInterval <= 0)
            registrationCheckInterval = 60;
        this.registrationCheckInterval = registrationCheckInterval;
    }

    public int getJobRequestInterval() {
        return this.jobRequestInterval;
    }

    private void setJobRequestInterval(int jobRequestInterval) {
        if (jobRequestInterval <= 0)
            jobRequestInterval = 60;
        this.jobRequestInterval = jobRequestInterval;
    }

    public int getSenderThreadInterval() {
        return this.senderThreadInterval;
    }

    private void setSenderThreadInterval(int senderThreadInterval) {
        if (senderThreadInterval <= 0)
            senderThreadInterval = 60;
        this.senderThreadInterval = senderThreadInterval;
    }

    public int getThreadPoolSize() {
        return this.threadPoolSize;
    }

    private void setThreadPoolSize(int threadPoolSize) {
        if (threadPoolSize <= 0)
            threadPoolSize = 1;
        this.threadPoolSize = threadPoolSize;
    }

    public int getResultsQueueMax() {
        return this.resultsQueueMax;
    }

    private void setResultsQueueMax(int resultsQueueMax) {
        this.resultsQueueMax = resultsQueueMax;
    }

    public boolean getRunNmapAsRoot() {
        return this.runNmapAsRoot;
    }

    private void setRunNmapAsRoot(boolean runNmapAsRoot) {
        this.runNmapAsRoot = runNmapAsRoot;
    }

    private String propertyFileName;

    public Settings(String propertyFileName) {

        this.propertyFileName = propertyFileName;
    }

    public void load() {

        Properties properties = new Properties();

        File propertyFile = new File(this.propertyFileName);

        FileReader fr = null;

        try {

            fr = new FileReader(propertyFile);

            try {

                log.info("Loading settings.");

                properties.load(fr);

                setBaseURI(properties.getProperty("baseURI"));
                log.info("baseURI: " + getBaseURI());

                setConnectionAttempts(Integer.parseInt(properties.getProperty("connectionAttempts")));
                log.info("connectionAttempts: " + getConnectionAttempts());

                setRegistrationRetryInterval(Integer.parseInt(properties.getProperty("registrationRetryInterval")));
                log.info("registrationRetryInterval: " + getRegistrationRetryInterval());

                setRegistrationCheckInterval(Integer.parseInt(properties.getProperty("registrationCheckInterval")));
                log.info("registrationCheckInterval: " + getRegistrationCheckInterval());

                setJobRequestInterval(Integer.parseInt(properties.getProperty("jobRequestInterval")));
                log.info("jobRequestInterval: " + getJobRequestInterval());

                setSenderThreadInterval(Integer.parseInt(properties.getProperty("senderThreadInterval")));
                log.info("senderThreadInterval: " + getSenderThreadInterval());

                setThreadPoolSize(Integer.parseInt(properties.getProperty("threadPoolSize")));
                log.info("threadPoolSize: " + getThreadPoolSize());

                setResultsQueueMax(Integer.parseInt(properties.getProperty("resultsQueueMax")));
                log.info("resultsQueueMax: " + getResultsQueueMax());

                setRunNmapAsRoot(properties.getProperty("runNmapAsRoot").equals("true"));
                log.info("runNmapAsRoot: " + getRunNmapAsRoot());

                log.info("Settings loaded.");

            } catch (IOException e) {
                log.error("Error while loading settings: " + e.toString());
            }

        } catch (FileNotFoundException e) {
            log.error("Property file not found: " + propertyFile.toString());
        } finally {
            try {

                log.info("Closing settings file.");

                if (fr != null)
                    fr.close();

            } catch (IOException e) {
                log.error("Error while closing settings file: " + propertyFile.toString());
            }
        }
    }
}
