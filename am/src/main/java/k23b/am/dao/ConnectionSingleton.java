package k23b.am.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.apache.log4j.Logger;

/**
 * Singleton that wraps a JDBC connection and delivers SynchronizedStatement objects to clients that wish to use it.
 */
public class ConnectionSingleton {

    private static final Logger log = Logger.getLogger(ConnectionSingleton.class);

    private static ConnectionSingleton instance;

    private static String dbUrl;
    private static String dbUser;
    private static String dbPass;

    public static String getDbUrl() {
        return dbUrl;
    }

    public static void setDbUrl(String dbUrl) {
        ConnectionSingleton.dbUrl = dbUrl;
    }

    public static String getDbUser() {
        return dbUser;
    }

    public static void setDbUser(String dbUser) {
        ConnectionSingleton.dbUser = dbUser;
    }

    public static String getDbPass() {
        return dbPass;
    }

    public static void setDbPass(String dbPass) {
        ConnectionSingleton.dbPass = dbPass;
    }

    public static ConnectionSingleton getInstance() {

        synchronized (ConnectionSingleton.class) {

            if (instance == null || instance.connection == null) {
                instance = new ConnectionSingleton();
            }
        }

        return instance;
    }

    private Connection connection;

    private ConnectionSingleton() {

        log.info("");

        try {

            connection = DriverManager.getConnection(getDbUrl(), getDbUser(), getDbPass());

            log.info("Connected to database.");

        } catch (SQLException e) {
            // e.printStackTrace();
            log.error("Could not connect to database.");

            connection = null;
        }
    }

    public SynchronizedStatement getStatement(String sql) throws SQLException {

        if (connection == null)
            throw new SQLException();

        try {

            return new SynchronizedStatement(connection, sql);

        } catch (InterruptedException e) {
            // e.printStackTrace();
            return null;
        }
    }
}
