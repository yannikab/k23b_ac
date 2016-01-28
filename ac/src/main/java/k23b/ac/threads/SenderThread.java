package k23b.ac.threads;

import java.util.Set;

import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.xml.SimpleXmlHttpMessageConverter;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import android.util.Log;
import k23b.ac.db.dao.JobDao;
import k23b.ac.db.dao.UserDao;
import k23b.ac.db.srv.JobSrv;
import k23b.ac.db.srv.SrvException;
import k23b.ac.db.srv.UserSrv;
import k23b.ac.rest.Job;
import k23b.ac.rest.User;
import k23b.ac.rest.UserContainer;
import k23b.ac.tasks.status.UsersSendStatus;
import k23b.ac.util.JobFactory;
import k23b.ac.util.Logger;
import k23b.ac.util.NetworkManager;
import k23b.ac.util.Settings;
import k23b.ac.util.observerPattern.Observable;
import k23b.ac.util.observerPattern.Observer;

public class SenderThread extends Thread implements Observer {

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
    public void update(Observable observable, boolean data) {
        
        synchronized (monitor) {
            if (data) {
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
                case INVALID:
                    networkError();
                    break;

                case SERVICE_ERROR:
                    serviceError();
                    break;

                case SUCCESS:
                    sendSuccess();
                    break;

                case CANCELLED:
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

    UsersSendStatus usersSend(String baseURI, UserContainer userContainer) {

        try {
            String url = baseURI + "users/";

            RestTemplate restTemplate = new RestTemplate();

            restTemplate.getMessageConverters().add(new SimpleXmlHttpMessageConverter());

            restTemplate.getMessageConverters().add(new StringHttpMessageConverter());

            String response = restTemplate.postForObject(url, userContainer, String.class);

            if (response.startsWith("Accepted"))
                return UsersSendStatus.SUCCESS;
            else if (response.startsWith("Service Error"))
                return UsersSendStatus.SERVICE_ERROR;
            else
                return UsersSendStatus.INVALID;

        } catch (RestClientException e) {
            Logger.logException(getClass().getSimpleName(), e);
            return UsersSendStatus.NETWORK_ERROR;
        }
    }

    public void serviceError() {

        Log.e(SenderThread.class.getName(), "Service Error, User Container was not Sent");
        outgoingUserContainer = null;
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
