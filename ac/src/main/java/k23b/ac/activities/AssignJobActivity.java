package k23b.ac.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import k23b.ac.R;
import k23b.ac.rest.Job;
import k23b.ac.rest.User;
import k23b.ac.util.JobDispatcher;
import k23b.ac.util.Logger;
import k23b.ac.util.UserManager;

public class AssignJobActivity extends Activity {

    long agentId;

    EditText editTextParams;

    RadioButton radioPeriodicNo;
    RadioButton radioPeriodicYes;

    EditText editTextPeriod;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        Logger.info(this.toString(), "onCreate()");

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_assign_job);

        Intent intent = getIntent();

        agentId = intent.getLongExtra("agentId", -1);

        String requestHash = intent.getStringExtra("requestHash");

        ((TextView) findViewById(R.id.assign_job_hash)).setText(requestHash);

        editTextParams = (EditText) findViewById(R.id.assign_job_params);

        radioPeriodicNo = (RadioButton) findViewById(R.id.assign_job_radio_periodic_no);
        radioPeriodicYes = (RadioButton) findViewById(R.id.assign_job_radio_periodic_yes);

        editTextPeriod = (EditText) findViewById(R.id.assign_job_period);

        radioPeriodicNo.setChecked(true);
        radioPeriodicYes.setChecked(false);

        editTextPeriod.setEnabled(false);
        editTextPeriod.setText("300");
    }

    @Override
    protected void onDestroy() {

        Logger.info(this.toString(), "onDestroy()");

        super.onDestroy();
    }

    public void onOkPressed(View v) {

        User u = UserManager.getInstance().getStoredUser();

        if (u == null) {

            Logger.info(this.toString(), "No user logged in, aborting activity.");
            finish();
            return;
        }

        Job j = new Job();

        j.setAgentId(agentId);
        j.setParams(editTextParams.getText().toString());
        j.setPeriodic(radioPeriodicYes.isChecked());
        j.setPeriod(j.getPeriodic() ? Integer.valueOf(editTextPeriod.getText().toString()) : 0);

        u.getJobs().add(j);

        JobDispatcher.getInstance().dispatch(this, u);

        Intent data = new Intent();
        setResult(RESULT_OK, data);
        finish();
    }

    public void onCancelPressed(View v) {

        setResult(RESULT_CANCELED);
        finish();
    }

    public void onRadioButtonClicked(View v) {

        switch (v.getId()) {
        case R.id.assign_job_radio_periodic_no:
            ((TextView) findViewById(R.id.assign_job_period)).setEnabled(false);
            return;
        case R.id.assign_job_radio_periodic_yes:
            ((TextView) findViewById(R.id.assign_job_period)).setEnabled(true);
            return;
        default:
            break;
        }
    }
}
