package k23b.sa.rest;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import k23b.sa.Result;

/**
 * A list for the compact distribution of a batch of Results to a SA.
 */

@XmlRootElement(name = "results")
@XmlAccessorType(XmlAccessType.FIELD)
public class ResultContainer {

    @XmlAttribute(required = true)
    private String status;

    @XmlElement(name = "result", required = false)
    private List<Result> results;

    public ResultContainer(String status) {

        this.status = status == null ? "" : status;

        this.results = new ArrayList<Result>();
    }

    public ResultContainer() {

        this(null);
    }

    public String getStatus() {
        return status;
    }

    public List<Result> getResults() {
        return results;
    }
}
