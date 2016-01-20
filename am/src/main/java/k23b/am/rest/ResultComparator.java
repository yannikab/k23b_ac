package k23b.am.rest;

import java.util.Comparator;

public class ResultComparator extends Result implements Comparator<Result> {

    @Override
    public int compare(Result r1, Result r2) {
    
        int t = r1.timeReceived.compareTo(r2.timeReceived);
        
        if (t != 0)
            return t;
        
        return Long.compare(r1.resultId, r2.resultId);
    }
}
