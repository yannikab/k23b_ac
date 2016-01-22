package k23b.am.srv;

import k23b.am.cc.AdminCC;
import k23b.am.cc.UserCC;
import k23b.am.dao.AdminDao;
import k23b.am.dao.DaoException;
import k23b.am.dao.UserDao;

/**
 * Service layer for user objects.
 */
public class UserSrv {

    private static volatile boolean lock = true;

    /**
     * Sets the service layer's underlying data store locking policy.
     * 
     * @param lock if set to true, individual underlying data stores will be locked upon access.
     */
    public static void setLock(boolean lock) {

        UserSrv.lock = lock;
    }

    /**
     * Creates a user using the specified data and returns an object representing it.
     * 
     * @param username the user's username.
     * @param password the user's password.
     * @return the created user object containing its generated id, or null if the user was not found after creating it.
     * @throws UserCredentialsException if the username is too long or a user already exists with specified username.
     * @throws SrvException if the user could not be created or a data access error occurs.
     */
    public static UserDao create(String username, String password) throws UserCredentialsException, SrvException {

        if (username.length() > 40)
            throw new SrvException("Can not create user. Username exceeds maximum length: " + username);

        try {

            synchronized (lock ? UserCC.class : new Object()) {

                if ((UserCC.findByUsername(username) != null))
                    throw new UserCredentialsException("Can not create user. Another user already exists with username: " + username);

                long userId = UserCC.create(username, password, false);

                if (userId == 0)
                    throw new SrvException("Could not create user with username: " + username);

                return UserCC.findById(userId);
            }

        } catch (DaoException e) {
            // e.printStackTrace();
            throw new SrvException("Data access error while creating user with username: " + username);
        }
    }

    /**
     * Retrieves the user with specified id.
     * 
     * @param adminId the user's id.
     * @return the user found or null if a user with specified id was not found.
     * @throws SrvException if a data access error occurs.
     */
    public static UserDao findById(long adminId) throws SrvException {

        try {

            synchronized (lock ? UserCC.class : new Object()) {

                return UserCC.findById(adminId);
            }

        } catch (DaoException e) {
            // e.printStackTrace();
            throw new SrvException("Data access error while finding user by id: " + adminId);
        }
    }

    /**
     * Retrieves the user with specified username.
     * 
     * @param username the user's username.
     * @return the user found or null if a user with specified username was not found.
     * @throws SrvException if a data access error occurs.
     */
    public static UserDao findByUsername(String username) throws SrvException {

        try {

            synchronized (lock ? UserCC.class : new Object()) {

                return UserCC.findByUsername(username);
            }

        } catch (DaoException e) {
            // e.printStackTrace();
            throw new SrvException("Data access error while finding user by username: " + username);
        }
    }

    /**
     * Logs in a user given its credentials.
     * 
     * @param username the user's username.
     * @param password the user's password.
     * @throws UserCredentialsException if a user with specified username does not exist, password is incorrect, or the user could not be logged in.
     * @throws SrvException if a data access error occurs.
     */
    public static void login(String username, String password) throws UserCredentialsException, SrvException {

        try {

            synchronized (lock ? UserCC.class : new Object()) {

                UserDao u = UserCC.findByUsername(username);

                if (u == null)
                    throw new UserCredentialsException("Can not login user. User does not exist with username: " + username);

                if (!u.getPassword().equals(password))
                    throw new UserCredentialsException("Can not login user. Incorrect password.");

                UserCC.setActive(u.getUserId(), true);
            }

        } catch (DaoException e) {
            // e.printStackTrace();
            throw new SrvException("Data access error while logging in user with username: " + username);
        }
    }

    /**
     * Logs out a user.
     * 
     * @param username the user's username.
     * @throws SrvException if a user with specified username does not exist, the user is not logged in, the user could not be logged out, or a data access error occurs.
     */
    public static void logout(String username) throws SrvException {

        try {

            synchronized (lock ? UserCC.class : new Object()) {

                UserDao u = UserCC.findByUsername(username);

                if (u == null)
                    throw new SrvException("Can not logout user. User does not exist with username: " + username);

                if (!u.getActive())
                    throw new SrvException("Can not logout user. User with username " + username + " is not logged in.");

                UserCC.setActive(u.getUserId(), false);
            }

        } catch (DaoException e) {
            // e.printStackTrace();
            throw new SrvException("Data access error while logging out user with username: " + username);
        }
    }

