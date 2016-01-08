package k23b.sa;

import java.util.NoSuchElementException;
import java.util.StringTokenizer;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 * It holds the result as well as the Job id from a nmap task.
 *
 */
@XmlRootElement(name = "Result")
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = "Result", propOrder = {
        "jobId",
        "jobResult"
})
public class Result {

    @XmlElement(required = true)
    private long jobId;
    @XmlElement(required = true)
    private String jobResult;

    public Result() {
    }

    public Result(long jobId, String jobResult) {
        this.jobId = jobId;
        this.jobResult = jobResult;
    }

    @Override
    public String toString() {

        String s = String.format("[%d] ", this.jobId);

        String l1 = s;
        String l2 = "";

        if (jobResult != null) {

            StringTokenizer st = new StringTokenizer(jobResult, System.lineSeparator());

            try {

                for (int i = 0; i < 4; i++)
                    st.nextToken();

                l1 = l1 + st.nextToken();

                StringBuilder sb = new StringBuilder();

                sb.append(System.lineSeparator());

                for (int j = 0; j < s.length(); j++)
                    sb.append(" ");

                sb.append(st.nextToken());

                l2 = sb.toString();

                try {
                    l1 = l1.substring(0, 80);
                } catch (IndexOutOfBoundsException e) {
                }

                try {
                    l2 = l2.substring(0, 80);
                } catch (IndexOutOfBoundsException e) {
                }

            } catch (NoSuchElementException e) {
            }
        }

        return l1 + l2;
    }
}
