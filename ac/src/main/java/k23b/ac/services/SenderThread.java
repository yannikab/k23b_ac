package k23b.ac.services;

import java.util.Set;

import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.xml.SimpleXmlHttpMessageConverter;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import android.net.NetworkInfo;
import android.net.NetworkInfo.State;
import android.util.Log;
import k23b.ac.db.dao.JobDao;
import k23b.ac.db.dao.UserDao;
import k23b.ac.db.srv.JobSrv;
import k23b.ac.db.srv.SrvException;
import k23b.ac.db.srv.UserSrv;
import k23b.ac.rest.Job;
import k23b.ac.rest.User;
import k23b.ac.rest.UserContainer;
import k23b.ac.services.observer.Observable;
import k23b.ac.services.observer.Observer;
import k23b.ac.tasks.status.UsersSendStatus;
import k23b.ac.util.JobFactory;
import k23b.ac.util.Settings;

/**
 * A thread implementing the functionality of sending the stored Users with their assigned Jobs to the AM
 * given an available network connection. If such a connection is unavailable the Thread waits until there
 * is one available.
 *
 */
public class SenderThread extends Thread implements Observer<State> {

    private int interval;
    private Object monitor;
    private static UserContainer outgoingUserContainer;

    @Override
    public void run() {

        Log.d(SenderThread.class.getName(), "Sender Thread running");

        while (!isInterrupted()) {

            try {

                synchronized (monitor) {

                    while (!NetworkManager.isNetworkAvailable()) {

                        Log.d(SenderThread.class.getName(), "Network Unavailable: SenderThread waiting");

                        monitor.wait();
                    }
                }

                Log.d(SenderThread.class.getName(), "Network Available!");

                sendUserContainer();

                Thread.sleep(interval * 1000);

            } catch (InterruptedException e) {

                Log.d(SenderThread.class.getName(), "Interrupted while Sleeping.");

                interrupt();
            }
        }
        // make one last check on the DB and try to send any potential
        // UserContainer
        if (NetworkManager.isNetworkAvailable())
            sendUserContainer();

        Log.d(SenderThread.class.getName(), "Finished.");

    }

    @Override
    public void update(Observable<NetworkInfo.State> observable, NetworkInfo.State data) {

        synchronized (monitor) {

            if (data == NetworkInfo.State.CONNECTED) {

                monitor.notify();
                Log.d(SenderThread.class.getName(), "Unblocking the Thread");
            }
        }

    }

    public SenderThread(int interval) {
        super();

        outgoingUserContainer = null;
        monitor = new Object();

        this.interval = interval;

        this.setName("Sender");
    }

    /**
     * The attempt of sending the UserContainer to the AM. Depending on the result of the HTTP call a relevant action will be taken.
     */
    private void sendUserContainer() {

        outgoingUserContainer = new UserContainer();

        try {
            Set<UserDao> userSet = UserSrv.findAll();

            for (UserDao ud : userSet) {

                User user = new User(ud.getUsername(), ud.getPassword());
                Set<JobDao> jobSet = JobSrv.findAllJobsFromUsername(ud.getUsername());

                outgoingUserContainer.getUsers().add(user);

                for (JobDao jd : jobSet)
                    user.getJobs().add(JobFactory.fromDao(jd));

            }

            if (!outgoingUserContainer.getUsers().isEmpty()) {

                Log.d(SenderThread.class.getName(), outgoingUserContainer.getUsers().size() + " Users to send to AM");
                for (User u : outgoingUserContainer.getUsers())
                    Log.d(SenderThread.class.getName(),
                            u.getJobs().size() + " Jobs from User: " + u.getUsername() + " to send to AM");

                UsersSendStatus status = usersSend(Settings.getBaseURI(), outgoingUserContainer);

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
            } else
                outgoingUserContainer = null;

        } catch (SrvException e) {
            Log.e(SenderThread.class.getName(), e.getMessage());
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
    
    public void sendSuccess() {

        Log.d(SenderThread.class.getName(), "Users were sent successfully!");
        synchronized (SenderThread.class) {

            for (User user : outgoingUserContainer.getUsers()) {

                for (Job job : user.getJobs()) {

                    try {

                        JobSrv.delete(job.getJobId());

                    } catch (SrvException e) {
                        Log.e(SenderThread.class.getName(), e.getMessage());
                    }
                }
                try {

                    UserSrv.tryDelete(user.getUsername());

                } catch (SrvException e) {
                    Log.e(SenderThread.class.getName(), e.getMessage());
                }
            }

            outgoingUserContainer = null;
        }
    }

    public void serviceError() {

        Log.e(SenderThread.class.getName(), "Service Error, User Container was not Sent");
        synchronized (SenderThread.class) {

            for (User user : outgoingUserContainer.getUsers()) {

                for (Job job : user.getJobs()) {

                    try {

                        JobSrv.delete(job.getJobId());

                    } catch (SrvException e) {
                        Log.e(SenderThread.class.getName(), e.getMessage());
                    }
                }
                try {

                    UserSrv.tryDelete(user.getUsername());

                } catch (SrvException e) {
                    Log.e(SenderThread.class.getName(), e.getMessage());
                }
            }

            outgoingUserContainer = null;
        }
        Log.d(SenderThread.class.getName(), "User Container Discarded");

    }

    public void networkError() {

        Log.e(SenderThread.class.getName(), "Network Error, User Container was not Sent");
        outgoingUserContainer = null;
    }

    public void cancelled() {

        Log.e(SenderThread.class.getName(), "UsersSendTask Cancelled, User Container was not Sent");
        outgoingUserContainer = null;
    }
}
