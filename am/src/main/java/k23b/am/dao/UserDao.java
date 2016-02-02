package k23b.am.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.Logger;

/**
 * Data access layer for user objects.
 */
public class UserDao {

    private static final Logger log = Logger.getLogger(UserDao.class);

    /**
     * Creates a new user using the data provided.
     * 
     * @param username the user's username.
     * @param password the user's password.
     * @param active the user's active status.
     * @return the newly created user's id.
     * @throws DaoException if the newly created user's id could not be retrieved or a data access error occurs.
     */
    public static long create(String username, String password, Date timeRegistered) throws DaoException {

        String sql = new StringBuilder()
                .append("insert into amdb_user ")
                .append("(USERNAME, PASSWORD, TIME_REGISTERED) ")
                .append("values ")
                .append("(?, ?, ?)")
                .toString();

        try (SynchronizedStatement ss = ConnectionSingleton.getInstance().getStatement(sql)) {

            log.debug("");
            log.debug("Executing query: " + sql);

            log.debug("Setting parameter 1 to: " + username);
            ss.setString(1, username);

            log.debug("Setting parameter 2 to: " + password);
            ss.setString(2, password);

            log.debug("Setting parameter 3 to: " + timeRegistered);
            ss.setTimestamp(3, new Timestamp(timeRegistered.getTime()));

            int rows = ss.executeUpdate();
            log.debug(rows + (rows == 1 ? " row " : " rows ") + "inserted.");

            ResultSet rs = ss.getGeneratedKeys();

            if (!rs.next())
                throw new DaoException("Could not retrieve id for created user.");

            long id = rs.getLong(1);
            log.debug("Id returned: " + id);

            return id;

        } catch (SQLException e) {
            // e.printStackTrace();
            log.error(e.getMessage());

            throw new DaoException("Error while creating user with username: " + username);
        }
    }

    /**
     * Retrieves an existing user with specified id.
     * 
     * @param userId the user's id.
     * @return an object representing the user found, or null if a user with specified id was not found.
     * @throws DaoException if a data access error occurs.
     */
    public static UserDao findById(long userId) throws DaoException {

        String sql = new StringBuilder()
                .append("select * from amdb_user ")
                .append("where ")
                .append("USER_ID = ?")
                .toString();

        try (SynchronizedStatement ss = ConnectionSingleton.getInstance().getStatement(sql)) {

            log.debug("");
            log.debug("Executing query: " + sql);

            log.debug("Setting parameter 1 to: " + userId);
            ss.setLong(1, userId);

            ss.executeQuery();

            ResultSet rs = ss.getResultSet();

            if (rs.next()) {

                long id = rs.getLong("USER_ID");
                long adminId = rs.getLong("ADMIN_ID");
                if (rs.wasNull())
                    adminId = 0;
                String username = rs.getString("USERNAME");
                String password = rs.getString("PASSWORD");
                Date timeRegistered = rs.getTimestamp("TIME_REGISTERED");
                Date timeAccepted = rs.getTimestamp("TIME_ACCEPTED");
                Date timeActive = rs.getTimestamp("TIME_ACTIVE");

                log.debug("1 row selected.");
                return new UserDao(id, adminId, username, password, timeRegistered, timeAccepted, timeActive);

            } else {

                log.debug("0 rows selected.");
                return null;
            }

        } catch (SQLException e) {
            // e.printStackTrace();
            log.error(e.getMessage());

            throw new DaoException("Error while finding user by id: " + userId);
        }
    }

