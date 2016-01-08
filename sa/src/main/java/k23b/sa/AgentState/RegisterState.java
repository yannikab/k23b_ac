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
 * The State in which the SA tries to register with the AM.
 */
public class RegisterState extends AgentState {

    private static final Logger log = Logger.getLogger(RegisterState.class);

    private int connectionAttempts;

    public RegisterState(MainThread mainThread) {
        super(mainThread);

        connectionAttempts = getMainThread().getSettings().getConnectionAttempts();
    }

    @Override
    public void handleState() {

        for (int i = 0; i < connectionAttempts; i++) {

            String message = String.format("Registration attempt %d of %d", i + 1, connectionAttempts);
            log.info(message);
            System.out.println(message);

            RequestStatus rs = sendRequest();

            message = "Status received: " + rs.toString();
            log.info(message);
            System.out.println(message);

            switch (rs) {

            case PENDING:
                setCurrentState(new CheckState(getMainThread()));
                return;

            case ACCEPTED:
                setCurrentState(new JobsState(getMainThread()));
                return;

            case REJECTED:
                setCurrentState(new ShutdownState(getMainThread()));
                return;

            case UNKNOWN:
            case INVALID:
                try {

                    Thread.sleep(getMainThread().getSettings().getRegistrationRetryInterval() * 1000);

                } catch (InterruptedException e) {
                    // e.printStackTrace();
                    Thread.currentThread().interrupt();
                }
                continue;
            }
        }

        String message = "Shutting down after " + connectionAttempts + " connection attempts.";
        log.info(message);
        System.out.println(message);

        setCurrentState(new ShutdownState(getMainThread()));
    }

    /**
     * The SA makes a registration Request to the AM using RESTful communication.
     */

    private RequestStatus sendRequest() {

        // log.info("Sending request for registration to Aggregator Manager.");

        AgentStats saStats = getMainThread().getAgentStats();

        // Send request to AM
        StringBuilder sb = new StringBuilder();
        sb.append(getMainThread().getSettings().getBaseURI());
        sb.append("handle/request/send/");
        sb.append(saStats.getHash());
        sb.append("/");
        sb.append(saStats.getNmapVersion());
        sb.append("/");
        sb.append(saStats.getOsVersion());
        sb.append("/");
        sb.append(saStats.getInterfaceMac());
        sb.append("/");
        sb.append(saStats.getInterfaceIp());
        sb.append("/");
        sb.append(saStats.getDeviceName());

        Builder builder = getMainThread()
                .getClient()
                .target(sb.toString())
                .request(MediaType.TEXT_PLAIN);

        try {

            Response response = builder.get();

            if (response.getStatus() != 200) {

                log.error("Registration attempt failed. HTTP error code: " + response.getStatus());
                return RequestStatus.INVALID;
            }

            String requestResponse = response.readEntity(String.class);

            try {

                return RequestStatus.valueOf(requestResponse.toUpperCase());

            } catch (IllegalArgumentException e) {
                // e.printStackTrace();

                String message = "Invalid Request Status: " + requestResponse;
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
