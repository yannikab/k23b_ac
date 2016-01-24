package k23b.ac.util;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import k23b.ac.db.dao.JobDao;
import k23b.ac.db.dao.UserDao;
import k23b.ac.db.srv.SrvException;
import k23b.ac.db.srv.UserSrv;
import k23b.ac.rest.Job;
import k23b.ac.rest.User;
import k23b.ac.rest.UserContainer;
import k23b.ac.tasks.UsersSendTask;
import k23b.ac.util.BlockingQueue.IBlockingQueue;
import k23b.ac.util.BlockingQueue.WaitNotifyQueue;

public class JobDispatcher extends IntentService implements UsersSendTask.UsersSendCallback {

	private static UsersSendTask usersSendTask;
	private static JobDispatcher instance;
	private static Context context;
	private static IBlockingQueue<UserContainer> jobDispatcherCache;

	public JobDispatcher() {
		super("JobDispatcher");
	}

	public static JobDispatcher getInstance() {

		synchronized (JobDispatcher.class) {
			if (instance == null) {
				jobDispatcherCache = new WaitNotifyQueue<UserContainer>("jobDispatcherCache");
				usersSendTask = null;
				instance = new JobDispatcher();
			}
		}
		return instance;
	}

	public void dispatch(Context context, User userObject) {

		Log.d(JobDispatcher.class.getName(), "Dispatch called from an Activity");

		JobDispatcher.setContext(context);

		Intent sendJobIntent = new Intent(context, JobDispatcher.class);
		sendJobIntent.putExtra("userObject", (User) userObject);

		context.startService(sendJobIntent);
	}

	@Override
	protected void onHandleIntent(Intent jobIntent) {

		Log.d(JobDispatcher.class.getName(), "Got the Intent");

		User userObject = (User) jobIntent.getParcelableExtra("userObject");
		dispatchJob(userObject);

	}

	public static void dispatchJob(User user) {

		Log.d(JobDispatcher.class.getName(), "DispatchJob Called");
		// check to see if there is a network connection
		if (!NetworkManager.networkAvailable(context)) {

			Log.d(JobDispatcher.class.getName(), "No active Network Connection Found!");
			// if not insert the job in the DB
			instance.saveIntoDB(user);
		}
		Log.d(JobDispatcher.class.getName(), "Active Network Connection Found!");
		// else send the UserContainer to the UsersSendTask

		UserContainer uc = new UserContainer();
		uc.getUsers().add(user);

		// Store the new User request for Job Dispatch in the cache
		try {
			jobDispatcherCache.put(uc);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// Execute the Asynchronous Task
		usersSendTask = new UsersSendTask(getInstance(), Settings.getBaseURI(), uc);
		usersSendTask.execute();

	}

	public static void setContext(Context context) {
		JobDispatcher.context = context;
	}

	public void saveIntoDB(User user) {

		try {

			UserDao ud = UserDaoFactory.fromUser(user);
			List<JobDao> jdList = new LinkedList<JobDao>();
			for (Job j : user.getJobs())
				jdList.add(JobDaoFactory.fromJob(j));

			UserDao createdUd = UserSrv.createUserWithJobs(ud, jdList);
			Log.d(JobDispatcher.class.getName(),
					"Created User: " + createdUd.getUsername() + " with " + jdList.size() + " Jobs");

		} catch (SrvException e) {
			Log.e(JobDispatcher.class.getName(), e.getMessage());
		}

	}

	@Override
	public void sendSuccess() {

		Log.d(JobDispatcher.class.getName(), "Users were sent successfully!");
		synchronized (JobDispatcher.class) {

			if (usersSendTask == null)
				return;

			if (jobDispatcherCache.size() != 0)
				try {
					jobDispatcherCache.get();
					
				} catch (InterruptedException e) {
					Log.e(JobDispatcher.class.getName(), "Interrupted on Get");
				}

			usersSendTask = null;
		}
	}

	@Override
	public void serviceError() {

		Log.e(JobDispatcher.class.getName(), "Service Error, User Container was not Sent");
		synchronized (JobDispatcher.class) {

			if (usersSendTask == null)
				return;

			while (jobDispatcherCache.size() != 0) {

				try {
					UserContainer uc = jobDispatcherCache.get();

					for (User user : uc.getUsers())
						instance.saveIntoDB(user);

				} catch (InterruptedException e) {
					Log.e(JobDispatcher.class.getName(), "Interrupted on Get");
				}

			}

			usersSendTask = null;
		}
	}

	@Override
	public void networkError() {

		Log.e(JobDispatcher.class.getName(), "Network Error, User Container was not Sent");
		synchronized (JobDispatcher.class) {

			if (usersSendTask == null)
				return;

			while (jobDispatcherCache.size() != 0) {

				try {
					UserContainer uc = jobDispatcherCache.get();

					for (User user : uc.getUsers())
						instance.saveIntoDB(user);
					
				} catch (InterruptedException e) {
					Log.e(JobDispatcher.class.getName(), "Interrupted on Get");
				}

			}

			usersSendTask = null;
		}
	}

	@Override
	public void cancelled() {

		Log.e(JobDispatcher.class.getName(), "UsersSendTask Cancelled, User Container was not Sent");
		synchronized (JobDispatcher.class) {

			if (usersSendTask == null)
				return;

			while (jobDispatcherCache.size() != 0) {

				UserContainer uc;
				try {
					uc = jobDispatcherCache.get();

					for (User user : uc.getUsers())
						instance.saveIntoDB(user);

				} catch (InterruptedException e) {
					Log.e(JobDispatcher.class.getName(), "Interrupted on Get");
				}

			}

			usersSendTask = null;
		}

	}

}