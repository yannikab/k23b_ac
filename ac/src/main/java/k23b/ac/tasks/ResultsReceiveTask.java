package k23b.ac.tasks;

import java.util.Collections;
import java.util.List;

import org.springframework.http.converter.xml.SimpleXmlHttpMessageConverter;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import android.os.AsyncTask;
import k23b.ac.rest.ResultContainer;
import k23b.ac.services.Logger;
import k23b.ac.rest.Result;
import k23b.ac.tasks.status.ReceiveStatus;

public class ResultsReceiveTask extends AsyncTask<Void, Void, ReceiveStatus> {

    public interface ResultsReceiveCallback {

        public void resultsReceived(List<Result> results);

        public void registrationPending();

        public void incorrectCredentials();

        public void serviceError();

        public void networkError();

        public void removeResultsTask();
    }

    private final ResultsReceiveCallback resultsReceiveCallback;
    private final String baseURI;
    private final String username;
    private final String password;
    private final int number;

    private List<Result> results;

    public ResultsReceiveTask(ResultsReceiveCallback resultsReceiveCallback, String baseURI, String username, String password, int number) {
        super();

        this.resultsReceiveCallback = resultsReceiveCallback;
        this.baseURI = baseURI;
        this.username = username;
        this.password = password;
        this.number = number;
    }

    @Override
    protected ReceiveStatus doInBackground(Void... params) {

        try {

            String url = baseURI + "results/all/" + username + "/" + password + "/" + number;

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

        resultsReceiveCallback.removeResultsTask();

        switch (status) {

        case RECEIVE_SUCCESS:
            resultsReceiveCallback.resultsReceived(results);
            break;

        case REGISTRATION_PENDING:
            resultsReceiveCallback.registrationPending();
            break;

        case INCORRECT_CREDENTIALS:
            resultsReceiveCallback.incorrectCredentials();
            break;

        case SERVICE_ERROR:
            resultsReceiveCallback.serviceError();
            break;

        case NETWORK_ERROR:
            resultsReceiveCallback.networkError();

        default:
            break;
        }
    }

    @Override
    protected void onCancelled() {

        resultsReceiveCallback.removeResultsTask();

        resultsReceiveCallback.resultsReceived(null);
    }
}
