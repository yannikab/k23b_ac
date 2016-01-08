package k23b.am.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.Logger;

/**
 * Data access layer for request objects.
 */
public class RequestDao {

    private static final Logger log = Logger.getLogger(RequestDao.class);

    /**
     * Creates a new request using the data provided.
     * 
     * @param hash the agent's hash value.
     * @param deviceName the agent's device name.
     * @param interfaceIP the agent's interface IP address.
     * @param interfaceMAC the agent's interface MAC address.
     * @param osVersion the agent's operating system version.
     * @param nmapVersion the agent's nmap version.
     * @param status the request's status.
     * @param timeReceived time the request was received.
     * @return the newly created request's id.
     * @throws DaoException if the newly created request's id could not be retrieved or a data access error occurs.
     */
    public static long create(String hash, String deviceName, String interfaceIP, String interfaceMAC, String osVersion, String nmapVersion, RequestStatus status, Date timeReceived) throws DaoException {

        String sql = new StringBuilder()
                .append("insert into amdb_request ")
                .append("(HASH, DEVICE_NAME, INTERFACE_IP, INTERFACE_MAC, OS_VERSION, NMAP_VERSION, STATUS, TIME_RECEIVED) ")
                .append("values ")
                .append("(?, ?, ?, ?, ?, ?, ?, ?)")
                .toString();

        try (SynchronizedStatement ss = ConnectionSingleton.getInstance().getStatement(sql)) {

            log.debug("");
            log.debug("Executing query: " + sql);

            log.debug("Setting parameter 1 to: " + hash);
            ss.setString(1, hash);

            log.debug("Setting parameter 2 to: " + deviceName);
            ss.setString(2, deviceName);

            log.debug("Setting parameter 3 to: " + interfaceIP);
            ss.setString(3, interfaceIP);

            log.debug("Setting parameter 4 to: " + interfaceMAC);
            ss.setString(4, interfaceMAC);

            log.debug("Setting parameter 5 to: " + osVersion);
            ss.setString(5, osVersion);

            log.debug("Setting parameter 6 to: " + nmapVersion);
            ss.setString(6, nmapVersion);

            log.debug("Setting parameter 7 to: " + status.ordinal());
            ss.setInt(7, status.ordinal());

            log.debug("Setting parameter 8 to: " + timeReceived);
            ss.setTimestamp(8, new Timestamp(timeReceived.getTime()));

            int rows = ss.executeUpdate();
            log.debug(rows + (rows == 1 ? " row " : " rows ") + "inserted.");

            ResultSet rs = ss.getGeneratedKeys();

            if (!rs.next())
                throw new DaoException("Could not retrieve id for created request.");

            long id = rs.getLong(1);
            log.debug("Id returned: " + id);

            return id;

        } catch (SQLException e) {
            // e.printStackTrace();
            log.error(e.getMessage());

            throw new DaoException("Error while creating request with hash: " + hash);
        }
    }

    /**
     * Retrieves an existing request with specified id.
     * 
     * @param requestId the request's id.
     * @return an object representing the request found, or null if a request with specified id was not found.
     * @throws DaoException if a data access error occurs.
     */
    public static RequestDao findById(long requestId) throws DaoException {

        String sql = new StringBuilder()
                .append("select * from amdb_request ")
                .append("where ")
                .append("REQUEST_ID = ?")
                .toString();

        try (SynchronizedStatement ss = ConnectionSingleton.getInstance().getStatement(sql)) {

            log.debug("");
            log.debug("Executing query: " + sql);

            log.debug("Setting parameter 1 to: " + requestId);
            ss.setLong(1, requestId);

            ss.executeQuery();

            ResultSet rs = ss.getResultSet();

            if (rs.next()) {

                long id = rs.getLong("REQUEST_ID");
                String hash = rs.getString("HASH");
                String deviceName = rs.getString("DEVICE_NAME");
                String interfaceIP = rs.getString("INTERFACE_IP");
                String interfaceMAC = rs.getString("INTERFACE_MAC");
                String osVersion = rs.getString("OS_VERSION");
                String nmapVersion = rs.getString("NMAP_VERSION");
                int statusValue = rs.getInt("STATUS");
                RequestStatus status = RequestStatus.values()[statusValue];
                Date timeReceived = rs.getTimestamp("TIME_RECEIVED");

                log.debug("1 row selected.");
                return new RequestDao(id, hash, deviceName, interfaceIP, interfaceMAC, osVersion, nmapVersion, status, timeReceived);

            } else {

                log.debug("0 rows selected.");
                return null;
            }

        } catch (SQLException e) {
            // e.printStackTrace();
            log.error(e.getMessage());

            throw new DaoException("Error while finding request by id: " + requestId);
        }
    }

