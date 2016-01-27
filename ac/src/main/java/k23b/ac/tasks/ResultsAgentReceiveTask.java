package k23b.ac.tasks;

import java.util.Collections;
import java.util.List;

import org.springframework.http.converter.xml.SimpleXmlHttpMessageConverter;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import android.os.AsyncTask;
import k23b.ac.rest.ResultContainer;
import k23b.ac.rest.Result;
import k23b.ac.tasks.status.ReceiveStatus;
import k23b.ac.util.Logger;

public class ResultsAgentReceiveTask extends AsyncTask<Void, Void, ReceiveStatus> {

    public interface ResultsAgentReceiveCallback {

        public void resultsReceived(List<Result> results);

        public void registrationPending();

        public void incorrectCredentials();

        public void serviceError();

        public void networkError();

        public void removeResultsTask();
    }

    private final ResultsAgentReceiveCallback resultsAgentReceiveCallback;
    private final String baseURI;
    private final String username;
    private final String password;
    private final String agentHash;
    private final int number;

    private List<Result> results;

    public ResultsAgentReceiveTask(ResultsAgentReceiveCallback resultsAgentReceiveCallback, String baseURI, String username, String password, String agentHash, int number) {
        super();

        this.resultsAgentReceiveCallback = resultsAgentReceiveCallback;
        this.baseURI = baseURI;
        this.username = username;
        this.password = password;
        this.agentHash = agentHash;
        this.number = number;
    }

    @Override
    protected ReceiveStatus doInBackground(Void... params) {

        try {

            String url = baseURI + String.format("results/agent/%s/%s/%s/%d", username, password, agentHash, number);

            RestTemplate restTemplate = new RestTemplate();

            restTemplate.getMessageConverters().add(new SimpleXmlHttpMessageConverter());

            ResultContainer resultContainer = restTemplate.getForObject(url, ResultContainer.class);

            Collections.sort(resultContainer.getResults());

            String status = resultContainer.getStatus();

            if (status.startsWith("Accepted")) {

                this.results = resultContainer.getResults();

                return ReceiveStatus.RECEIVE_SUCCESS;

            } else if (status.startsWith("Pending"))
                return ReceiveStatus.REGISTRATION_PENDING;
            else if (status.startsWith("Incorrect"))
                return ReceiveStatus.INCORRECT_CREDENTIALS;
            else
                return ReceiveStatus.SERVICE_ERROR;

        } catch (RestClientException e) {
            Logger.logException(getClass().getSimpleName(), e);
            return ReceiveStatus.NETWORK_ERROR;
        }
    }

    @Override
    protected void onPostExecute(final ReceiveStatus status) {

        resultsAgentReceiveCallback.removeResultsTask();

        switch (status) {

        case RECEIVE_SUCCESS:
            resultsAgentReceiveCallback.resultsReceived(results);
            break;

        case REGISTRATION_PENDING:
            resultsAgentReceiveCallback.registrationPending();
            break;

        case INCORRECT_CREDENTIALS:
            resultsAgentReceiveCallback.incorrectCredentials();
            break;

        case SERVICE_ERROR:
            resultsAgentReceiveCallback.serviceError();
            break;

        case NETWORK_ERROR:
            resultsAgentReceiveCallback.networkError();

        default:
            break;
        }
    }

    @Override
    protected void onCancelled() {

        resultsAgentReceiveCallback.removeResultsTask();

        resultsAgentReceiveCallback.resultsReceived(null);
    }
}
