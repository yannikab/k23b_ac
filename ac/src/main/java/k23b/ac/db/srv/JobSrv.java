package k23b.ac.db.srv;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import android.util.Log;
import k23b.ac.db.dao.DaoException;
import k23b.ac.db.dao.JobDao;
import k23b.ac.db.dao.UserDao;

/**
 * A service layer in which methods from the DAO layer are invoked for the manipulation of Jobs.
 */
public class JobSrv {

    /**
     * Creation of a Job object
     *  
     * @param username
     * @param agentId
     * @param params
     * @param periodic
     * @param period
     * @return An instance of JobDao which includes the info from the selected column from the Jobs Table
     * @throws SrvException
     */
    public static JobDao create(String username, long agentId, String params, boolean periodic, int period) throws SrvException {

        try {

            synchronized (UserDao.class) {

                UserDao user = UserDao.findUserByUsername(username);

                if (user == null) {
                    Log.e(JobSrv.class.getName(),
                            "Could not create Job for agent with id: " + agentId + ". No such User.");
                    return null;
                }

                synchronized (JobDao.class) {

                    long jobId = JobDao.createJob(params, username, agentId, new Date(System.currentTimeMillis()),
                            periodic, period);

                    JobDao job = JobDao.findJobById(jobId);

                    if (job == null)
                        throw new SrvException("Could not create Job for agent with id: " + agentId);

                    return job;
                }
            }

        } catch (DaoException e) {
            // e.printStackTrace();
            throw new SrvException("Data access error while creating job for agent with id: " + agentId);
        }
    }

    /**
     * Searches for a Job object with a given jobId.
     * 
     * @param jobId
     * @return An instance of JobDao which includes the info from the selected column from the Jobs Table
     * @throws SrvException
     */
    public static JobDao findById(long jobId) throws SrvException {

        try {

            synchronized (JobDao.class) {

                return JobDao.findJobById(jobId);
            }

        } catch (DaoException e) {
            throw new SrvException("Data access error while searching for Job with id: " + jobId);
        }
    }
    
    /**
     * Deletes a Job object with a given jobId.
     * 
     * @param jobId
     * @throws SrvException
     */
    public static void delete(long jobId) throws SrvException {

        try {

            synchronized (JobDao.class) {

                if (JobDao.findJobById(jobId) == null)
                    return;

                JobDao.deleteJob(jobId);
            }

        } catch (DaoException e) {
            throw new SrvException("Data access error while sending (deleting) Job with id: " + jobId);
        }
    }
    
    /**
     * Searches for Job objects created by a User with a given username.
     * 
     * @param username
     * @return A set of JobDao instances which include the info from the selected columns from the Jobs Table
     * @throws SrvException
     */
    public static Set<JobDao> findAllJobsFromUsername(String username) throws SrvException {

        try {

            synchronized (UserDao.class) {

                if (UserDao.findUserByUsername(username) == null)
                    return new HashSet<JobDao>();

                synchronized (JobDao.class) {

                    return JobDao.findAllJobsFromUsername(username);
                }
            }

        } catch (DaoException e) {
            throw new SrvException("Data access error while finding Jobs from User with username: " + username);
        }
    }
}
