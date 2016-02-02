package k23b.am.srv;

import java.time.Instant;
import java.util.Date;
import java.util.Set;

import k23b.am.cc.AdminCC;
import k23b.am.cc.AgentCC;
import k23b.am.cc.JobCC;
import k23b.am.cc.UserCC;
import k23b.am.dao.AdminDao;
import k23b.am.dao.AgentDao;
import k23b.am.dao.DaoException;
import k23b.am.dao.JobDao;
import k23b.am.dao.UserDao;

/**
 * Service layer for job objects.
 */
public class JobSrv {

    private static volatile boolean lock = true;

    /**
     * Sets the service layer's underlying data store locking policy.
     * 
     * @param lock if set to true, individual underlying data stores will be locked upon access.
     */
    public static void setLock(boolean lock) {

        JobSrv.lock = lock;
    }

    /**
     * Creates a job for a specific agent, on behalf of a specific admin.
     * 
     * @param agentId id of the agent for which the job is being created.
     * @param adminId id of the admin that is creating the job.
     * @param params job parameters.
     * @param periodic job's periodic property.
     * @param period job's period, if periodic.
     * @return the created job object containing its generated id, or null if the job was not found after creating it.
     * @throws SrvException if agent id or admin id are invalid, the admin is not logged in, the job could not be created, or a data access error occurs.
     */
    public static JobDao create(long agentId, long adminId, Date timeAssigned, String params, boolean periodic, int period) throws SrvException {

        if (timeAssigned == null)
            throw new SrvException(new IllegalArgumentException("Date timeAssigned is null."));

        if (params == null)
            throw new SrvException(new IllegalArgumentException("String params is null."));

        try {

            synchronized (lock ? AgentCC.class : new Object()) {

                AgentDao agent = AgentCC.findById(agentId);

                if (agent == null)
                    throw new SrvException("Can not create job. Could not find agent with id: " + agentId);

                synchronized (lock ? AdminCC.class : new Object()) {

                    AdminDao admin = AdminCC.findById(adminId);

                    if (admin == null)
                        throw new SrvException("Can not create job. Could not find admin with id: " + adminId);

                    if (!admin.getActive())
                        throw new SrvException("Can not create job. Admin with id " + adminId + " is not logged in.");

                    synchronized (lock ? JobCC.class : new Object()) {

                        long jobId = JobCC.create(agentId, adminId, timeAssigned, params, periodic, period);

                        if (jobId == 0)
                            throw new SrvException("Could not create job for agent with id: " + agentId);

                        return JobCC.findById(jobId);
                    }
                }
            }

        } catch (DaoException e) {
            // e.printStackTrace();
            throw new SrvException("Data access error while creating job for agent with id: " + agentId);
        }
    }

    /**
     * Creates a job for a specific agent, on behalf of a specific user.
     * 
     * @param agentId id of the agent for which the job is being created.
     * @param username user's username.
     * @param password user's password.
     * @param params job parameters.
     * @param periodic job's periodic property.
     * @param period job's period, if periodic.
     * @return the created job object containing its generated id, or null if the job was not found after creating it.
     * @throws UserCredentialsException if a user with specified username does not exist, or the password is incorrect.
     * @throws UserApprovalException if the user has not been approved by an admin.
     * @throws SrvException if the job could not be created, or a data access error occurs.
     */
    public static JobDao createForUser(long agentId, String username, String password, Date timeAssigned, String params, boolean periodic, int period) throws UserCredentialsException, UserApprovalException, SrvException {

        if (timeAssigned == null)
            throw new SrvException(new IllegalArgumentException("Date timeAssigned is null."));

        if (params == null)
            throw new SrvException(new IllegalArgumentException("String params is null."));

        try {

            synchronized (lock ? AgentCC.class : new Object()) {

                AgentDao agent = AgentCC.findById(agentId);

                if (agent == null)
                    throw new SrvException("Can not create job. Could not find agent with id: " + agentId);

                synchronized (lock ? UserCC.class : new Object()) {

                    UserDao user = UserCC.findByUsername(username);

                    if (user == null)
                        throw new UserCredentialsException("Can not create job. Could not find user with username: " + username);

                    if (user.getAdminId() == 0)
                        throw new UserApprovalException("Can not create job for user. An administrator needs to accept user: " + username);

                    if (!user.getPassword().equals(password))
                        throw new UserCredentialsException("Can not create job for user. Incorrect password for user with username: " + username);

                    synchronized (lock ? AdminCC.class : new Object()) {

                        AdminDao admin = AdminCC.findById(user.getAdminId());

                        if (admin == null)
                            throw new SrvException("Can not create job. Could not find admin with id: " + user.getAdminId());

                        synchronized (lock ? JobCC.class : new Object()) {

                            long jobId = JobCC.create(agentId, user.getAdminId(), timeAssigned, params, periodic, period);

                            if (jobId == 0)
                                throw new SrvException("Could not create job for agent with id: " + agentId);

                            return JobCC.findById(jobId);
                        }
                    }
                }
            }

        } catch (DaoException e) {
            // e.printStackTrace();
            throw new SrvException("Data access error while creating job for agent with id: " + agentId);
        }
    }

