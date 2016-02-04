package k23b.ac.db.srv;

import java.util.List;
import java.util.Set;

import android.util.Log;
import k23b.ac.db.dao.DaoException;
import k23b.ac.db.dao.JobDao;
import k23b.ac.db.dao.UserDao;

public class UserSrv {

    public static UserDao create(String username, String password) throws SrvException {

        try {

            synchronized (UserDao.class) {

                UserDao ud = UserDao.findUserByUsername(username);

                if (ud != null)
                    return null;

                String name = UserDao.createUser(username, password);

                return UserDao.findUserByUsername(name);
            }

        } catch (DaoException e) {
            throw new SrvException("Data access error while creating User with username: " + username);
        }
    }

    public static UserDao createUserWithJobs(UserDao user, List<JobDao> jobList) throws SrvException {

        try {

            synchronized (UserDao.class) {

                UserDao ud = UserDao.findUserByUsername(user.getUsername());
                String createdUser = "";
                if (ud == null) {
                    createdUser = UserDao.createUser(user.getUsername(), user.getPassword());
                    if (createdUser == null)
                        throw new SrvException("User with username: " + user.getUsername() + " with " + jobList.size()
                                + " Jobs could NOT be Created");
                    ud = UserDao.findUserByUsername(createdUser);
                }

                synchronized (JobDao.class) {

                    for (JobDao j : jobList) {
                        long jobId = JobDao.createJob(j.getParameters(), ud.getUsername(), j.getAgentId(),
                                j.getTime_assigned(), j.getPeriodic(), j.getPeriod());

                        JobDao job = JobDao.findJobById(jobId);

                        if (job == null)
                            throw new SrvException("Could not create Job from agent with id: " + j.getAgentId()
                                    + " for User " + user.getUsername());
                    }

                    return (ud == null ? UserDao.findUserByUsername(createdUser) : ud);
                }
            }

        } catch (DaoException e) {
            throw new SrvException("Data access error while creating User with username: " + user.getUsername()
                    + " with " + jobList.size() + " Jobs");
        }

    }

    public static UserDao find(String username) throws SrvException {

        try {

            synchronized (UserDao.class) {

                return UserDao.findUserByUsername(username);
            }

        } catch (DaoException e) {
            throw new SrvException("Data access error while finding User by username: " + username);
        }
    }

    public static void delete(String username) throws SrvException {

        try {

            synchronized (UserDao.class) {

                UserDao ud = UserDao.findUserByUsername(username);

                if (ud == null)
                    return;

                synchronized (JobDao.class) {

                    Set<JobDao> jobs = JobDao.findAllJobsFromUsername(username);

                    for (JobDao jd : jobs)
                        JobDao.deleteJob(jd.getId());

                    UserDao.deleteUser(username);
                }
            }

        } catch (DaoException e) {

            throw new SrvException("Data access error while deleting user with username: " + username);
        }
    }

    public static void tryDelete(String username) throws SrvException {

        try {

            synchronized (UserDao.class) {

                UserDao ud = UserDao.findUserByUsername(username);

                if (ud == null)
                    return;

                synchronized (JobDao.class) {

                    Set<JobDao> jobs = JobDao.findAllJobsFromUsername(username);

                    if (jobs.isEmpty()) {
                        UserDao.deleteUser(username);
                        Log.d(UserSrv.class.getName(),
                                "No Jobs from User: " + username + ". User Deleted Successfully.");
                        return;
                    }
                    Log.d(UserSrv.class.getName(),
                            "There are still Jobs remaining from User: " + username + ". User was not Deleted.");
                }
            }

        } catch (DaoException e) {

            throw new SrvException("Data access error while deleting user with username: " + username);
        }

    }

    public static Set<UserDao> findAll() throws SrvException {

        try {

            synchronized (UserDao.class) {

                return UserDao.findAll();
            }

        } catch (DaoException e) {

            throw new SrvException("Data access error while finding all jobs.");
        }
    }
}