    /**
     * Checks for a user's login status given its username.
     * 
     * @param username the user's username.
     * @return true if the user is logged in, false otherwise.
     * @throws UserCredentialsException if a user with specified username does not exist.
     * @throws SrvException if a data access error occurs.
     */
    public static boolean isLoggedIn(String username) throws UserCredentialsException, SrvException {

        try {

            synchronized (lock ? UserCC.class : new Object()) {

                UserDao u = UserCC.findByUsername(username);

                if (u == null)
                    throw new UserCredentialsException("Can not check user login status. User does not exist with username: " + username);

                return u.getActive();
            }

        } catch (DaoException e) {
            // e.printStackTrace();
            throw new SrvException("Data access error while checking login status of user with username: " + username);
        }
    }

    /**
     * Accepts a user on behalf of a specified admin.
     * 
     * @param username the user's username.
     * @param adminId id of the admin that is accepting the user.
     * @throws SrvException if the admin does not exist or is not logged in, the user does not exist or is already accepted, or a data access error occurs.
     */
    public static void accept(String username, long adminId) throws SrvException {

        try {

            synchronized (lock ? AdminCC.class : new Object()) {

                // make sure that the admin accepting the user exists and is logged in

                AdminDao ad = AdminCC.findById(adminId);

                if (ad == null)
                    throw new SrvException("Can not accept user. Could not find admin with id: " + adminId);

                if (!ad.getActive())
                    throw new SrvException("Can not accept user. Admin with id " + adminId + " is not logged in.");

                synchronized (lock ? UserCC.class : new Object()) {

                    // make sure that user exists and that it is not accepted
                    UserDao ud = UserCC.findByUsername(username);

                    if (ud == null)
                        throw new SrvException("Can not accept user. Could not find user with username: " + username);

                    if (ud.getAdminId() > 0)
                        throw new SrvException("Can not accept user. An admin has already accepted user with username: " + username);

                    UserCC.setAdminId(ud.getUserId(), adminId);
                }
            }

        } catch (DaoException e) {
            // e.printStackTrace();
            throw new SrvException("Data access error while accepting user with username: " + username);
        }
    }

    /**
     * Rejects a user on behalf of a specified admin.
     * 
     * @param username the user's username.
     * @param adminId id of the admin that is rejecting the user.
     * @throws SrvException if the admin does not exist or is not logged in, the user does not exist or is not accepted, or a data access error occurs.
     */
    public static void reject(String username, long adminId) throws SrvException {

        try {

            synchronized (lock ? AdminCC.class : new Object()) {

                // make sure that the admin accepting the user exists and is logged in

                AdminDao ad = AdminCC.findById(adminId);

                if (ad == null)
                    throw new SrvException("Can not reject user. Could not find admin with id: " + adminId);

                if (!ad.getActive())
                    throw new SrvException("Can not reject user. Admin with id " + adminId + " is not logged in.");

                synchronized (lock ? UserCC.class : new Object()) {

                    // make sure that user exists and that it is not accepted
                    UserDao ud = UserCC.findByUsername(username);

                    if (ud == null)
                        throw new SrvException("Can not reject user. Could not find user with username: " + username);

                    if (ud.getAdminId() == 0)
                        throw new SrvException("Can not reject user. Status is not accepted for user with username: " + username);

                    UserCC.setAdminId(ud.getUserId(), 0);
                }
            }

        } catch (DaoException e) {
            // e.printStackTrace();
            throw new SrvException("Data access error while accepting user with username: " + username);
        }
    }

    /**
     * Checks for a user's accepted status given its username.
     * 
     * @param username the user's username.
     * @return true if the user has been accepted by some admin, false otherwise.
     * @throws UserCredentialsException if a user with specified username does not exist.
     * @throws SrvException if a data access error occurs.
     */
    public static boolean isAccepted(String username) throws UserCredentialsException, SrvException {

        try {

            synchronized (lock ? UserCC.class : new Object()) {

                UserDao u = UserCC.findByUsername(username);

                if (u == null)
                    throw new UserCredentialsException("Can not check user accept status. User does not exist with username: " + username);

                return u.getAdminId() > 0;
            }

        } catch (DaoException e) {
            // e.printStackTrace();
            throw new SrvException("Data access error while checking accept status of user with username: " + username);
        }
    }
}
