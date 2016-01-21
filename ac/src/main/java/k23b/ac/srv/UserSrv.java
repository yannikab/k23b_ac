package k23b.ac.srv;

import java.util.Set;

import k23b.ac.dao.DaoException;
import k23b.ac.dao.JobDao;
import k23b.ac.dao.UserDao;

public class UserSrv {

    public static UserDao create(String username, String password) throws SrvException {

        try {

            if (UserDao.findUserByUsername(username) != null)
                throw new SrvException("Cannot create User. Another User already exists with username: " + username);

            return UserDao.createUser(username, password);

        } catch (DaoException e) {
            throw new SrvException("Data access error while creating User with username: " + username);
        }
    }

    public static UserDao find(String username) throws SrvException {

        try {

            return UserDao.findUserByUsername(username);

        } catch (DaoException e) {
            throw new SrvException("Data access error while finding User by username: " + username);
        }
    }

    public static void delete(String username) throws SrvException {

        try {

            if (!JobDao.findAllJobsFromUsername(username).isEmpty())
                throw new SrvException("Can not delete user. User " + username + " still has jobs in the database.");

            UserDao.deleteUser(username);

        } catch (DaoException e) {

            throw new SrvException("Data access error while deleting user with username: " + username);
        }
    }

    public static Set<UserDao> findAll() throws SrvException {

        try {

            return UserDao.findAll();

        } catch (DaoException e) {

            throw new SrvException("Data access error while finding all jobs.");
        }
    }

    // public static void login(String username, String password) throws SrvException {
    //
    // try {
    //
    // User u = UserDao.findUserbyUsername(username);
    //
    // if (u == null)
    // throw new SrvException("Can not login User. User does not exist with username: " + username);
    //
    // if (!u.getPassword().equals(hashForPassword(password)))
    // throw new SrvException("Can not login User. Incorrect password.");
    //
    // UserDao.setActive(username);
    //
    // } catch (DaoException e) {
    // throw new SrvException("Data access error while logging in User with username: " + username);
    // }
    // }
    //
    // public static void logout(String username) throws SrvException {
    //
    // try {
    //
    // User u = UserDao.findUserbyUsername(username);
    //
    // if (u == null)
    // throw new SrvException("Can not logout User. User does not exist with username: " + username);
    //
    // UserDao.setInactive(username);
    //
    // } catch (DaoException e) {
    //
    // throw new SrvException("Data access error while logging out User with username: " + username);
    // }
    // }
    //
    // public static boolean isLoggedIn(String username) throws SrvException {
    //
    // try {
    //
    // User u = UserDao.findUserbyUsername(username);
    //
    // if (u == null)
    // throw new SrvException("Can check login status of User. User does not exist with username: " + username);
    //
    // // return u.isActive();
    //
    // } catch (DaoException e) {
    // throw new SrvException("Data access error while checking login status of User with username: " + username);
    // }
    //
    // }
}
