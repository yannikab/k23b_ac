package k23b.ac;

import k23b.ac.dao.UserDao;
import k23b.ac.rest.User;

public class UserDaoFactory extends UserDao {

    protected UserDaoFactory(String username, String password) {
        
        super(username, password, false);
    }

    public static UserDao fromUser(User u) {

        return new UserDaoFactory(u.getUsername(), u.getPassword());
    }
}
