package k23b.ac.tasks;

import java.util.Collections;
import java.util.List;

import org.springframework.http.converter.xml.SimpleXmlHttpMessageConverter;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import android.os.AsyncTask;
import k23b.ac.Logger;
import k23b.ac.rest.Agent;
import k23b.ac.rest.AgentContainer;

public class AgentsReceiveTask extends AsyncTask<Void, Void, ReceiveStatus> {

    public interface AgentsCallback {

        public void agentsReceived(List<Agent> agents);

        public void registrationPending();

        public void incorrectCredentials();

        public void serviceError();

        public void networkError();
    }

    private final AgentsCallback agentsCallback;
    private final String baseURI;
    private final String username;
    private final String password;

    private List<Agent> agents;

    public AgentsReceiveTask(AgentsCallback agentsCallback, String baseURI, String username, String password) {
        super();

        this.agentsCallback = agentsCallback;
        this.baseURI = baseURI;
        this.username = username;
        this.password = password;
    }

    @Override
    protected ReceiveStatus doInBackground(Void... params) {

        try {

            // Thread.sleep(5000);

            String url = baseURI + "agents/" + username + "/" + password;

            RestTemplate restTemplate = new RestTemplate();

            restTemplate.getMessageConverters().add(new SimpleXmlHttpMessageConverter());

            AgentContainer agentContainer = restTemplate.getForObject(url, AgentContainer.class);

            Collections.sort(agentContainer.getAgents());

            String status = agentContainer.getStatus();

            if (status.startsWith("Accepted")) {

                this.agents = agentContainer.getAgents();

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

        switch (status) {

        case RECEIVE_SUCCESS:
            agentsCallback.agentsReceived(agents);
            break;

        case REGISTRATION_PENDING:
            agentsCallback.registrationPending();
            break;

        case INCORRECT_CREDENTIALS:
            agentsCallback.incorrectCredentials();
            break;

        case SERVICE_ERROR:
            agentsCallback.serviceError();
            break;

        case NETWORK_ERROR:
            agentsCallback.networkError();

        default:
            break;
        }
    }

    @Override
    protected void onCancelled() {

        agentsCallback.agentsReceived(null);
    }
}
