package k23b.ac.rest;

import java.util.Date;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

/**
 * Serves the purpose of receiving the Job Result info from a SA.
 */

@Root(name = "result")
public class Result {

    @Element(required = false)
    private long resultId;

    @Element(required = true)
    private long jobId;

    @Element(required = false)
    private Date timeReceived;

    @Element(required = true)
    private String output;

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

    public long getJobId() {
        return this.jobId;
    }

    public Date getTimeReceived() {
        return this.timeReceived;
    }

    public String getJobResult() {
        return output;
    }
}