    /**
     * Retrieves the job with specified id.
     * 
     * @param jobId the job's id.
     * @return the job found or null if a job with specified id was not found.
     * @throws SrvException if a data access error occurs.
     */
    public static JobDao findById(long jobId) throws SrvException {

        try {

            synchronized (lock ? JobCC.class : new Object()) {

                return JobCC.findById(jobId);
            }

        } catch (DaoException e) {
            // e.printStackTrace();
            throw new SrvException("Data access error while finding job by id: " + jobId);
        }
    }

    /**
     * Retrieves all jobs assigned to a specific agent.
     * 
     * @param agentId the agent's id.
     * @return a set of objects, each representing a job.
     * @throws SrvException if an agent with specified id does not exist, or a data access error occurs.
     */
    public static Set<JobDao> findAllWithAgentId(long agentId) throws SrvException {

        try {

            synchronized (lock ? AgentCC.class : new Object()) {

                if (AgentCC.findById(agentId) == null)
                    throw new SrvException("Can not find jobs. Agent does not exist with id: " + agentId);

                synchronized (lock ? JobCC.class : new Object()) {

                    return JobCC.findAllWithAgentId(agentId);
                }
            }

        } catch (DaoException e) {
            // e.printStackTrace();
            throw new SrvException("Data access error while finding jobs for agent with id: " + agentId);
        }
    }

    /**
     * Retrieves all sent or not sent jobs that have been assigned to a specific agent.
     * 
     * @param agentId the agent's id.
     * @param sent the jobs' status, sent or not sent.
     * @return a set of objects, each representing a job.
     * @throws SrvException if an agent with specified id does not exist, or a data access error occurs.
     */
    public static Set<JobDao> findAllWithAgentId(long agentId, boolean sent) throws SrvException {

        try {

            synchronized (lock ? AgentCC.class : new Object()) {

                if (AgentCC.findById(agentId) == null)
                    throw new SrvException("Can not find jobs. Agent does not exist with id: " + agentId);

                synchronized (lock ? JobCC.class : new Object()) {

                    return JobCC.findAllWithAgentId(agentId, sent);
                }
            }

        } catch (DaoException e) {
            // e.printStackTrace();
            throw new SrvException("Data access error while finding jobs " + (sent ? "sent" : "not sent") + " for agent with id: " + agentId);
        }
    }

    /**
     * Retrieves all jobs that have been created by a specific admin.
     * 
     * @param adminId the admin's id.
     * @return a set of objects, each representing a job.
     * @throws SrvException if an admin with specified id does not exist, or a data access error occurs.
     */
    public static Set<JobDao> findAllWithAdminId(long adminId) throws SrvException {

        try {

            synchronized (lock ? AdminCC.class : new Object()) {

                if (AdminCC.findById(adminId) == null)
                    throw new SrvException("Can not find jobs. Admin does not exist with id: " + adminId);

                synchronized (lock ? JobCC.class : new Object()) {

                    return JobCC.findAllWithAdminId(adminId);
                }
            }

        } catch (DaoException e) {
            // e.printStackTrace();
            throw new SrvException("Data access error while finding jobs for admin with id: " + adminId);
        }
    }

    /**
     * Sets a job's sent time to the current time.
     * 
     * @param jobId the job's id
     * @throws SrvException if a job with specified id does not exist, the job has already been sent, its sent time could not be set, or a data access error occurs.
     */
    public static void send(long jobId) throws SrvException {

        try {

            synchronized (lock ? JobCC.class : new Object()) {

                JobDao j = JobCC.findById(jobId);

                if (j == null)
                    throw new SrvException("Can not send job. Could not find job with id: " + jobId);

                if (j.getSent())
                    throw new SrvException("Can not send job. Job with id " + jobId + " has already been sent.");

                JobCC.setTimeSent(jobId, Date.from(Instant.now()));
            }

        } catch (DaoException e) {
            // e.printStackTrace();
            throw new SrvException("Data access error while setting sent to true for job with id: " + jobId);
        }
    }

    /**
     * Sets a job's stop time to the current time.
     * 
     * @param jobId the job's id
     * @throws SrvException if a job with specified id does not exist, the job is not periodic or has not been sent, its stop time could not be set, or a data access error occurs.
     */
    public static void stopPeriodic(long jobId) throws SrvException {

        try {

            synchronized (lock ? JobCC.class : new Object()) {

                JobDao j = JobCC.findById(jobId);

                if (j == null)
                    throw new SrvException("Can not stop job. Could not find job with id: " + jobId);

                if (!j.getPeriodic())
                    throw new SrvException("Can not stop job. Job with id " + jobId + " is not periodic.");

                if (!j.getSent())
                    throw new SrvException("Can not stop job. Job with id " + jobId + " has not been sent.");

                JobCC.setTimeStopped(jobId, Date.from(Instant.now()));
            }

        } catch (DaoException e) {
            // e.printStackTrace();
            throw new SrvException("Data access error while setting sent to true for job with id: " + jobId);
        }
    }
}
