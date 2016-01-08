package k23b.am.dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.concurrent.Semaphore;

import org.apache.log4j.Logger;

import com.mysql.jdbc.Statement;

/**
 * Wraps a prepared statement object. Only one synchronized statement object can be active at any given time. 
 */
public class SynchronizedStatement implements AutoCloseable {

    private static final Logger log = Logger.getLogger(SynchronizedStatement.class);

    private PreparedStatement ps;

    private static Semaphore sem = new Semaphore(1);

    public SynchronizedStatement(Connection connection, String sql) throws InterruptedException {

        sem.acquire();

        // log.debug("");

        try {

            ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            // log.debug("Created prepared statement.");

        } catch (SQLException e) {
            // e.printStackTrace();
            log.error("Could not create prepared statement.");

            ps = null;
        }
    }

    public void setString(int parameterIndex, String x) throws SQLException {

        if (ps == null)
            throw new SQLException();

        ps.setString(parameterIndex, x);
    }

    public void setInt(int parameterIndex, int x) throws SQLException {

        if (ps == null)
            throw new SQLException();

        ps.setInt(parameterIndex, x);
    }

    public void setLong(int parameterIndex, long x) throws SQLException {

        if (ps == null)
            throw new SQLException();

        ps.setLong(parameterIndex, x);
    }

    public void setBoolean(int parameterIndex, boolean x) throws SQLException {

        if (ps == null)
            throw new SQLException();

        ps.setBoolean(parameterIndex, x);
    }

    public void setDate(int parameterIndex, Date x) throws SQLException {

        if (ps == null)
            throw new SQLException();

        ps.setDate(parameterIndex, x);
    }

    public void setTimestamp(int parameterIndex, Timestamp x) throws SQLException {

        if (ps == null)
            throw new SQLException();

        ps.setTimestamp(parameterIndex, x);
    }

    public ResultSet executeQuery() throws SQLException {

        if (ps == null)
            throw new SQLException();

        return ps.executeQuery();
    }

    public int executeUpdate() throws SQLException {

        if (ps == null)
            throw new SQLException();

        return ps.executeUpdate();
    }

    ResultSet getResultSet() throws SQLException {

        if (ps == null)
            throw new SQLException();

        return ps.getResultSet();
    }

    ResultSet getGeneratedKeys() throws SQLException {

        if (ps == null)
            throw new SQLException();

        return ps.getGeneratedKeys();
    }

    @Override
    public void close() throws SQLException {

        if (ps == null)
            throw new SQLException();

        ps.close();

        // log.debug("Closed prepared statement.");

        sem.release();
    }
}
