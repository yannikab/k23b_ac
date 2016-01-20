package k23b.sa;

import java.util.NoSuchElementException;
import java.util.StringTokenizer;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * It holds the result as well as the Job id from a nmap task.
 *
 */

@XmlRootElement(name = "result")
@XmlAccessorType(XmlAccessType.FIELD)
public class Result {

    @XmlElement(required = true)
    protected long jobId;

    @XmlElement(required = true)
    protected String output;

    public Result() {
        super();
    }

    public Result(long jobId, String output) {

        this.jobId = jobId;
        this.output = output;
    }

    @Override
    public String toString() {

        String s = String.format("[%d] ", this.jobId);

        String l1 = s;
        String l2 = "";

        if (output != null) {

            StringTokenizer st = new StringTokenizer(output, System.lineSeparator());

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
