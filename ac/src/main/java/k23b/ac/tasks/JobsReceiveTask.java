package k23b.ac.tasks;

import java.util.Collections;
import java.util.List;

import org.springframework.http.converter.xml.SimpleXmlHttpMessageConverter;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import android.os.AsyncTask;
import k23b.ac.rest.Job;
import k23b.ac.rest.JobContainer;
import k23b.ac.tasks.status.ReceiveStatus;
import k23b.ac.util.Logger;

public class JobsReceiveTask extends AsyncTask<Void, Void, ReceiveStatus> {

    public interface JobsReceiveCallback {

        public void jobsReceived(List<Job> jobs);

        public void registrationPending();

        public void incorrectCredentials();

        public void serviceError();

        public void networkError();
    }

    private final JobsReceiveCallback jobsReceiveCallback;
    private final String baseURI;
    private final String username;
    private final String password;
    private final String agentHash;

    private List<Job> jobs;

    public JobsReceiveTask(JobsReceiveCallback jobsReceiveCallback, String baseURI, String username, String password,
            String agentHash) {
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

            // Thread.sleep(5000);

            String url = baseURI + "jobs/" + username + "/" + password + "/" + agentHash;

            RestTemplate restTemplate = new RestTemplate();

            restTemplate.getMessageConverters().add(new SimpleXmlHttpMessageConverter());

            JobContainer jobContainer = restTemplate.getForObject(url, JobContainer.class);

            Collections.sort(jobContainer.getJobs());

            String status = jobContainer.getStatus();

            if (status.startsWith("Accepted")) {

                this.jobs = jobContainer.getJobs();

                return ReceiveStatus.RECEIVE_SUCCESS;

            } else if (status.startsWith("Pending")) {
                return ReceiveStatus.REGISTRATION_PENDING;
            } else if (status.startsWith("Incorrect")) {
                return ReceiveStatus.INCORRECT_CREDENTIALS;
            } else {
                return ReceiveStatus.SERVICE_ERROR;
            }

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

        case REGISTRATION_PENDING:
            jobsReceiveCallback.registrationPending();
            break;

        case INCORRECT_CREDENTIALS:
            jobsReceiveCallback.incorrectCredentials();
            break;

        case SERVICE_ERROR:
            jobsReceiveCallback.serviceError();
            break;

        case NETWORK_ERROR:
            jobsReceiveCallback.networkError();

        default:
            break;
        }
    }

    @Override
    protected void onCancelled() {

        jobsReceiveCallback.jobsReceived(null);
    }
}
