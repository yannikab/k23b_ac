package k23b.ac.tasks;

public interface TaskCallback {

    public void incorrectCredentials();

    public void registrationPending();

    public void sessionExpired();

    public void networkError();

    public void serviceError();
}
