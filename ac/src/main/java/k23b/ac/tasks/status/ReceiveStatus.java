package k23b.ac.tasks.status;

/**
 * An enumeration containing possible results of HTTP GET in
 *  AgentsReceiveTask, JobsReceiveTask, ResultsAgentReceiveTask, ResultsAllReceiveTask
 * 
 */
public enum ReceiveStatus {
    RECEIVE_SUCCESS, INCORRECT_CREDENTIALS, REGISTRATION_PENDING, SESSION_EXPIRED, NETWORK_ERROR, SERVICE_ERROR, INVALID
}
