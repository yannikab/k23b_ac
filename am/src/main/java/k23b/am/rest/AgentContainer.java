package k23b.am.rest;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "agents")
@XmlAccessorType(XmlAccessType.FIELD)
public class AgentContainer {

    @XmlAttribute(required = true)
    private String status;

    @XmlElement(name = "agent", required = false)
    private List<Agent> agents;

    public AgentContainer(String status) {

        this.status = status == null ? "" : status;

        this.agents = new ArrayList<Agent>();
    }

    public AgentContainer() {

        this(null);
    }

    public String getStatus() {
        return status;
    }

    public List<Agent> getAgents() {
        return agents;
    }
}
