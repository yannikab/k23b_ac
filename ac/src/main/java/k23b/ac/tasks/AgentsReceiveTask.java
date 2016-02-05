package k23b.ac.tasks;

import java.util.Collections;
import java.util.List;

import org.springframework.http.converter.xml.SimpleXmlHttpMessageConverter;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import android.os.AsyncTask;
import k23b.ac.db.dao.CachedAgentStatus;
import k23b.ac.db.srv.CachedAgentSrv;
import k23b.ac.db.srv.SrvException;
import k23b.ac.rest.Agent;
import k23b.ac.rest.AgentContainer;
import k23b.ac.services.Logger;
import k23b.ac.tasks.status.ReceiveStatus;

public class AgentsReceiveTask extends AsyncTask<Void, Void, ReceiveStatus> {

    public interface AgentsReceiveCallback extends TaskCallback {

        public void agentsReceived(List<Agent> agents);

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

            String url = baseURI + "agents/" + username + "/" + password;

            RestTemplate restTemplate = new RestTemplate();

            restTemplate.getMessageConverters().add(new SimpleXmlHttpMessageConverter());

            AgentContainer agentContainer = restTemplate.getForObject(url, AgentContainer.class);

            // try {
            // Thread.sleep(5000);
            // } catch (InterruptedException e) {
            // // TODO Auto-generated catch block
            // e.printStackTrace();
            // }

            Collections.sort(agentContainer.getAgents());

            String status = agentContainer.getStatus();

            if (status.startsWith("Accepted")) {

                this.agents = agentContainer.getAgents();

                for (Agent a : this.agents) {

                    try {

                        CachedAgentSrv.createOrUpdate(a.getAgentId(), a.getRequestHash(), a.getTimeAccepted(), a.getTimeJobRequest(), a.getTimeTerminated(), CachedAgentStatus.values()[a.getAgentStatus().ordinal()]);

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

            agentsReceiveCallback.agentsReceived(agents);
            break;

        case INCORRECT_CREDENTIALS:

            agentsReceiveCallback.incorrectCredentials();
            break;

        case REGISTRATION_PENDING:

            agentsReceiveCallback.registrationPending();
            break;

        case SESSION_EXPIRED:

            agentsReceiveCallback.sessionExpired();
            break;

        case NETWORK_ERROR:

            agentsReceiveCallback.networkError();
            break;

        case SERVICE_ERROR:

            agentsReceiveCallback.serviceError();
            break;

        default:
            
            agentsReceiveCallback.serviceError();
            break;
        }

        agentsReceiveCallback.removeAgentsTask();
    }

    @Override
    protected void onCancelled() {

        agentsReceiveCallback.removeAgentsTask();
    }
}
