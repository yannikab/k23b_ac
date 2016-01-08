package k23b.sa.JobProvider;

import java.util.StringTokenizer;

import k23b.sa.Job;
import k23b.sa.Result;
import k23b.sa.BlockingQueue.IBlockingQueue;

/**
 * Parses the line read from the JobGenerator or JobReader and constructs a Job element which then is returned
 *
 */
public class LineParser {

    private static boolean runNmapAsRoot = false;

    public static void setRunNmapAsRoot(boolean value) {
        runNmapAsRoot = value;
    }

    private String line;
    IBlockingQueue<Result> jobResultsBlockingQueue;

    public LineParser(String line, IBlockingQueue<Result> jobResultsBlockingQueue) {

        this.line = line;

        this.jobResultsBlockingQueue = jobResultsBlockingQueue;
    }

    public Job getJob() {

        StringTokenizer lineTokenizer = new StringTokenizer(line, ",");

        int id = Integer.parseInt(lineTokenizer.nextToken());

        String paramString = lineTokenizer.nextToken();

        String[] cmdArray;
        int cmdArraySize;

        StringTokenizer paramTokenizer = new StringTokenizer(paramString, " ");

        cmdArraySize = paramTokenizer.countTokens();
        String strToken = "";
        if (cmdArraySize > 0)
            strToken = paramTokenizer.nextToken();

        if (strToken.compareTo("exit") == 0) {
            cmdArray = new String[cmdArraySize];
            cmdArray[0] = strToken;
        } else if (strToken.compareTo("stop") == 0) {
            cmdArray = new String[cmdArraySize];
            cmdArray[0] = strToken;
            cmdArray[1] = paramTokenizer.nextToken();

        } else {

            cmdArraySize += runNmapAsRoot ? 2 : 1;

            cmdArray = new String[cmdArraySize];

            int curr = 0;

            if (runNmapAsRoot)
                cmdArray[curr++] = "sudo";

            cmdArray[curr++] = "nmap";
            cmdArray[curr++] = strToken;

            while (paramTokenizer.hasMoreTokens())
                cmdArray[curr++] = paramTokenizer.nextToken();

        }

        boolean isPeriodic = lineTokenizer.nextToken().equals("true");

        int period = Integer.parseInt(lineTokenizer.nextToken());

        return new Job(id, cmdArray, isPeriodic, period, this.jobResultsBlockingQueue);
    }
}
