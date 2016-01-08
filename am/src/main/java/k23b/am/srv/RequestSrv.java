package k23b.am.srv;

import java.time.Instant;
import java.util.Date;
import java.util.Set;

import k23b.am.cc.AdminCC;
import k23b.am.cc.RequestCC;
import k23b.am.dao.AdminDao;
import k23b.am.dao.DaoException;
import k23b.am.dao.RequestDao;
import k23b.am.dao.RequestStatus;

/**
 * Service layer for request objects.
 */
public class RequestSrv {

    private static volatile boolean lock = true;

    /**
     * Sets the service layer's underlying data store locking policy.
     * 
     * @param lock if set to true, individual underlying data stores will be locked upon access.
     */
    public static void setLock(boolean lock) {

        RequestSrv.lock = lock;
    }

    /**
     * Creates a request using the specified data and returns an object representing it.
     * 
     * @param hash the agent's hash value.
     * @param deviceName the agent's device name.
     * @param interfaceIP the agent's interface IP address.
     * @param interfaceMAC the agent's interface MAC address.
     * @param osVersion the agent's operating system version.
     * @param nmapVersion the agent's nmap version.
     * @return the created request object containing its generated id, or null if the request was not found after creating it.
     * @throws SrvException if a request already exists with specified hash, the request could not be created, or a data access error occurs.
     */
    public static RequestDao create(String hash, String deviceName, String interfaceIP, String interfaceMAC, String osVersion, String nmapVersion) throws SrvException {

        try {

            synchronized (lock ? RequestCC.class : new Object()) {

                if (RequestCC.findByHash(hash) != null)
                    throw new SrvException("Request already exists with hash: " + hash);

                long requestId = RequestCC.create(hash, deviceName, interfaceIP, interfaceMAC, osVersion, nmapVersion, RequestStatus.PENDING, Date.from(Instant.now()));

                if (requestId == 0)
                    throw new SrvException("Could not create request with hash: " + hash);

                return RequestCC.findById(requestId);
            }

        } catch (DaoException e) {
            // e.printStackTrace();
            throw new SrvException("Data access error while creating request with hash: " + hash);
        }
    }

    /**
     * Retrieves the request with specified id.
     * 
     * @param requestId the request's id.
     * @return the request found or null if a request with specified id was not found.
     * @throws SrvException if a data access error occurs.
     */
    public static RequestDao findById(long requestId) throws SrvException {

        try {

            synchronized (lock ? RequestCC.class : new Object()) {

                return RequestCC.findById(requestId);
            }

        } catch (DaoException e) {
            // e.printStackTrace();
            throw new SrvException("Data access error while finding request with id: " + requestId);
        }
    }

    /**
     * Retrieves the request with specified id.
     * 
     * @param hash the request's hash.
     * @return the request found or null if a request with specified hash was not found.
     * @throws SrvException if a data access error occurs.
     */
    public static RequestDao findByHash(String hash) throws SrvException {

        try {

            synchronized (lock ? RequestCC.class : new Object()) {

                return RequestCC.findByHash(hash);
            }

        } catch (DaoException e) {
            // e.printStackTrace();
            throw new SrvException("Data access error while finding request by hash: " + hash);
        }
    }

    /**
     * Retrieves all requests that have a specific status.
     * 
     * @param requestStatus status of the requests.
     * @return a set of objects, each representing a request.
     * @throws SrvException if a data access error occurs.
     */
    public static Set<RequestDao> findAllWithStatus(RequestStatus requestStatus) throws SrvException {

        try {

            synchronized (lock ? RequestCC.class : new Object()) {

                if (requestStatus == RequestStatus.ALL)
                    return RequestCC.findAll();
                else
                    return RequestCC.findAllWithStatus(requestStatus);
            }

        } catch (DaoException e) {
            // e.printStackTrace();
            throw new SrvException("Data access error while finding requests with status: " + requestStatus);
        }
    }

    /**
     * Changes a request's status to rejected.
     * 
     * @param requestId the request's id.
     * @param adminId id of the admin rejecting the request.
     * @throws SrvException if request id or admin id are invalid, request is already rejected, admin is not logged in, status could not be set, or a data access error occurs.
     */
    public static void reject(long requestId, long adminId) throws SrvException {

        try {

            synchronized (lock ? RequestCC.class : new Object()) {

                RequestDao request = RequestCC.findById(requestId);

                if (request == null)
                    throw new SrvException("Can not reject request. Can not find request with id: " + requestId);

                if (request.getRequestStatus() == RequestStatus.REJECTED)
                    throw new SrvException("Can not reject request. Status is already rejected for request with id: " + requestId);

                synchronized (lock ? AdminCC.class : new Object()) {

                    AdminDao admin = AdminCC.findById(adminId);

                    if (admin == null)
                        throw new SrvException("Can not reject request. Can not find admin with id: " + adminId);

                    if (!admin.getActive())
                        throw new SrvException("Can not reject request. Admin with id " + adminId + " is not logged in.");

                    RequestCC.setStatus(requestId, RequestStatus.REJECTED);
                }
            }

        } catch (DaoException e) {
            // e.printStackTrace();
            throw new SrvException("Data access error while rejecting request with id: " + requestId);
        }
    }

    /**
     * Changes a request's status to pending.
     * 
     * @param requestId the request's id.
     * @param adminId the admin setting the request's status to pending.
     * @throws SrvException if request id or admin id are invalid, request is already pending, admin is not logged in, status could not be set, or a data access error occurs.
     */
    public static void pending(long requestId, long adminId) throws SrvException {

        try {

            synchronized (lock ? RequestCC.class : new Object()) {

                RequestDao request = RequestCC.findById(requestId);

                if (request == null)
                    throw new SrvException("Can not set request status to pending. Can not find request with id: " + requestId);

                if (request.getRequestStatus() == RequestStatus.PENDING)
                    throw new SrvException("Can not set request status to pending. Status is already pending for request with id: " + requestId);

                synchronized (lock ? AdminCC.class : new Object()) {

                    AdminDao admin = AdminCC.findById(adminId);

                    if (admin == null)
                        throw new SrvException("Can not set request status to pending. Can not find admin with id: " + adminId);

                    if (!admin.getActive())
                        throw new SrvException("Can not set request status to pending. Admin with id " + adminId + " is not logged in.");

                    RequestCC.setStatus(requestId, RequestStatus.PENDING);
                }
            }

        } catch (DaoException e) {
            // e.printStackTrace();
            throw new SrvException("Data access error while setting status to pending for request with id: " + requestId);
        }
    }

    /**
     * Sets a request's received time to the current time.
     * 
     * @param requestId the request's id
     * @throws SrvException if a request with specified id does not exist, its received time could not be set, or a data access error occurs.
     */
    public static void updateTimeReceived(long requestId) throws SrvException {

        try {

            synchronized (lock ? RequestCC.class : new Object()) {

                RequestDao request = RequestCC.findById(requestId);

                if (request == null)
                    throw new SrvException("Can not update time received for request. Can not find request with id: " + requestId);

                RequestCC.setTimeReceived(requestId, Date.from(Instant.now()));
            }

        } catch (DaoException e) {
            // e.printStackTrace();
            throw new SrvException("Data access error while updating time received for request with id: " + requestId);
        }
    }
}
