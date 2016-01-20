package k23b.am.rest;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "users")
@XmlAccessorType(XmlAccessType.FIELD)
public class UserContainer {

    @XmlElement(name = "user", required = false)
    private List<User> users;

    public UserContainer() {

        this.users = new ArrayList<User>();
    }

    public List<User> getUsers() {
        return users;
    }
}
