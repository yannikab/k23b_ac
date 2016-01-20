package k23b.ac.srv;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import k23b.ac.dao.DaoException;
import k23b.ac.dao.User;
import k23b.ac.dao.UserDao;
import k23b.ac.srv.SrvException;

public class UserSrv {

    public static User register(String username, String password) throws SrvException {

        try {

            if (UserDao.findUserbyUsername(username) != null)
                throw new SrvException("Cannot create User. Another User already exists with username: " + username);

            return UserDao.createUser(username, password, false);

        } catch (DaoException e) {
            throw new SrvException("Data access error while creating User with username: " + username);
        }
    }

    public static User findByUsername(String username) throws SrvException {

        try {

            return UserDao.findUserbyUsername(username);

        } catch (DaoException e) {
            throw new SrvException("Data access error while finding User by username: " + username);
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
