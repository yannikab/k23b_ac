package k23b.ac.fragments;

import android.app.Fragment;
import android.widget.Toast;
import k23b.ac.R;
import k23b.ac.services.Logger;
import k23b.ac.services.UserManager;
import k23b.ac.util.Settings;

/**
 * Parent class for the Agents, Jobs, Login, Register, ResultsAgent, ResultsAll Fragments. 
 */
public class FragmentBase extends Fragment {

    public void incorrectCredentials() {

        Logger.info(this.toString(), "Incorrect credentials, aborting activity.");

        abortActivity();
    }

    public void registrationPending() {

        Logger.info(this.toString(), "Registration pending, aborting activity.");

        abortActivity();
    }

    public void sessionExpired() {

        Logger.info(this.toString(), "Session expired, aborting activity.");

        if (Settings.getLogOutOnSessionExpiry())
            UserManager.getInstance().clearUser();

        abortActivity();
    }

    protected void abortActivity() {

        if (getActivity() == null)
            return;

        getActivity().finish();
    }

    public void networkError() {

        Logger.info(this.toString(), "Network error.");

        if (getActivity() == null)
            return;

        Toast.makeText(getActivity(), getString(R.string.error_network_error), Toast.LENGTH_SHORT).show();
    }

    public void serviceError() {

        Logger.info(this.toString(), "Service error.");

        if (getActivity() == null)
            return;

        Toast.makeText(getActivity(), getString(R.string.error_service_error), Toast.LENGTH_SHORT).show();
    }
}
