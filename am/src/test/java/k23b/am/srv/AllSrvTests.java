package k23b.am.srv;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({ AdminSrvTest.class, UserSrvTest.class, AgentSrvTest.class, DbTest.class, RequestSrvTest.class, JobSrvTest.class, ResultSrvTest.class })
public class AllSrvTests {

}
