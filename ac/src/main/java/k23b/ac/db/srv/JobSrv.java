package k23b.ac.db.srv;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import android.util.Log;
import k23b.ac.db.dao.DaoException;
import k23b.ac.db.dao.JobDao;
import k23b.ac.db.dao.UserDao;

public class JobSrv {

    public static JobDao create(String username, long agentId, String params, boolean periodic, int period) throws SrvException {

        try {

            synchronized (UserDao.class) {

                UserDao user = UserDao.findUserByUsername(username);

                if (user == null){
                	Log.e(JobSrv.class.getName() ,"Could not create Job for agent with id: " + agentId+". No such User.");
                	return null;
                }
                // not using active
                // if (!user.isActive())
                // throw new SrvException("Can not create job. User with username " + username + " is not logged in.");

                synchronized (JobDao.class) {

                    long jobId = JobDao.createJob(params, username, agentId, new Date(System.currentTimeMillis()), periodic, period);

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

    public static JobDao findById(long jobId) throws SrvException {

        try {

            synchronized (JobDao.class) {

                return JobDao.findJobById(jobId);
            }

        } catch (DaoException e) {
            throw new SrvException("Data access error while searching for Job with id: " + jobId);
        }
    }

    // public static Set<Job> findAllWithAgentId(int agentId) {
    //
    // return JobDao.findAllJobsFromAgentId(agentId);
    // }

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

    // public static Set<Job> findAllJobsFromActiveUsers() {
    //
    // return JobDao.findAllJobsFromActiveUsers();
    //
    // }

    // public static void setTimeAssigned(int jobId) throws SrvException {
    //
    // try {
    //
    // Job job = JobDao.findJobById(jobId);
    //
    // if (job == null)
    // throw new SrvException("Cannot set Time Assigned. No such Job with id: " + jobId);
    //
    // JobDao.setTimeAssigned(jobId, new Date());
    //
    // } catch (DaoException e) {
    // throw new SrvException("Data access error while setting Time Assigned for Job with id: " + jobId);
    // }
    //
    // }
}
