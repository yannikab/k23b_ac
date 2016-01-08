package k23b.am.dao;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.log4j.Logger;

/**
 * Data access layer for admin objects.
 */
public class AdminDao {

    private static final Logger log = Logger.getLogger(AdminDao.class);

    /**
     * Creates a new admin using the data provided.
     * 
     * @param username the admin's username.
     * @param password the admin's password.
     * @param active the admin's active status.
     * @return the newly created admin's id.
     * @throws DaoException if the newly created admin's id could not be retrieved or a data access error occurs.
     */
    public static long create(String username, String password, boolean active) throws DaoException {

        String sql = new StringBuilder()
                .append("insert into amdb_admin ")
                .append("(USERNAME, PASSWORD, ACTIVE) ")
                .append("values ")
                .append("(?, ?, ?)")
                .toString();

        try (SynchronizedStatement ss = ConnectionSingleton.getInstance().getStatement(sql)) {

            log.debug("");
            log.debug("Executing query: " + sql);

            log.debug("Setting parameter 1 to: " + username);
            ss.setString(1, username);

            log.debug("Setting parameter 2 to: " + new String(password));
            ss.setString(2, new String(password));

            log.debug("Setting parameter 3 to: " + active);
            ss.setBoolean(3, active);

            int rows = ss.executeUpdate();
            log.debug(rows + (rows == 1 ? " row " : " rows ") + "inserted.");

            ResultSet rs = ss.getGeneratedKeys();

            if (!rs.next())
                throw new DaoException("Could not retrieve id for created admin.");

            long id = rs.getLong(1);
            log.debug("Id returned: " + id);

            return id;

        } catch (SQLException e) {
            // e.printStackTrace();
            log.error(e.getMessage());

            throw new DaoException("Error while creating admin with username: " + username);
        }
    }

    /**
     * Retrieves an existing admin with specified id.
     * 
     * @param adminId the admin's id.
     * @return an object representing the admin found, or null if an admin with specified id was not found.
     * @throws DaoException if a data access error occurs.
     */
    public static AdminDao findById(long adminId) throws DaoException {

        String sql = new StringBuilder()
                .append("select * from amdb_admin ")
                .append("where ")
                .append("ADMIN_ID = ?")
                .toString();

        try (SynchronizedStatement ss = ConnectionSingleton.getInstance().getStatement(sql)) {

            log.debug("");
            log.debug("Executing query: " + sql);

            log.debug("Setting parameter 1 to: " + adminId);
            ss.setLong(1, adminId);

            ss.executeQuery();

            ResultSet rs = ss.getResultSet();

            if (rs.next()) {

                long id = rs.getLong("ADMIN_ID");
                String username = rs.getString("USERNAME");
                String password = rs.getString("PASSWORD");
                boolean active = rs.getBoolean("ACTIVE");

                log.debug("1 row selected.");
                return new AdminDao(id, username, password, active);

            } else {

                log.debug("0 rows selected.");
                return null;
            }

        } catch (SQLException e) {
            // e.printStackTrace();
            log.error(e.getMessage());

            throw new DaoException("Error while finding AdminDao by id: " + adminId);
        }
    }

    /**
     * Retrieves an existing admin with specified username.
     * 
     * @param username the admin's username.
     * @return an object representing the admin found, or null if an admin with specified username was not found.
     * @throws DaoException if a data access error occurs.
     */
    public static AdminDao findByUsername(String username) throws DaoException {

        String sql = new StringBuilder()
                .append("select * from amdb_admin ")
                .append("where ")
                .append("USERNAME = ?")
                .toString();

        try (SynchronizedStatement ss = ConnectionSingleton.getInstance().getStatement(sql)) {

            log.debug("");
            log.debug("Executing query: " + sql);

            log.debug("Setting parameter 1 to: " + username);
            ss.setString(1, username);

            ss.executeQuery();

            ResultSet rs = ss.getResultSet();

            if (rs.next()) {

                long adminId = rs.getLong("ADMIN_ID");
                String name = rs.getString("USERNAME");
                String password = rs.getString("PASSWORD");
                boolean active = rs.getBoolean("ACTIVE");

                log.debug("1 row selected.");
                return new AdminDao(adminId, name, password, active);

            } else {

                log.debug("0 rows selected.");
                return null;
            }

        } catch (SQLException e) {
            // e.printStackTrace();
            log.error(e.getMessage());

            throw new DaoException("Error while finding admin by username: " + username);
        }
    }

    /**
     * Sets the active status of an admin to a specific value.
     * 
     * @param adminId the admin's id.
     * @param active the admin's active status.
     * @throws DaoException if the admin's active status could not be changed to the specified value or a data access error occurs.
     */
    public static void setActive(long adminId, boolean active) throws DaoException {

        String sql = new StringBuilder()
                .append("update amdb_admin set ")
                .append("ACTIVE = ? ")
                .append("where ")
                .append("ADMIN_ID = ?")
                .toString();

        try (SynchronizedStatement ss = ConnectionSingleton.getInstance().getStatement(sql)) {

            log.debug("");
            log.debug("Executing query: " + sql);

            log.debug("Setting parameter 1 to: " + active);
            ss.setBoolean(1, active);

            log.debug("Setting parameter 2 to: " + adminId);
            ss.setLong(2, adminId);

            int rows = ss.executeUpdate();
            log.debug(rows + (rows == 1 ? " row " : " rows ") + "updated.");

            if (rows != 1)
                throw new DaoException("Error while setting active to " + active + " on admin with id: " + adminId);
            ;

        } catch (SQLException e) {
            // e.printStackTrace();
            log.error(e.getMessage());

            throw new DaoException("Error while setting active to " + active + " on admin with id: " + adminId);
        }
    }

    long adminId;
    String username;
    String password;
    boolean active;

    public long getAdminId() {
        return adminId;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public boolean getActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    private AdminDao(long adminId, String username, String password, boolean active) {
        super();
        this.adminId = adminId;
        this.username = username;
        this.password = password;
        this.active = active;
    }
}
