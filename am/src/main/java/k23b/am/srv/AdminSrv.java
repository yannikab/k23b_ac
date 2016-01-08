package k23b.am.srv;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import k23b.am.cc.AdminCC;
import k23b.am.dao.AdminDao;
import k23b.am.dao.DaoException;

/**
 * Service layer for admin objects.
 */
public class AdminSrv {

    private static volatile boolean lock = true;

    /**
     * Sets the service layer's underlying data store locking policy.
     * 
     * @param lock if set to true, individual underlying data stores will be locked upon access.
     */
    public static void setLock(boolean lock) {

        AdminSrv.lock = lock;
    }

    /**
     * Creates an admin using the specified data and returns an object representing it.
     * 
     * @param username the admin's username.
     * @param password the admin's password.
     * @return the created admin object containing its generated id, or null if the admin was not found after creating it.
     * @throws SrvException if an admin already exists with specified username, the admin could not be created, or a data access error occurs.
     */
    public static AdminDao create(String username, String password) throws SrvException {

        try {

            synchronized (lock ? AdminCC.class : new Object()) {

                if ((AdminCC.findByUsername(username) != null))
                    throw new SrvException("Can not create admin. Another admin already exists with username: " + username);

                long adminId = AdminCC.create(username, hashForPassword(password), false);

                if (adminId == 0)
                    throw new SrvException("Could not create admin with username: " + username);

                return AdminCC.findById(adminId);
            }

        } catch (DaoException e) {
            // e.printStackTrace();
            throw new SrvException("Data access error while creating admin with username: " + username);
        }
    }

    /**
     * Retrieves the admin with specified id.
     * 
     * @param adminId the admin's id.
     * @return the admin found or null if an admin with specified id was not found.
     * @throws SrvException if a data access error occurs.
     */
    public static AdminDao findById(long adminId) throws SrvException {

        try {

            synchronized (lock ? AdminCC.class : new Object()) {

                return AdminCC.findById(adminId);
            }

        } catch (DaoException e) {
            // e.printStackTrace();
            throw new SrvException("Data access error while finding admin by id: " + adminId);
        }
    }

    /**
     * Retrieves the admin with specified username.
     * 
     * @param username the admin's username.
     * @return the admin found or null if an admin with specified username was not found.
     * @throws SrvException if a data access error occurs.
     */
    public static AdminDao findByUsername(String username) throws SrvException {

        try {

            synchronized (lock ? AdminCC.class : new Object()) {

                return AdminCC.findByUsername(username);
            }

        } catch (DaoException e) {
            // e.printStackTrace();
            throw new SrvException("Data access error while finding admin by username: " + username);
        }
    }

    /**
     * Logs in an admin given its credentials.
     * 
     * @param username the admin's username.
     * @param password the admin's password.
     * @throws SrvException if an admin with specified username does not exist, password is incorrect, the admin could not be logged in, or a data access error occurs.
     */
    public static void login(String username, String password) throws SrvException {

        try {

            synchronized (lock ? AdminCC.class : new Object()) {

                AdminDao a = AdminCC.findByUsername(username);

                if (a == null)
                    throw new SrvException("Can not login admin. Admin does not exist with username: " + username);

                if (!a.getPassword().equals(hashForPassword(password)))
                    throw new SrvException("Can not login admin. Incorrect password.");

                AdminCC.setActive(a.getAdminId(), true);
            }

        } catch (DaoException e) {
            // e.printStackTrace();
            throw new SrvException("Data access error while logging in admin with username: " + username);
        }
    }

    /**
     * Logs out an admin.
     * 
     * @param username the admin's username.
     * @throws SrvException if an admin with specified username does not exist, the admin is not logged in, the admin could not be logged out, or a data access error occurs.
     */
    public static void logout(String username) throws SrvException {

        try {

            synchronized (lock ? AdminCC.class : new Object()) {

                AdminDao a = AdminCC.findByUsername(username);

                if (a == null)
                    throw new SrvException("Can not logout admin. Admin does not exist with username: " + username);

                if (!a.getActive())
                    throw new SrvException("Can not logout admin. Admin with username " + username + " is not logged in.");

                AdminCC.setActive(a.getAdminId(), false);
            }

        } catch (DaoException e) {
            // e.printStackTrace();
            throw new SrvException("Data access error while logging out admin with username: " + username);
        }
    }

    /**
     * Checks for an admin's login status given its username.
     * 
     * @param username the admin's username.
     * @return true if the admin is logged in, false otherwise.
     * @throws SrvException if an admin with specified username does not exist, or a data access error occurs.
     */
    public static boolean isLoggedIn(String username) throws SrvException {

        try {

            synchronized (lock ? AdminCC.class : new Object()) {

                AdminDao a = AdminCC.findByUsername(username);

                if (a == null)
                    throw new SrvException("Can not check admin login status. Admin does not exist with username: " + username);

                return a.getActive();
            }

        } catch (DaoException e) {
            // e.printStackTrace();
            throw new SrvException("Data access error while checking login status of admin with username: " + username);
        }
    }

    private static String hashForPassword(String password) throws SrvException {

        try {

            MessageDigest md = MessageDigest.getInstance("SHA-256");

            md.update(password.getBytes());

            byte[] digest = md.digest();

            return bytesToHex(digest);

        } catch (NoSuchAlgorithmException e) {
            // e.printStackTrace();
            throw new SrvException(e.getMessage());
        }
    }

    private static final char[] hexArray = "0123456789ABCDEF".toCharArray();

    private static String bytesToHex(byte[] bytes) {

        char[] hexChars = new char[bytes.length * 2];

        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }

        return new String(hexChars);
    }
}
