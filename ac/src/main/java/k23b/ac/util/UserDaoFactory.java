package k23b.ac.util;

import k23b.ac.db.dao.UserDao;
import k23b.ac.rest.User;

/**
 * A utility class in which a User object is converted into a UserDao
 *
 */
public class UserDaoFactory extends UserDao {

    protected UserDaoFactory(String username, String password) {

        super(username, password, false);
    }

    public static UserDao fromUser(User u) {

        return new UserDaoFactory(u.getUsername(), u.getPassword());
    }
}
