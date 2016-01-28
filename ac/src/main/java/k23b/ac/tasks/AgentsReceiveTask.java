package k23b.ac.tasks;

import java.util.Collections;
import java.util.List;

import org.springframework.http.converter.xml.SimpleXmlHttpMessageConverter;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import android.os.AsyncTask;
import k23b.ac.rest.Agent;
import k23b.ac.rest.AgentContainer;
import k23b.ac.services.Logger;
import k23b.ac.tasks.status.ReceiveStatus;

public class AgentsReceiveTask extends AsyncTask<Void, Void, ReceiveStatus> {

    public interface AgentsReceiveCallback {

        public void agentsReceived(List<Agent> agents);

        public void registrationPending();

        public void incorrectCredentials();

        public void serviceError();

        public void networkError();

        public void removeAgentsTask();
    }

    private final AgentsReceiveCallback agentsReceiveCallback;
    private final String baseURI;
    private final String username;
    private final String password;

    private List<Agent> agents;

    public AgentsReceiveTask(AgentsReceiveCallback agentsReceiveCallback, String baseURI, String username, String password) {
        super();

        this.agentsReceiveCallback = agentsReceiveCallback;
        this.baseURI = baseURI;
        this.username = username;
        this.password = password;
    }

    @Override
    protected ReceiveStatus doInBackground(Void... params) {

        try {

            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

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

        agentsReceiveCallback.removeAgentsTask();

        switch (status) {

        case RECEIVE_SUCCESS:
            agentsReceiveCallback.agentsReceived(agents);
            break;

        case REGISTRATION_PENDING:
            agentsReceiveCallback.registrationPending();
            break;

        case INCORRECT_CREDENTIALS:
            agentsReceiveCallback.incorrectCredentials();
            break;

        case SERVICE_ERROR:
            agentsReceiveCallback.serviceError();
            break;

        case NETWORK_ERROR:
            agentsReceiveCallback.networkError();

        default:
            break;
        }
    }

    @Override
    protected void onCancelled() {

        agentsReceiveCallback.removeAgentsTask();

        agentsReceiveCallback.agentsReceived(null);
    }
}
