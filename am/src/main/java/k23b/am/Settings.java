package k23b.am;

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

    private static int jobRequestInterval;
    private static boolean cacheEnabled;
    private static long cacheSize;
    private static String dbUrl;
    private static String dbUser;
    private static String dbPass;
    private static boolean createSchema;
    private static boolean createAdmins;
    private static boolean expireUserSessions;
    private static int userSessionMinutes;

    public static int getJobRequestInterval() {
        return jobRequestInterval;
    }

    public static boolean getCacheEnabled() {
        return cacheEnabled;
    }

    public static long getCacheSize() {
        return cacheSize;
    }

    public static String getDbUrl() {
        return dbUrl;
    }

    public static String getDbUser() {
        return dbUser;
    }

    public static String getDbPass() {
        return dbPass;
    }

    public static boolean getCreateSchema() {
        return createSchema;
    }

    public static boolean getCreateAdmins() {
        return createAdmins;
    }
    
    public static boolean getExpireUserSessions() {
        return expireUserSessions;
    }
    
    public static int getUserSessionMinutes() {
        return userSessionMinutes;
    }

    public static void load() {

        Properties properties = new Properties();

        File propertyFile = new File("am.properties");

        try (FileReader fr = new FileReader(propertyFile)) {

            try {

                log.info("Loading settings.");

                properties.load(fr);

                jobRequestInterval = Integer.parseInt(properties.getProperty("jobRequestInterval"));
                log.info("jobRequestInterval: " + getJobRequestInterval());

                cacheEnabled = Boolean.parseBoolean(properties.getProperty("cacheEnabled"));
                log.info("cacheEnabled: " + getCacheEnabled());

                cacheSize = Long.parseLong(properties.getProperty("cacheSize"));
                log.info("cacheSize: " + getCacheSize());

                dbUrl = properties.getProperty("dbUrl");
                log.info("dbUrl: " + getDbUrl());

                dbUser = properties.getProperty("dbUser");
                log.info("dbUser: " + getDbUser());

                dbPass = properties.getProperty("dbPass");
                char[] pass = new char[dbPass.length()];
                for (int i = 0; i < pass.length; i++)
                    pass[i] = '*';
                log.info("dbPass: " + new String(pass));

                createSchema = Boolean.parseBoolean(properties.getProperty("createSchema"));
                log.info("createSchema: " + getCreateSchema());

                createAdmins = Boolean.parseBoolean(properties.getProperty("createAdmins"));
                log.info("createAdmins: " + getCreateAdmins());
                
                expireUserSessions = Boolean.parseBoolean(properties.getProperty("expireUserSessions"));
                log.info("expireUserSessions: " + getExpireUserSessions());
                
                userSessionMinutes = Integer.parseInt(properties.getProperty("userSessionMinutes"));
                log.info("userSessionMinutes: " + getUserSessionMinutes());

                log.info("Settings loaded.");

            } catch (IOException e) {
                // e.printStackTrace();
                log.error("Error while loading settings: " + e.toString());
            }

        } catch (FileNotFoundException e) {
            // e.printStackTrace();
            log.error("Property file not found: " + propertyFile.toString());
        } catch (IOException e) {
            // e.printStackTrace();
            log.error("Error while closing settings file: " + propertyFile.toString());
        }
    }
}
