package k23b.ac.rest;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

import android.annotation.SuppressLint;

/**
 * Serves the purpose of receiving the Job Result info from a SA.
 */

@Root(name = "result")
public class Result implements Comparable<Result> {

    @Element(required = false)
    protected long resultId;

    @Element(required = false)
    protected String agentHash;

    @Element(required = true)
    protected long jobId;

    @Element(required = false)
    protected Date timeReceived;

    @Element(required = true)
    protected String output;

    public Result() {
        super();
    }

    public Result(long jobId, String output) {

        this.jobId = jobId;
        this.output = output;
    }

    public long getResultId() {
        return this.resultId;
    }

    public String getAgentHash() {
        return agentHash;
    }

    public long getJobId() {
        return this.jobId;
    }

    public Date getTimeReceived() {
        return this.timeReceived;
    }

    public String getJobResult() {
        return output;
    }

    @SuppressLint("SimpleDateFormat")
    private static final DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

    public String getFormattedTimeReceived() {

        if (timeReceived == null)
            return "-";

        return dateFormat.format(timeReceived);
    }

    @Override
    public int compareTo(Result that) {

        if (this.equals(that))
            return 0;

        if (this.resultId > that.resultId)
            return -1;
        else if (this.resultId < that.resultId)
            return 1;
        else
            return 0;
    }
}
