package k23b.ac.services;

import java.util.LinkedList;
import java.util.List;

import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.xml.SimpleXmlHttpMessageConverter;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

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
import k23b.ac.tasks.status.UsersSendStatus;
import k23b.ac.util.JobDaoFactory;
import k23b.ac.util.Settings;
import k23b.ac.util.UserDaoFactory;

/**
 * A singleton class extending IntentService for the dispatching of a UserContainer containing Jobs of a User using Web Services or ,
 * in case of unavailable connection, temporarily saving it to the SQLite Database.
 */
public class JobDispatcher extends IntentService {

    private static JobDispatcher instance;
    private static UserContainer jobDispatcherCache;

    public JobDispatcher() {
        super("JobDispatcher");
    }
    
    /**
     * Returns the JobDispatcher instance. If this method is called for the first time, the instance of the JobDispatcher is initialized then.
     * @return The JobDispatcher instance.
     */
    public static JobDispatcher getInstance() {

        synchronized (JobDispatcher.class) {
            if (instance == null) {
                jobDispatcherCache = null;
                instance = new JobDispatcher();
            }
        }
        return instance;
    }
    
    /**
     * A method expressing the Intent for a Job dispatch.
     * 
     * @param context The context of the calling Activity/Fragment
     * @param userObject The User which has Jobs to be dispatched.
     */
    public void dispatch(Context context, User userObject) {

        Log.d(JobDispatcher.class.getName(), "Dispatch called from an Activity");

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
    
    /**
     * The dispatching of Jobs assigned by the User to the AM. If no active Network Connection is found the User and its assigned Jobs are stored in the Database
     *  to be dispatched when the connection is restored. It is invoked by the onHandleIntent(Intent jobIntent) method
     * 
     * @param user The User who assigned Jobs.
     */
    public void dispatchJob(User user) {

        Log.d(JobDispatcher.class.getName(), "DispatchJob Called");
        // check to see if there is a network connection
        if (!NetworkManager.isNetworkAvailable()) {

            Log.d(JobDispatcher.class.getName(), "No active Network Connection Found!");
            // if not insert the job in the DB
            instance.saveIntoDB(user);
            return;
        }
        Log.d(JobDispatcher.class.getName(), "Active Network Connection Found!");
        // else send the UserContainer to the UsersSendTask

        UserContainer uc = new UserContainer();
        uc.getUsers().add(user);

        // Store the new User request for Job Dispatch in the cache

        jobDispatcherCache = uc;

        // Send the UserContainer using RESTful Web Services
        UsersSendStatus status = usersSend(Settings.getBaseURI(), uc);

        switch (status) {

        case NETWORK_ERROR:
            networkError();
            break;

        case SERVICE_ERROR:
            serviceError();
            break;

        case SEND_SUCCESS:
            sendSuccess();
            break;

        default:
            cancelled();
            break;
        }
    }

    /**
     * The HTTP POST call for exposing the UserContainer to the AM
     * 
     * @param baseURI The base URI of the AC handlers.
     * @param userContainer The UserContainer to be send.
     * @return A Status concerning the result of the HTTP POST call.
     */
    UsersSendStatus usersSend(String baseURI, UserContainer userContainer) {

        try {
            String url = baseURI + "users/";

            RestTemplate restTemplate = new RestTemplate();

            restTemplate.getMessageConverters().add(new SimpleXmlHttpMessageConverter());

            restTemplate.getMessageConverters().add(new StringHttpMessageConverter());

            String response = restTemplate.postForObject(url, userContainer, String.class);

            if (response.startsWith("Accepted"))
                return UsersSendStatus.SEND_SUCCESS;
            else
                return UsersSendStatus.SERVICE_ERROR;

        } catch (RestClientException e) {
            Logger.logException(getClass().getSimpleName(), e);
            return UsersSendStatus.NETWORK_ERROR;
        }
    }

    /**
     * Storing the User with his/her Jobs in the Database
     * @param user The User who assigned Jobs.
     */
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

    public void sendSuccess() {

        Log.d(JobDispatcher.class.getName(), "Users were sent successfully!");
        synchronized (JobDispatcher.class) {

            if (jobDispatcherCache != null)
                jobDispatcherCache = null;
        }
    }

    public void serviceError() {

        Log.e(JobDispatcher.class.getName(), "Service Error, User Container was not Sent");
        synchronized (JobDispatcher.class) {

            if (jobDispatcherCache != null)
                jobDispatcherCache = null;

        }
        Log.e(JobDispatcher.class.getName(), "User Container Discarded");
    }

    public void networkError() {

        Log.e(JobDispatcher.class.getName(), "Network Error, User Container was not Sent");
        synchronized (JobDispatcher.class) {

            if (jobDispatcherCache != null) {

                for (User user : jobDispatcherCache.getUsers())
                    instance.saveIntoDB(user);

                jobDispatcherCache = null;
            }
        }
    }

    public void cancelled() {

        Log.e(JobDispatcher.class.getName(), "UsersSendTask Cancelled, User Container was not Sent");
        synchronized (JobDispatcher.class) {

            if (jobDispatcherCache != null) {

                for (User user : jobDispatcherCache.getUsers())
                    instance.saveIntoDB(user);

                jobDispatcherCache = null;
            }
        }
    }
}