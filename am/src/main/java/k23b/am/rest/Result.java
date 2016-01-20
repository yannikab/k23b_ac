package k23b.am.rest;

import java.util.Date;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

/**
 * Serves the purpose of sending and receiving a job result.
 */

@XmlRootElement(name = "result")
@XmlAccessorType(XmlAccessType.FIELD)
public class Result {

    @XmlElement(required = false)
    protected long resultId;

    @XmlElement(required = true)
    protected long jobId;

    @XmlJavaTypeAdapter(DateAdapter.class)
    @XmlElement(required = false)
    protected Date timeReceived;

    @XmlElement(required = true)
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