    /**
     * Retrieves an existing user with specified username.
     * 
     * @param username the user's username.
     * @return an object representing the user found, or null if a user with specified username was not found.
     * @throws DaoException if a data access error occurs.
     */
    public static UserDao findByUsername(String username) throws DaoException {

        String sql = new StringBuilder()
                .append("select * from amdb_user ")
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

                long userId = rs.getLong("USER_ID");
                long adminId = rs.getLong("ADMIN_ID");
                if (rs.wasNull())
                    adminId = 0;
                String name = rs.getString("USERNAME");
                String password = rs.getString("PASSWORD");
                Date timeRegistered = rs.getTimestamp("TIME_REGISTERED");
                Date timeAccepted = rs.getTimestamp("TIME_ACCEPTED");
                Date timeActive = rs.getTimestamp("TIME_ACTIVE");

                log.debug("1 row selected.");
                return new UserDao(userId, adminId, name, password, timeRegistered, timeAccepted, timeActive);

            } else {

                log.debug("0 rows selected.");
                return null;
            }

        } catch (SQLException e) {
            // e.printStackTrace();
            log.error(e.getMessage());

            throw new DaoException("Error while finding user by username: " + username);
        }
    }

    /**
     * Retrieves all users currently in store.
     * 
     * @return a set of objects, each representing a user.
     * @throws DaoException if a data access error occurs.
     */
    public static Set<UserDao> findAll() throws DaoException {

        String sql = new StringBuilder()
                .append("select * from amdb_user")
                .toString();

        try (SynchronizedStatement ss = ConnectionSingleton.getInstance().getStatement(sql)) {

            log.debug("");
            log.debug("Executing query: " + sql);

            ss.executeQuery();

            ResultSet rs = ss.getResultSet();

            Set<UserDao> users = new HashSet<UserDao>();

            int rows = 0;

            while (rs.next()) {

                rows++;
                long userId = rs.getLong("USER_ID");
                long adminId = rs.getLong("ADMIN_ID");
                if (rs.wasNull())
                    adminId = 0;
                String name = rs.getString("USERNAME");
                String password = rs.getString("PASSWORD");
                Date timeRegistered = rs.getTimestamp("TIME_REGISTERED");
                Date timeAccepted = rs.getTimestamp("TIME_ACCEPTED");
                Date timeActive = rs.getTimestamp("TIME_ACTIVE");

                users.add(new UserDao(userId, adminId, name, password, timeRegistered, timeAccepted, timeActive));
            }

            log.debug(rows + (rows == 1 ? " row " : " rows ") + "selected.");
            return users;

        } catch (SQLException e) {
            // e.printStackTrace();
            log.error(e.getMessage());

            throw new DaoException("Error while finding all users.");
        }
    }

    /**
     * Sets the admin id of a user to a specific value.
     * 
     * @param userId the user's id.
     * @param adminId the user's admin id, or 0 if admin id should be set to null.
     * @throws DaoException if the user's admin id could not be changed to the specified value or a data access error occurs.
     */
    public static void setAdminId(long userId, long adminId) throws DaoException {

        String sql = new StringBuilder()
                .append("update amdb_user set ")
                .append(adminId > 0 ? "ADMIN_ID = ? " : "ADMIN_ID = NULL ")
                .append("where ")
                .append("USER_ID = ?")
                .toString();

        try (SynchronizedStatement ss = ConnectionSingleton.getInstance().getStatement(sql)) {

            log.debug("");
            log.debug("Executing query: " + sql);

            if (adminId > 0) {
                log.debug("Setting parameter 1 to: " + adminId);
                ss.setLong(1, adminId);

                log.debug("Setting parameter 2 to: " + userId);
                ss.setLong(2, userId);
            } else {
                log.debug("Setting parameter 1 to: " + userId);
                ss.setLong(1, userId);
            }

            int rows = ss.executeUpdate();
            log.debug(rows + (rows == 1 ? " row " : " rows ") + "updated.");

            if (rows != 1)
                throw new DaoException("Error while setting admin id to " + adminId + " on user with id: " + userId);

        } catch (SQLException e) {
            // e.printStackTrace();
            log.error(e.getMessage());

            throw new DaoException("Error while setting admin id to " + adminId + " on user with id: " + userId);
        }
    }

    /**
     * Sets the accepted time of a user to a specific value.
     * 
     * @param userId the user's id.
     * @param timeAccepted the time user was accepted.
     * @throws DaoException if the user's accepted time could not be changed to the specified value or a data access error occurs.
     */
    public static void setTimeAccepted(long userId, Date timeAccepted) throws DaoException {

        String sql = new StringBuilder()
                .append("update amdb_user set ")
                .append(timeAccepted != null ? "TIME_ACCEPTED = ? " : "TIME_ACCEPTED = NULL ")
                .append("where ")
                .append("USER_ID = ?")
                .toString();

        try (SynchronizedStatement ss = ConnectionSingleton.getInstance().getStatement(sql)) {

            log.debug("");
            log.debug("Executing query: " + sql);

            if (timeAccepted != null) {

                log.debug("Setting parameter 1 to: " + timeAccepted);
                ss.setTimestamp(1, new Timestamp((timeAccepted.getTime())));

                log.debug("Setting parameter 2 to: " + userId);
                ss.setLong(2, userId);

            } else {

                log.debug("Setting parameter 1 to: " + userId);
                ss.setLong(1, userId);
            }

            int rows = ss.executeUpdate();
            log.debug(rows + (rows == 1 ? " row " : " rows ") + "updated.");

            if (rows != 1)
                throw new DaoException("Error while setting accepted time to " + timeAccepted + " on user with id: " + userId);

        } catch (SQLException e) {
            // e.printStackTrace();
            log.error(e.getMessage());

            throw new DaoException("Error while setting accepted time to " + timeAccepted + " on user with id: " + userId);
        }
    }

    /**
     * Sets the active time of a user to a specific value.
     * 
     * @param userId the user's id.
     * @param timeActive the time user was active.
     * @throws DaoException if the user's active time could not be changed to the specified value or a data access error occurs.
     */
    public static void setTimeActive(long userId, Date timeActive) throws DaoException {

        String sql = new StringBuilder()
                .append("update amdb_user set ")
                .append("TIME_ACTIVE = ? ")
                .append("where ")
                .append("USER_ID = ?")
                .toString();

        try (SynchronizedStatement ss = ConnectionSingleton.getInstance().getStatement(sql)) {

            log.debug("");
            log.debug("Executing query: " + sql);

            log.debug("Setting parameter 1 to: " + timeActive);
            ss.setTimestamp(1, new Timestamp((timeActive.getTime())));

            log.debug("Setting parameter 2 to: " + userId);
            ss.setLong(2, userId);

            int rows = ss.executeUpdate();
            log.debug(rows + (rows == 1 ? " row " : " rows ") + "updated.");

            if (rows != 1)
                throw new DaoException("Error while setting time active to " + timeActive + " on user with id: " + userId);

        } catch (SQLException e) {
            // e.printStackTrace();
            log.error(e.getMessage());

            throw new DaoException("Error while setting time active to " + timeActive + " on user with id: " + userId);
        }
    }

    private long userId;
    private long adminId;
    private String username;
    private String password;
    private Date timeRegistered;
    private Date timeAccepted;
    private Date timeActive;

    public long getUserId() {
        return userId;
    }

    public long getAdminId() {
        return adminId;
    }

    public void setAdminId(long adminId) {
        this.adminId = adminId;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public Date getTimeRegistered() {
        return timeRegistered;
    }

    public Date getTimeAccepted() {
        return timeAccepted;
    }

    public void setTimeAccepted(Date timeAccepted) {
        this.timeAccepted = timeAccepted;
    }

    public Date getTimeActive() {
        return timeActive;
    }

    public void setTimeActive(Date timeActive) {
        this.timeActive = timeActive;
    }

    private UserDao(long userId, long adminId, String username, String password, Date timeRegistered, Date timeAccepted, Date timeActive) {
        super();
        this.userId = userId;
        this.adminId = adminId;
        this.username = username;
        this.password = password;
        this.timeRegistered = timeRegistered;
        this.timeAccepted = timeAccepted;
        this.timeActive = timeActive;
    }
}
