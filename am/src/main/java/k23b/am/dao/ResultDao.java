package k23b.am.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.Logger;

/**
 * Data access layer for result objects.
 */
public class ResultDao {

    private static final Logger log = Logger.getLogger(ResultDao.class);

    /**
     * Creates a new result using the data provided.
     * 
     * @param jobId id of the job for which the result is being created.
     * @param output the result's job output.
     * @return the newly created result's id.
     * @throws DaoException if the newly created result's id could not be retrieved or a data access error occurs.
     */
    public static long create(long jobId, String output) throws DaoException {

        String sql = ""
                + "insert into amdb_result "
                + "(JOB_ID, OUTPUT) "
                + "values "
                + "(?, ?)";

        try (SynchronizedStatement ss = ConnectionSingleton.getInstance().getStatement(sql)) {

            log.debug("");
            log.debug("Executing query: " + sql);

            log.debug("Setting parameter 1 to: " + jobId);
            ss.setLong(1, jobId);

            log.debug("Setting parameter 2 to: " + output);
            ss.setString(2, output);

            int rows = ss.executeUpdate();
            log.debug(rows + (rows == 1 ? " row " : " rows ") + "inserted.");

            ResultSet rs = ss.getGeneratedKeys();

            if (!rs.next())
                throw new DaoException("Could not retrieve id for created result.");

            long id = rs.getLong(1);
            log.debug("Id returned: " + id);

            return id;

        } catch (SQLException e) {
            // e.printStackTrace();
            log.error(e.getMessage());

            throw new DaoException("Error while creating result for job with id: " + jobId);
        }
    }

    /**
     * Retrieves an existing result with specified id.
     * 
     * @param resultId the result's id.
     * @return an object representing the result found, or null if a result with specified id was not found.
     * @throws DaoException if a data access error occurs.
     */
    public static ResultDao findById(long resultId) throws DaoException {

        String sql = ""
                + "select * from amdb_result "
                + "where "
                + "RESULT_ID = ?";

        try (SynchronizedStatement ss = ConnectionSingleton.getInstance().getStatement(sql)) {

            log.debug("");
            log.debug("Executing query: " + sql);

            log.debug("Setting parameter 1 to: " + resultId);
            ss.setLong(1, resultId);

            ss.executeQuery();

            ResultSet rs = ss.getResultSet();

            if (!rs.next()) {
                log.debug("0 rows selected.");
                return null;
            }

            long id = rs.getLong("RESULT_ID");
            long jobId = rs.getLong("JOB_ID");
            Date timeReceived = rs.getTimestamp("TIME_RECEIVED");
            String output = rs.getString("OUTPUT");

            log.debug("1 row selected.");
            return new ResultDao(id, jobId, timeReceived, output);

        } catch (SQLException e) {
            // e.printStackTrace();
            log.error(e.getMessage());

            throw new DaoException("Error while finding agent by id: " + resultId);
        }
    }

    /**
     * Retrieves all results that have been received for a specific job.
     * 
     * @param jobId the job's id.
     * @return a set of objects, each representing a result.
     * @throws DaoException if a data access error occurs.
     */
    public static Set<ResultDao> findAllWithJobId(long jobId) throws DaoException {

        String sql = ""
                + "select * from amdb_result "
                + "where "
                + "JOB_ID = ?";

        try (SynchronizedStatement ss = ConnectionSingleton.getInstance().getStatement(sql)) {

            log.debug("");
            log.debug("Executing query: " + sql);

            log.debug("Setting parameter 1 to: " + jobId);
            ss.setLong(1, jobId);

            ss.executeQuery();

            ResultSet rs = ss.getResultSet();

            Set<ResultDao> results = new HashSet<ResultDao>();

            int rows = 0;

            while (rs.next()) {

                rows++;
                long resultId = rs.getLong("RESULT_ID");
                long id = rs.getLong("JOB_ID");
                Date timeReceived = rs.getTimestamp("TIME_RECEIVED");
                String output = rs.getString("OUTPUT");

                results.add(new ResultDao(resultId, id, timeReceived, output));
            }

            log.debug(rows + (rows == 1 ? " row " : " rows ") + "selected.");
            return results;

        } catch (SQLException e) {
            // e.printStackTrace();
            throw new DaoException("Error while fetching results with for job with id: " + jobId);
        }

    }

