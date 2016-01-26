package k23b.ac.fragments.adapters;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import k23b.ac.R;
import k23b.ac.rest.Result;

public class ResultsArrayAdapter extends ArrayAdapter<Result> {

    public ResultsArrayAdapter(Context context, List<Result> results) {
        super(context, 0, results);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        Result r = getItem(position);

        if (convertView == null)
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.row_results, parent, false);

        TextView textViewResultId = (TextView) convertView.findViewById(R.id.rowResultId);
        TextView textViewJobId = (TextView) convertView.findViewById(R.id.rowResultJobId);
        TextView textViewTimeReceived = (TextView) convertView.findViewById(R.id.rowResultTimeReceived);

        convertView.setBackgroundColor(getContext().getResources().getColor(position % 2 == 0 ? R.color.row_back : R.color.row_back_alt));

        textViewResultId.setText(String.valueOf(r.getResultId()));
        textViewJobId.setText(String.valueOf(r.getJobId()));
        textViewTimeReceived.setText(r.getFormattedTimeReceived());

        return convertView;
    }
}