    /**
     * Retrieves an existing request with specified hash.
     * 
     * @param hash the request's hash.
     * @return an object representing the request found, or null if a request with specified hash was not found.
     * @throws DaoException if a data access error occurs.
     */
    public static RequestDao findByHash(String hash) throws DaoException {

        String sql = new StringBuilder()
                .append("select * from amdb_request ")
                .append("where ")
                .append("HASH = ?")
                .toString();

        try (SynchronizedStatement ss = ConnectionSingleton.getInstance().getStatement(sql)) {

            log.debug("");
            log.debug("Executing query: " + sql);

            log.debug("Setting parameter 1 to: " + hash);
            ss.setString(1, hash);

            ss.executeQuery();

            ResultSet rs = ss.getResultSet();

            if (rs.next()) {

                long requestId = rs.getLong("REQUEST_ID");
                String requestHash = rs.getString("HASH");
                String deviceName = rs.getString("DEVICE_NAME");
                String interfaceIP = rs.getString("INTERFACE_IP");
                String interfaceMAC = rs.getString("INTERFACE_MAC");
                String osVersion = rs.getString("OS_VERSION");
                String nmapVersion = rs.getString("NMAP_VERSION");
                int statusValue = rs.getInt("STATUS");
                RequestStatus status = RequestStatus.values()[statusValue];
                Date timeReceived = rs.getTimestamp("TIME_RECEIVED");

                log.debug("1 row selected.");
                return new RequestDao(requestId, requestHash, deviceName, interfaceIP, interfaceMAC, osVersion, nmapVersion, status, timeReceived);

            } else {

                log.debug("0 rows selected.");
                return null;
            }

        } catch (SQLException e) {
            // e.printStackTrace();
            log.error(e.getMessage());

            throw new DaoException("Error while finding request by hash: " + hash);
        }
    }

    /**
     * Retrieves all requests currently in store.
     * 
     * @return a set of objects, each representing a request.
     * @throws DaoException if a data access error occurs.
     */
    public static Set<RequestDao> findAll() throws DaoException {

        String sql = new StringBuilder()
                .append("select * from amdb_request")
                .toString();

        try (SynchronizedStatement ss = ConnectionSingleton.getInstance().getStatement(sql)) {

            log.debug("");
            log.debug("Executing query: " + sql);

            ss.executeQuery();

            ResultSet rs = ss.getResultSet();

            Set<RequestDao> requests = new HashSet<RequestDao>();

            int rows = 0;

            while (rs.next()) {

                rows++;
                long requestId = rs.getLong("REQUEST_ID");
                String hash = rs.getString("HASH");
                String deviceName = rs.getString("DEVICE_NAME");
                String interfaceIP = rs.getString("INTERFACE_IP");
                String interfaceMAC = rs.getString("INTERFACE_MAC");
                String osVersion = rs.getString("OS_VERSION");
                String nmapVersion = rs.getString("NMAP_VERSION");
                int statusValue = rs.getInt("STATUS");
                RequestStatus status = RequestStatus.values()[statusValue];
                Date timeReceived = rs.getTimestamp("TIME_RECEIVED");

                requests.add(new RequestDao(requestId, hash, deviceName, interfaceIP, interfaceMAC, osVersion, nmapVersion, status, timeReceived));
            }

            log.debug(rows + (rows == 1 ? " row " : " rows ") + "selected.");
            return requests;

        } catch (SQLException e) {
            // e.printStackTrace();
            log.error(e.getMessage());

            throw new DaoException("Error while fetching all requests.");
        }
    }

    /**
     * Retrieves all requests that have a specific status.
     * 
     * @param requestStatus status of the requests.
     * @return a set of objects, each representing a request.
     * @throws DaoException if a data access error occurs.
     */
    public static Set<RequestDao> findAllWithStatus(RequestStatus requestStatus) throws DaoException {

        String sql = new StringBuilder()
                .append("select * from amdb_request ")
                .append("where ")
                .append("STATUS = ?")
                .toString();

        try (SynchronizedStatement ss = ConnectionSingleton.getInstance().getStatement(sql)) {

            log.debug("");
            log.debug("Executing query: " + sql);

            log.debug("Setting parameter 1 to: " + requestStatus.ordinal());
            ss.setLong(1, requestStatus.ordinal());

            ss.executeQuery();

            ResultSet rs = ss.getResultSet();

            Set<RequestDao> requests = new HashSet<RequestDao>();

            int rows = 0;

            while (rs.next()) {

                rows++;
                long requestId = rs.getLong("REQUEST_ID");
                String hash = rs.getString("HASH");
                String deviceName = rs.getString("DEVICE_NAME");
                String interfaceIP = rs.getString("INTERFACE_IP");
                String interfaceMAC = rs.getString("INTERFACE_MAC");
                String osVersion = rs.getString("OS_VERSION");
                String nmapVersion = rs.getString("NMAP_VERSION");
                int statusValue = rs.getInt("STATUS");
                RequestStatus status = RequestStatus.values()[statusValue];
                Date timeReceived = rs.getTimestamp("TIME_RECEIVED");

                requests.add(new RequestDao(requestId, hash, deviceName, interfaceIP, interfaceMAC, osVersion, nmapVersion, status, timeReceived));
            }

            log.debug(rows + (rows == 1 ? " row " : " rows ") + "selected.");
            return requests;

        } catch (SQLException e) {
            // e.printStackTrace();
            log.error(e.getMessage());

            throw new DaoException("Error while fetching requests with status: " + requestStatus);
        }
    }

