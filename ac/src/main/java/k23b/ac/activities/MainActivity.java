package k23b.ac.activities;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;
import k23b.ac.R;
import k23b.ac.fragments.AgentsFragment;
import k23b.ac.fragments.JobsFragment;
import k23b.ac.fragments.NavigationDrawerFragment;
import k23b.ac.fragments.ResultsAgentFragment;
import k23b.ac.fragments.ResultsAllFragment;
import k23b.ac.services.Logger;
import k23b.ac.services.NetworkManager;
import k23b.ac.services.UserManager;

public class MainActivity extends Activity implements NavigationDrawerFragment.NavigationDrawerCallbacks {

    public static final int ASSIGN_JOB_REQUEST = 1;

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        Logger.info(this.toString(), "onCreate()");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mNavigationDrawerFragment = (NavigationDrawerFragment) getFragmentManager()
                .findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();

        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);

        registerReceiver(NetworkManager.getInstance(), intentFilter);

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(R.id.navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout));
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {

        FragmentManager fragmentManager = getFragmentManager();

        Fragment currentFragment = fragmentManager.findFragmentById(R.id.container);

        switch (position) {
        case 0:
            if (!(currentFragment instanceof AgentsFragment))
                fragmentManager.beginTransaction().replace(R.id.container, new AgentsFragment()).commit();
            break;
        case 1:
            if (!(currentFragment instanceof JobsFragment))
                fragmentManager.beginTransaction().replace(R.id.container, new JobsFragment()).commit();
            break;
        case 2:
            if (!(currentFragment instanceof ResultsAgentFragment))
                fragmentManager.beginTransaction().replace(R.id.container, new ResultsAgentFragment()).commit();
            break;
        case 3:
            if (!(currentFragment instanceof ResultsAllFragment))
                fragmentManager.beginTransaction().replace(R.id.container, new ResultsAllFragment()).commit();
            break;
        }
    }

    public void onSectionAttached(int number) {

        switch (number) {
        case 1:
            mTitle = getString(R.string.title_fragment_agents);
            break;
        case 2:
            mTitle = getString(R.string.title_fragment_jobs);
            break;
        case 3:
            mTitle = getString(R.string.title_fragment_results_agent);
            break;
        case 4:
            mTitle = getString(R.string.title_fragment_results_all);
            break;
        }
    }

    private void restoreActionBar() {
        ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            getMenuInflater().inflate(R.menu.main, menu);
            restoreActionBar();
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        switch (item.getItemId()) {

        case R.id.action_settings:

            return true;

        case R.id.action_logout:

            Logger.info(this.toString(), "Log out pressed, aborting activity.");

            UserManager.getInstance().clearUser();

            finish();

            return false;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {

        case ASSIGN_JOB_REQUEST:

            switch (resultCode) {
            case RESULT_OK:
                Toast.makeText(this, String.valueOf("Job assigned to Agent"), Toast.LENGTH_LONG).show();
                break;
            case RESULT_CANCELED:
            default:
                Toast.makeText(this, String.valueOf("Job assignment cancelled"), Toast.LENGTH_LONG).show();
                break;
            }
            break;
        default:
            break;
        }
    }

    @Override
    protected void onDestroy() {

        Logger.info(this.toString(), "onDestroy()");

        unregisterReceiver(NetworkManager.getInstance());

        super.onDestroy();

        // clearUser();
    }
}
