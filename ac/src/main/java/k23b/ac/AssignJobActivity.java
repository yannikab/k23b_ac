package k23b.ac;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RadioButton;
import android.widget.TextView;

public class AssignJobActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assign_job);

        Intent intent = getIntent();

        long agentId = intent.getLongExtra("agentId", -1);

        String requestHash = intent.getStringExtra("requestHash");

        ((TextView) findViewById(R.id.assign_job_hash)).setText(requestHash);

        ((RadioButton) findViewById(R.id.assign_job_radio_periodic_no)).setChecked(true);
        ((RadioButton) findViewById(R.id.assign_job_radio_periodic_yes)).setChecked(false);

        ((TextView) findViewById(R.id.assign_job_period)).setEnabled(false);
        ((TextView) findViewById(R.id.assign_job_period)).setText("300");
    }

    public void onOkPressed(View v) {

        // TODO: verify data, assign job

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
