package k23b.ac.tasks;

import java.util.Collections;
import java.util.List;

import org.springframework.http.converter.xml.SimpleXmlHttpMessageConverter;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import android.os.AsyncTask;
import k23b.ac.Logger;
import k23b.ac.rest.Job;
import k23b.ac.rest.JobContainer;

public class JobsReceiveTask extends AsyncTask<Void, Void, List<Job>> {

    public interface JobsReceiver {
        public void setJobs(List<Job> jobs);
    }

    private final JobsReceiver receiver;
    private final String baseURI;
    private final String username;
    private final String password;
    private final String agentHash;

    public JobsReceiveTask(JobsReceiver receiver, String baseURI, String username, String password, String agentHash) {
        super();
        this.receiver = receiver;
        this.baseURI = baseURI;
        this.username = username;
        this.password = password;
        this.agentHash = agentHash;
    }

    @Override
    protected List<Job> doInBackground(Void... params) {

        try {

            // Thread.sleep(5000);

            String url = baseURI + "jobs/" + username + "/" + password + "/" + agentHash;

            RestTemplate restTemplate = new RestTemplate();

            restTemplate.getMessageConverters().add(new SimpleXmlHttpMessageConverter());

            JobContainer jobContainer = restTemplate.getForObject(url, JobContainer.class);

            Collections.sort(jobContainer.getJobs());

            return jobContainer.getStatus().equals("Accepted") ? jobContainer.getJobs() : null;

        } catch (RestClientException e) {
            Logger.logException(getClass().getSimpleName(), e);
            return null;
        }
    }

    @Override
    protected void onPostExecute(final List<Job> jobs) {

        receiver.setJobs(jobs);
    }

    @Override
    protected void onCancelled() {

        receiver.setJobs(null);
    }
}
