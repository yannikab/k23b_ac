package k23b.am.rest;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "user")
@XmlAccessorType(XmlAccessType.FIELD)
public class User {

    @XmlAttribute(required = true)
    private String username;

    @XmlAttribute(required = true)
    private String password;

    @XmlElement(name = "job", required = false)
    private List<Job> jobs;

    public User(String username, String password) {

        this.username = username == null ? "" : username;
        this.password = password == null ? "" : password;

        this.jobs = new ArrayList<Job>();
    }

    public User() {

        this(null, null);
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public List<Job> getJobs() {
        return jobs;
    }
}
