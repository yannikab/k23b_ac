package k23b.sa.rest;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSeeAlso;

import k23b.sa.Result;

/**
 * A list for the compact distribution of a batch of Results to the AM.
 */

@XmlRootElement(name = "results")
@XmlSeeAlso({ Result.class })
@XmlAccessorType(XmlAccessType.NONE)
public class ResultList {

    private List<Result> resultList;

    public ResultList() {
        resultList = new ArrayList<Result>();
    }

    public ResultList(List<Result> resultList) {
        this.resultList = resultList;
    }

    @XmlElement(required = true)
    public List<Result> getResult() {
        return resultList;
    }

    public void setResult(List<Result> resultList) {
        this.resultList = resultList;
    }
}