    /**
     * Sets the status of a request to a specific value.
     * 
     * @param requestId the request's id.
     * @param requestStatus the request's status.
     * @throws DaoException if the request's status could not be changed to the specified value or a data access error occurs.
     */
    public static void setStatus(long requestId, RequestStatus requestStatus) throws DaoException {

        String sql = new StringBuilder()
                .append("update amdb_request set ")
                .append("STATUS = ? ")
                .append("where ")
                .append("REQUEST_ID = ?")
                .toString();

        try (SynchronizedStatement ss = ConnectionSingleton.getInstance().getStatement(sql)) {

            log.debug("");
            log.debug("Executing query: " + sql);

            log.debug("Setting parameter 1 to: " + requestStatus.ordinal());
            ss.setInt(1, requestStatus.ordinal());

            log.debug("Setting parameter 2 to: " + requestId);
            ss.setLong(2, requestId);

            int rows = ss.executeUpdate();
            log.debug(rows + (rows == 1 ? " row " : " rows ") + "updated.");

            if (rows != 1)
                throw new DaoException("Could not update status to " + requestStatus + " on request with id: " + requestId);

        } catch (SQLException e) {
            // e.printStackTrace();
            log.error(e.getMessage());

            throw new DaoException("Error while setting status to " + requestStatus + " on request with id: " + requestId);
        }
    }

    /**
     * Sets a request's received time to a specific value.
     * 
     * @param requestId the request's id.
     * @param timeReceived the request's received time.
     * @throws DaoException if the request's received time could not be changed to the specified value or a data access error occurs.
     */
    public static void setTimeReceived(long requestId, Date timeReceived) throws DaoException {

        String sql = new StringBuilder()
                .append("update amdb_request set ")
                .append("TIME_RECEIVED = ? ")
                .append("where ")
                .append("REQUEST_ID = ?")
                .toString();

        try (SynchronizedStatement ss = ConnectionSingleton.getInstance().getStatement(sql)) {

            log.debug("");
            log.debug("Executing query: " + sql);

            log.debug("Setting parameter 1 to: " + timeReceived);
            ss.setTimestamp(1, new Timestamp(timeReceived.getTime()));

            log.debug("Setting parameter 2 to: " + requestId);
            ss.setLong(2, requestId);

            int rows = ss.executeUpdate();
            log.debug(rows + (rows == 1 ? " row " : " rows ") + "updated.");

            if (rows != 1)
                throw new DaoException("Could not update time received to " + timeReceived + " on request with id: " + requestId);

        } catch (SQLException e) {
            // e.printStackTrace();
            log.error(e.getMessage());

            throw new DaoException("Error while setting time received to " + timeReceived + " on request with id: " + requestId);
        }
    }

    long requestId;
    String hash;
    String deviceName;
    String interfaceIP;
    String interfaceMAC;
    String osVersion;
    String nmapVersion;
    RequestStatus requestStatus;
    Date timeReceived;

    public long getRequestId() {
        return requestId;
    }

    public String getHash() {
        return hash;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public String getInterfaceIP() {
        return interfaceIP;
    }

    public String getInterfaceMAC() {
        return interfaceMAC;
    }

    public String getOsVersion() {
        return osVersion;
    }

    public String getNmapVersion() {
        return nmapVersion;
    }

    public RequestStatus getRequestStatus() {
        return requestStatus;
    }

    public void setRequestStatus(RequestStatus requestStatus) {
        this.requestStatus = requestStatus;
    }

    public Date getTimeReceived() {
        return timeReceived;
    }

    public void setTimeReceived(Date timeReceived) {
        this.timeReceived = timeReceived;
    }

    private RequestDao(long requestId, String hash, String deviceName, String interfaceIP, String interfaceMAC, String osVersion, String nmapVersion, RequestStatus status, Date timeReceived) {
        super();
        this.requestId = requestId;
        this.hash = hash;
        this.deviceName = deviceName;
        this.interfaceIP = interfaceIP;
        this.interfaceMAC = interfaceMAC;
        this.osVersion = osVersion;
        this.nmapVersion = nmapVersion;
        this.requestStatus = status;
        this.timeReceived = timeReceived;
    }
}
