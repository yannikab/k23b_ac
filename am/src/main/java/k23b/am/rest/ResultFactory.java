package k23b.am.rest;

import k23b.am.dao.ResultDao;

public class ResultFactory extends Result {

    public static Result fromDao(ResultDao rd) {

        Result r = new Result();

        r.resultId = rd.getResultId();
        r.output = rd.getOutput();
        r.jobId = rd.getJobId();
        r.timeReceived = rd.getTimeReceived();

        return r;
    }
}
