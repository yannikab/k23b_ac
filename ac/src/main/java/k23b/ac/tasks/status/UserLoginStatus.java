package k23b.ac.tasks.status;

/**
 * An enumeration containing possible results of HTTP GET in UserLoginTask
 *
 */
public enum UserLoginStatus {
    LOGIN_SUCCESS, INCORRECT_CREDENTIALS, REGISTRATION_PENDING, NETWORK_ERROR, SERVICE_ERROR, INVALID
}
