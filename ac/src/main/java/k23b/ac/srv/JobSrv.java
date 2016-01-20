package k23b.ac.srv;

import java.util.Date;
import java.util.Set;

import k23b.ac.dao.DaoException;
import k23b.ac.dao.Job;
import k23b.ac.dao.JobDao;
import k23b.ac.dao.User;
import k23b.ac.dao.UserDao;

public class JobSrv {

	public static Job create(int agentId, String username, String params, boolean periodic, int period) throws SrvException {

		try {

			User user = UserDao.findUserbyUsername(username);

			if (user == null)
				throw new SrvException("Can not create job. Could not find User with username: " + username);

			if (!user.isActive())
				throw new SrvException("Can not create job. User with username " + username + " is not logged in.");

			Job job = JobDao.createJob(params, username, agentId, new Date(), periodic, period);

			if (job == null)
				throw new SrvException("Could not create Job for agent with id: " + agentId);

			return job;

		} catch (DaoException e) {
			// e.printStackTrace();
			throw new SrvException("Data access error while creating job for agent with id: " + agentId);
		}
	}
	
	public static Job findById(int jobId) throws SrvException{
		
		try{
			
			return JobDao.findJobById(jobId);
			
		}catch(DaoException e){
			throw new SrvException("Data access error while searching for Job with id: "+jobId);
		}
	}
	
	public static Set<Job> findAllWithAgentId(int agentId) {
		
			return JobDao.findAllJobsFromAgentId(agentId);
	}
	
	public static Set<Job> findAllFromUser(String username) throws SrvException{
		
		try {			
			if(UserDao.findUserbyUsername(username) == null)
				throw new SrvException("Cannot find Jobs. No such User with username: "+ username);
			
			return JobDao.findAllJobsFromUsername(username);
			
		} catch (DaoException e) {
			throw new SrvException("Data access error while finding Jobs from User with username: "+username);
		}
	}
	
	public static void send(int jobId) throws SrvException{
		
		try{
			
			Job job = JobDao.findJobById(jobId);
			
			if(job == null)
				throw new SrvException("Cannot send Job. No such Job with id: "+jobId);
			
			JobDao.deleteJob(jobId);
		}catch(DaoException e){
			throw new SrvException("Data access error while sending (deleting) Job with id: "+jobId);
		}
		
	}
	
	public static Set<Job> findAllJobsFromActiveUsers(){
		
		return JobDao.findAllJobsFromActiveUsers();
		
	}
	
	public static void setTimeAssigned(int jobId) throws SrvException{
		
		try{
			
			Job job = JobDao.findJobById(jobId);
			
			if(job == null)
				throw new SrvException("Cannot set Time Assigned. No such Job with id: "+jobId);
			
			JobDao.setTimeAssigned(jobId, new Date());
			
		}catch(DaoException e){
			throw new SrvException("Data access error while setting Time Assigned for Job with id: "+jobId);
		}
		
	}
	

}
