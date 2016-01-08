package k23b.sa.AgentState;

import javax.ws.rs.ProcessingException;
import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.log4j.Logger;

import k23b.sa.AgentStats;
import k23b.sa.RequestStatus;
import k23b.sa.Threads.MainThread;

/**
 * The State in which the SA checks the Request Status of the Request made to the AM.
 */
public class CheckState extends AgentState {

    private static final Logger log = Logger.getLogger(CheckState.class);

    public CheckState(MainThread mainThread) {
        super(mainThread);
    }

    @Override
    public void handleState() {

        while (!Thread.currentThread().isInterrupted()) {

            try {

                Thread.sleep(getMainThread().getSettings().getRegistrationCheckInterval() * 1000);

            } catch (InterruptedException e) {
                // e.printStackTrace();
                Thread.currentThread().interrupt();
            }
            
            RequestStatus rs = checkRequest();

            String message = "Status received: " + rs.toString();
            log.info(message);
            System.out.println(message);

            switch (rs) {

            case PENDING:
                continue;

            case ACCEPTED:
                setCurrentState(new JobsState(getMainThread()));
                return;

            case REJECTED:
                setCurrentState(new ShutdownState(getMainThread()));
                return;

            case INVALID:
            case UNKNOWN:
                setCurrentState(new RegisterState(getMainThread()));
                return;
            }
        }

        setCurrentState(new ShutdownState(getMainThread()));
    }

    /**
     * Checks the Request Status of the made Request using RESTful communication.
     * 
     * @return The Request Status of this SA.
     */
    private RequestStatus checkRequest() {

        // return RequestStatus.INVALID;
        
        String message = "Checking Aggregator Manager for registration.";
        log.info(message);
        System.out.println(message);

        String baseURI = getMainThread().getSettings().getBaseURI();

        AgentStats agentStats = getMainThread().getAgentStats();

        Builder builder = getMainThread()
                .getClient()
                .target(baseURI + "handle/request/check/" + agentStats.getHash() + "/")
                .request(MediaType.TEXT_PLAIN);
        
        try {

            Response response = builder.get();

            if (response.getStatus() != 200) {
                log.error("Registration check failed. HTTP error code: " + response.getStatus());
                return RequestStatus.INVALID;
            }

            String checkResponse = response.readEntity(String.class);

            try {

                return RequestStatus.valueOf(checkResponse.toUpperCase());

            } catch (IllegalArgumentException e) {
                // e.printStackTrace();

                message = "Invalid Request Status: " + checkResponse;
                log.error(message);
                System.out.println(message);

                return RequestStatus.INVALID;
            }

        } catch (ProcessingException e) {
            // e.printStackTrace();
            log.error(e.getMessage());
            System.out.println(e.getMessage());

            return RequestStatus.INVALID;
        }
    }
}
