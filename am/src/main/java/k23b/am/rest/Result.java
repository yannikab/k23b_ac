package k23b.am.rest;

import java.util.Date;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
/**
 * 	Serves the purpose of receiving the Job Result info from a SA.
 */
@XmlRootElement(name = "Result")
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = "Result", propOrder = {
        "jobId",
        "jobResult"
})
public class Result {
    @XmlTransient
    private long resultId;
    @XmlElement(required = true)
    private long jobId;
    @XmlTransient
    private Date time;
    @XmlElement(required = true)
    private String jobResult;

    public Result() {
    }

    public long getResultId() {
        return this.resultId;
    }

    public void setResultId(long resultId) {
        this.resultId = resultId;
    }

    public long getJobId() {
        return this.jobId;
    }

    public Date getTime() {
        return this.time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public String getJobResult() {
        return jobResult;
    }

    public void setJobResult(String jobResult) {
        this.jobResult = jobResult;
    }
}
