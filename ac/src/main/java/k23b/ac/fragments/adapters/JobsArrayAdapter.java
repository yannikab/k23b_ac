package k23b.ac.fragments.adapters;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import k23b.ac.R;
import k23b.ac.rest.Job;
import k23b.ac.rest.status.JobStatus;

public class JobsArrayAdapter extends ArrayAdapter<Job> {

    public JobsArrayAdapter(Context context, List<Job> agents) {
        super(context, 0, agents);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        Job j = getItem(position);

        if (convertView == null)
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.row_jobs, parent, false);

        TextView textViewJobId = (TextView) convertView.findViewById(R.id.rowJobId);
        TextView textViewTimeAssigned = (TextView) convertView.findViewById(R.id.rowJobTimeAssigned);
        TextView textViewTimeSent = (TextView) convertView.findViewById(R.id.rowJobTimeSent);
        TextView textViewParameters = (TextView) convertView.findViewById(R.id.rowJobParameters);
        TextView textViewPeriodic = (TextView) convertView.findViewById(R.id.rowJobPeriodic);
        TextView textViewInterval = (TextView) convertView.findViewById(R.id.rowJobInterval);
        TextView textViewStatus = (TextView) convertView.findViewById(R.id.rowJobStatus);

        convertView.setBackgroundColor(
                getContext().getResources().getColor(position % 2 == 0 ? R.color.row_back : R.color.row_back_alt));

        textViewJobId.setText(String.valueOf(j.getJobId()));
        textViewTimeAssigned.setText(j.getFormattedTimeAssigned());
        textViewTimeSent.setText(j.getFormattedTimeSent());
        textViewParameters.setText(j.getParams());
        textViewPeriodic.setText(j.getPeriodic() ? "Yes" : "No");
        textViewInterval.setText(j.getPeriodic() ? String.valueOf(j.getPeriod()) : "-");
        JobStatus status = j.getStatus();

        textViewStatus.setText(j.getStatus().toString());

        textViewStatus.setBackgroundColor(
                getContext().getResources().getColor(status == JobStatus.ASSIGNED ? R.color.cell_job_assigned
                        : (status == JobStatus.SENT ? R.color.cell_job_sent : R.color.cell_job_stopped)));

        return convertView;
    }
}
