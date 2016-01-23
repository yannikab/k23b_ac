package k23b.ac.util;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import k23b.ac.db.dao.JobDao;
import k23b.ac.db.dao.UserDao;
import k23b.ac.db.srv.SrvException;
import k23b.ac.db.srv.UserSrv;
import k23b.ac.rest.Job;
import k23b.ac.rest.User;
import k23b.ac.rest.UserContainer;
import k23b.ac.tasks.UsersSendTask;

public class JobDispatcher extends IntentService implements UsersSendTask.UsersSendCallback {
	
	private static UsersSendTask usersSendTask;
	private static JobDispatcher instance;
	private static Context context;
	private static Queue<UserContainer> jobDispatcherCache; 

    
    public JobDispatcher(){
    	super("JobDispatcher");
    }

	public static JobDispatcher getInstance() {
		if(instance == null){
			jobDispatcherCache = new LinkedList<UserContainer>();
			usersSendTask = null;
			instance = new JobDispatcher();
		}
		return instance;
	}

	
	@Override
	protected void onHandleIntent(Intent jobIntent) {
		
		User userObject = (User) jobIntent.getParcelableExtra("userObject");
		dispatchJob(userObject);
		
	}
	
	public void dispatch(Context context, User userObject){
		
		Intent sendJobIntent = new Intent(context , JobDispatcher.class);
		sendJobIntent.putExtra("userObject", (User) userObject);
		
		context.startService(sendJobIntent);
	}
	
    public static void dispatchJob(User user) {
    	
    	//check to see if there is a network connection
    	if(!isOnline()){
    		//if not insert the job in the DB
    		try {
    			
    			UserDao ud = UserDaoFactory.fromUser(user);
    			List<JobDao> jdList = new LinkedList<JobDao>();
    			for (Job j : user.getJobs())
    				jdList.add(JobDaoFactory.fromJob(j));
    			
    			UserDao createdUd = UserSrv.createUserWithJobs(ud, jdList);
    			Log.d(JobDispatcher.class.getName(), "Created User: "+createdUd.getUsername()+" with "+jdList.size()+" Jobs");
    			
    	    	
    		} catch (SrvException e) {
    			Log.e(JobDispatcher.class.getName(), e.getMessage());
    		}
    	}
		//else send the UserContainer to the UsersSendTask
    	
    	UserContainer uc = new UserContainer();
    	uc.getUsers().add(user);
    	
    	//Store the new User request for Job Dispatch in the cache
    	jobDispatcherCache.add(uc);
    	
    	//Execute the Asynchronous Task
    	usersSendTask = new UsersSendTask(getInstance(),Settings.getBaseURI(), uc);
    	usersSendTask.execute();
    	
    }
    	
	public static void setContext(Context context) {
		JobDispatcher.context = context;
	}
	
	public static boolean isOnline() {
	    ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
	    NetworkInfo networkInfo = cm.getActiveNetworkInfo();
	    return (networkInfo != null && networkInfo.isConnected());
	}
	
	@Override
	public void sendSuccess() {
		
		usersSendTask = null;
		
		if(!jobDispatcherCache.isEmpty())
			jobDispatcherCache.remove();		
	}

	@Override
	public void serviceError() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void networkError() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void cancelled() {
		// TODO Auto-generated method stub
		
	}  
    
}