    /**
     * Retrieves all results received for a specific agent within a specific time range.
     * 
     * @param agentId the agent's id.
     * @param startTime start of the time range.
     * @param endTime end of the time range.
     * @return a set of objects, each representing a result.
     * @throws DaoException if a data access error occurs.
     */
    public static Set<ResultDao> findAllWithinDates(long agentId, Date startTime, Date endTime) throws DaoException {

        String sql = ""
                + "select * "
                + "from amdb_result inner join amdb_job "
                + "on amdb_result.JOB_ID = amdb_job.JOB_ID "
                + "where amdb_job.AGENT_ID = ? "
                + "and amdb_result.TIME_RECEIVED >= ? "
                + "and amdb_result.TIME_RECEIVED <= ?";

        try (SynchronizedStatement ss = ConnectionSingleton.getInstance().getStatement(sql)) {

            log.debug("");
            log.debug("Executing query: " + sql);

            log.debug("Setting parameter 1 to: " + agentId);
            ss.setLong(1, agentId);

            log.debug("Setting parameter 2 to: " + startTime);
            ss.setTimestamp(2, new Timestamp(startTime.getTime()));

            log.debug("Setting parameter 3 to: " + endTime);
            ss.setTimestamp(3, new Timestamp(endTime.getTime()));

            ss.executeQuery();

            ResultSet rs = ss.getResultSet();

            Set<ResultDao> results = new HashSet<ResultDao>();

            int rows = 0;

            while (rs.next()) {

                rows++;
                long resultId = rs.getLong("RESULT_ID");
                long jobId = rs.getLong("JOB_ID");
                Date timeReceived = rs.getTimestamp("TIME_RECEIVED");
                String output = rs.getString("OUTPUT");

                results.add(new ResultDao(resultId, jobId, timeReceived, output));
            }

            log.debug(rows + (rows == 1 ? " row " : " rows ") + "selected.");
            return results;

        } catch (SQLException e) {
            // e.printStackTrace();
            throw new DaoException("Error while finding results between times for agent with id: " + agentId);
        }
    }

    /**
     * Retrieves all results that have been received within a specific time range.
     * 
     * @param startTime start of the time range.
     * @param endTime end of the time range.
     * @return a set of objects, each representing a result.
     * @throws DaoException if a data access error occurs.
     */
    public static Set<ResultDao> findAllWithinDates(Date startTime, Date endTime) throws DaoException {

        String sql = ""
                + "select * from amdb_result "
                + "where "
                + "TIME_RECEIVED >= ? and "
                + "TIME_RECEIVED <= ?";

        try (SynchronizedStatement ss = ConnectionSingleton.getInstance().getStatement(sql)) {

            log.debug("");
            log.debug("Executing query: " + sql);

            log.debug("Setting parameter 1 to: " + startTime);
            ss.setTimestamp(1, new Timestamp(startTime.getTime()));

            log.debug("Setting parameter 2 to: " + endTime);
            ss.setTimestamp(2, new Timestamp(endTime.getTime()));

            ss.executeQuery();

            ResultSet rs = ss.getResultSet();

            Set<ResultDao> results = new HashSet<ResultDao>();

            int rows = 0;

            while (rs.next()) {

                rows++;
                long resultId = rs.getLong("RESULT_ID");
                long jobId = rs.getLong("JOB_ID");
                Date timeReceived = rs.getTimestamp("TIME_RECEIVED");
                String output = rs.getString("OUTPUT");

                results.add(new ResultDao(resultId, jobId, timeReceived, output));
            }

            log.debug(rows + (rows == 1 ? " row " : " rows ") + "selected.");
            return results;

        } catch (SQLException e) {
            // e.printStackTrace();
            throw new DaoException("Error while finding results between times " + startTime + " and " + endTime + ".");
        }
    }

    private long resultId;
    private long jobId;
    private Date timeReceived;
    private String output;

    public long getResultId() {
        return resultId;
    }

    public long getJobId() {
        return jobId;
    }

    public Date getTimeReceived() {
        return timeReceived;
    }

    public String getOutput() {
        return output;
    }

    private ResultDao(long resultId, long jobId, Date timeReceived, String output) {
        this.resultId = resultId;
        this.jobId = jobId;
        this.timeReceived = timeReceived;
        this.output = output;
    }
}
