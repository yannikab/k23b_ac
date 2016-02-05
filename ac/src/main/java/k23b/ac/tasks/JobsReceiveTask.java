package k23b.ac.tasks;

import java.util.Collections;
import java.util.List;

import org.springframework.http.converter.xml.SimpleXmlHttpMessageConverter;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import android.os.AsyncTask;
import k23b.ac.db.dao.CachedJobStatus;
import k23b.ac.db.srv.CachedJobSrv;
import k23b.ac.db.srv.SrvException;
import k23b.ac.rest.Job;
import k23b.ac.rest.JobContainer;
import k23b.ac.services.Logger;
import k23b.ac.tasks.status.ReceiveStatus;

public class JobsReceiveTask extends AsyncTask<Void, Void, ReceiveStatus> {

    public interface JobsReceiveCallback extends TaskCallback {

        public void jobsReceived(List<Job> jobs);

        public void removeJobsTask();
    }

    private final JobsReceiveCallback jobsReceiveCallback;
    private final String baseURI;
    private final String username;
    private final String password;
    private final String agentHash;

    private List<Job> jobs;

    public JobsReceiveTask(JobsReceiveCallback jobsReceiveCallback, String baseURI, String username, String password, String agentHash) {
        super();

        this.jobsReceiveCallback = jobsReceiveCallback;
        this.baseURI = baseURI;
        this.username = username;
        this.password = password;
        this.agentHash = agentHash;
    }

    @Override
    protected ReceiveStatus doInBackground(Void... params) {

        try {

            String url = baseURI + "jobs/" + username + "/" + password + "/" + agentHash;

            RestTemplate restTemplate = new RestTemplate();

            restTemplate.getMessageConverters().add(new SimpleXmlHttpMessageConverter());

            JobContainer jobContainer = restTemplate.getForObject(url, JobContainer.class);

            // try {
            // Thread.sleep(5000);
            // } catch (InterruptedException e) {
            // // TODO Auto-generated catch block
            // e.printStackTrace();
            // }

            Collections.sort(jobContainer.getJobs());

            String status = jobContainer.getStatus();

            if (status.startsWith("Accepted")) {

                this.jobs = jobContainer.getJobs();

                for (Job j : this.jobs) {

                    try {

                        CachedJobSrv.createOrUpdate(j.getJobId(), j.getAgentId(), j.getTimeAssigned(), j.getTimeSent(), j.getParams(), j.getPeriodic(), j.getPeriod(), CachedJobStatus.values()[j.getStatus().ordinal()]);

                    } catch (SrvException e) {

                        Logger.error(this.toString(), e.getMessage());

                        continue;
                    }
                }

                return ReceiveStatus.RECEIVE_SUCCESS;
            }

            if (status.equals("Incorrect Credentials"))
                return ReceiveStatus.INCORRECT_CREDENTIALS;

            if (status.equals("Registration Pending"))
                return ReceiveStatus.REGISTRATION_PENDING;

            if (status.equals("Session Expired"))
                return ReceiveStatus.SESSION_EXPIRED;

            if (status.equals("Service Error"))
                return ReceiveStatus.SERVICE_ERROR;

            return ReceiveStatus.INVALID;

        } catch (RestClientException e) {

            Logger.logException(getClass().getSimpleName(), e);

            return ReceiveStatus.NETWORK_ERROR;
        }
    }

    @Override
    protected void onPostExecute(final ReceiveStatus status) {

        switch (status) {

        case RECEIVE_SUCCESS:

            jobsReceiveCallback.jobsReceived(jobs);
            break;

        case INCORRECT_CREDENTIALS:

            jobsReceiveCallback.incorrectCredentials();
            break;

        case REGISTRATION_PENDING:

            jobsReceiveCallback.registrationPending();
            break;

        case SESSION_EXPIRED:

            jobsReceiveCallback.sessionExpired();
            break;

        case NETWORK_ERROR:

            jobsReceiveCallback.networkError();
            break;

        case SERVICE_ERROR:

            jobsReceiveCallback.serviceError();
            break;

        default:
            
            jobsReceiveCallback.serviceError();
            break;
        }

        jobsReceiveCallback.removeJobsTask();
    }

    @Override
    protected void onCancelled() {

        jobsReceiveCallback.removeJobsTask();
    }
}
