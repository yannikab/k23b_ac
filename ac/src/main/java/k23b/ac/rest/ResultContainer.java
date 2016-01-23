package k23b.ac.rest;

import java.util.ArrayList;
import java.util.List;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

@Root(name = "results")
public class ResultContainer {

    @Attribute(required = true)
    private String status;

    @Element(name = "result", required = false)
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
        return this.results;
    }
}