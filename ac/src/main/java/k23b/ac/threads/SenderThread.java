package k23b.ac.threads;

import java.util.Date;
import java.util.Set;

import android.content.Context;
import android.util.Log;
import k23b.ac.db.dao.JobDao;
import k23b.ac.db.dao.UserDao;
import k23b.ac.db.srv.JobSrv;
import k23b.ac.db.srv.SrvException;
import k23b.ac.db.srv.UserSrv;
import k23b.ac.rest.Job;
import k23b.ac.rest.User;
import k23b.ac.rest.UserContainer;
import k23b.ac.tasks.UsersSendTask;
import k23b.ac.util.NetworkManager;
import k23b.ac.util.Settings;

public class SenderThread extends Thread implements UsersSendTask.UsersSendCallback {

    private static UsersSendTask usersSendTask;
    private static UserContainer outgoingUserContainer;
    private Context context;

    @Override
    public void run() {

        Log.d(SenderThread.class.getName(), "Sender Thread running");

        while (!isInterrupted()) {

            if (NetworkManager.networkAvailable(context)) {

                Log.d(SenderThread.class.getName(), "Network Available!");

                sendUserContainer();
            }

            try {
                Thread.sleep(Settings.getSenderThreadInterval() * 1000);
            } catch (InterruptedException e) {

                Log.d(SenderThread.class.getName(), "Interrupted while Sleeping.");

                interrupt();
            }
        }
        // make one last check on the DB and try to send any potential
        // UserContainer
        sendUserContainer();
        Log.d(SenderThread.class.getName(), "Finished.");

    }

    public SenderThread(Context context) {
        super();

        this.context = context;
        usersSendTask = null;
        outgoingUserContainer = null;
    }

    private void sendUserContainer() {

        outgoingUserContainer = new UserContainer();

        try {
            Set<UserDao> userSet = UserSrv.findAll();

            for (UserDao ud : userSet) {

                User user = new User(ud.getUsername(), ud.getPassword());
                Set<JobDao> jobSet = JobSrv.findAllJobsFromUsername(ud.getUsername());

                outgoingUserContainer.getUsers().add(user);

                for (JobDao jd : jobSet) {

                    Job j = new Job();
                    j.setAgentId(jd.getAgentId());
                    j.setAdminId(-1);
                    j.setJobId(jd.getId());
                    j.setParams(jd.getParameters());
                    j.setTimeAssigned(jd.getTime_assigned());
                    j.setTimeSent(new Date());
                    j.setTimeStopped(new Date());
                    j.setPeriodic(jd.getPeriodic());
                    j.setPeriod(jd.getPeriod());

                    user.getJobs().add(j);
                }
            }

            if (usersSendTask != null)
                Log.e(SenderThread.class.getName(), "usersSendTask is NOT null , data will be lost");

            if (!outgoingUserContainer.getUsers().isEmpty()) {

                Log.d(SenderThread.class.getName(), outgoingUserContainer.getUsers().size() + " Users to send to AM");
                for (User u : outgoingUserContainer.getUsers())
                    Log.d(SenderThread.class.getName(),
                            u.getJobs().size() + " Jobs from User: " + u.getUsername() + " to send to AM");

                usersSendTask = new UsersSendTask(this, Settings.getBaseURI(), outgoingUserContainer);
                usersSendTask.execute();
            } else
                outgoingUserContainer = null;

        } catch (SrvException e) {
            Log.e(SenderThread.class.getName(), e.getMessage());
        }
    }

    @Override
    public void sendSuccess() {

        Log.d(SenderThread.class.getName(), "Users were sent successfully!");
        synchronized (SenderThread.class) {

            if (usersSendTask == null)
                return;

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

            usersSendTask = null;
            outgoingUserContainer = null;
        }

    }

    @Override
    public void serviceError() {

        if (usersSendTask == null)
            return;

        Log.e(SenderThread.class.getName(), "Service Error, User Container was not Sent");
        synchronized (SenderThread.class) {

            usersSendTask = null;
        }
    }

    @Override
    public void networkError() {

        if (usersSendTask == null)
            return;

        Log.e(SenderThread.class.getName(), "Network Error, User Container was not Sent");
        synchronized (SenderThread.class) {

            usersSendTask = null;
        }
    }

    @Override
    public void cancelled() {

        if (usersSendTask == null)
            return;

        Log.e(SenderThread.class.getName(), "UsersSendTask Cancelled, User Container was not Sent");
        synchronized (SenderThread.class) {

            usersSendTask = null;
        }

    }

}
