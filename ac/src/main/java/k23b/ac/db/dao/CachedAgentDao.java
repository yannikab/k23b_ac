package k23b.ac.db.dao;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

/**
 * The Data Access Object class for the manipulation of the Cached Agents Table and the retrieval of information from it.
 *
 */
public class CachedAgentDao {
    
    private static String[] cachedagentTableColumns = { DatabaseHandler.getKeyCaAgentId(), DatabaseHandler.getKeyCaAgentHash(), 
            DatabaseHandler.getKeyCaTimeAccepted(), DatabaseHandler.getKeyCaTimeJobRequest(), DatabaseHandler.getKeyCaTimeTerminated(),
            DatabaseHandler.getKeyCaAgentStatus() };
    
    /**
     * The creation of a new Cached Agent row in the Cached Agents Table.
     * 
     * @param agentId
     * @param agentHash
     * @param timeAccepted
     * @param timeJobRequest
     * @param timeTerminated
     * @param agentStatus
     * @throws DaoException
     */
    // this method assumes that an agent does not exist with this id, and blindly creates it
    @SuppressLint("SimpleDateFormat")
    public static void create(long agentId, String agentHash, Date timeAccepted, Date timeJobRequest, Date timeTerminated, CachedAgentStatus agentStatus) throws DaoException {
        
        Log.d(CachedAgentDao.class.getName(), "Creating Cached Agent Dao with AgentId: " + agentId + " AgentHash: " + agentHash + " TimeAccepted: " + timeAccepted 
                + " TimeJobRequest: " + timeJobRequest + " TimeTerminated: " + timeTerminated + " AgentStatus: "+ agentStatus);
        
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        ContentValues values = new ContentValues();
        values.put(DatabaseHandler.getKeyCaAgentId(), agentId);
        values.put(DatabaseHandler.getKeyCaAgentHash(), agentHash);
        values.put(DatabaseHandler.getKeyCaTimeAccepted(), dateFormat.format(timeAccepted));
        values.put(DatabaseHandler.getKeyCaTimeJobRequest(), dateFormat.format(timeJobRequest));
        values.put(DatabaseHandler.getKeyCaTimeTerminated(), dateFormat.format(timeTerminated));
        values.put(DatabaseHandler.getKeyCaAgentStatus(), agentStatus.toString());
        
        DatabaseHandler dbHandler = DatabaseHandler.getDBHandler();

        // Express the need for an open Database
        SQLiteDatabase db = dbHandler.openDatabase();
        long rowId;
        
        try {

            rowId = db.insertOrThrow(DatabaseHandler.getCachedJobsTable(), null, values);

            if (rowId < 0) {
                // Database not needed anymore
                dbHandler.closeDatabase();
                
                String message = "Error while inserting Cached Agent with AgentId: "+ agentId + " AgentHash: " + agentHash + " Time Accepted: "
                        + timeAccepted + " TimeJobRequest: "+ timeJobRequest + " Time Terminated: " + timeTerminated + " AgentStatus: "+ agentStatus.toString(); 
                
                Log.e(CachedAgentDao.class.getName(), message);
                throw new DaoException(message);
            }
        } catch (SQLException e){
            
         // Database not needed anymore
            dbHandler.closeDatabase();

            Log.e(CachedAgentDao.class.getName(), e.getMessage());
            throw new DaoException("Error while inserting Cached Agent with AgentId: "+ agentId + " AgentHash: " + agentHash + " Time Accepted: "
                    + timeAccepted + " TimeJobRequest: "+ timeJobRequest + " Time Terminated: " + timeTerminated + " AgentStatus: "+ agentStatus.toString() + " | "+ e.getMessage());
        }
        
        Cursor cursor = db.query(DatabaseHandler.getCachedAgentsTable(), cachedagentTableColumns, DatabaseHandler.getKeyCaAgentId() + " = "+ agentId, null, null, null,null);
        
        if (cursor.getCount() > 1) {
            cursor.close();
            // Database not needed anymore
            dbHandler.closeDatabase();

            Log.e(CachedAgentDao.class.getName(), "More than one Cached Agents with Id: " + agentId);
            throw new DaoException("More than one Cached Agents with Id: " + agentId);
        }
        if (cursor.moveToFirst()) {
              
            Log.d(CachedAgentDao.class.getName(), "Created Cached Agent Dao with AgentId: " + cursor.getLong(0) + " AgentHash: " + cursor.getString(1) + " TimeAccepted: " + cursor.getString(2) 
                + " TimeJobRequest: " + cursor.getString(3) + " TimeTerminated: " + cursor.getString(4) + " AgentStatus: "+ cursor.getString(5) + " successfully!");
            
            cursor.close();
            // Database not needed anymore
            dbHandler.closeDatabase();
            return;
        }
        
        String message = "Cached Agent Dao with AgentId: " + agentId + " AgentHash: " + agentHash + " TimeAccepted: " + timeAccepted 
                + " TimeJobRequest: " + timeJobRequest + " TimeTerminated: " + timeTerminated + " AgentStatus: "+ agentStatus.toString() + "created but NOT FOUND";
        
        cursor.close();
        // Database not needed anymore
        dbHandler.closeDatabase();

        Log.e(CachedAgentDao.class.getName(), message);
        throw new DaoException(message);
    }
    
    /**
     * The update of a Cached Agent row in the Cached Agents Table.
     * 
     * @param agentId
     * @param agentHash
     * @param timeAccepted
     * @param timeJobRequest
     * @param timeTerminated
     * @param agentStatus
     * @throws DaoException
     */
    // this method assumes that an agent exists with this id, and blindly updates it
    @SuppressLint("SimpleDateFormat")
    public static void update(long agentId, String agentHash, Date timeAccepted, Date timeJobRequest, Date timeTerminated, CachedAgentStatus agentStatus) throws DaoException {
        
        Log.d(CachedAgentDao.class.getName(), "Updating Cached Agent Dao with AgentId: " + agentId + " AgentHash: " + agentHash + " TimeAccepted: " + timeAccepted 
                + " TimeJobRequest: " + timeJobRequest + " TimeTerminated: " + timeTerminated + " AgentStatus: "+ agentStatus);
        
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        ContentValues values = new ContentValues();
        values.put(DatabaseHandler.getKeyCaAgentId(), agentId);
        values.put(DatabaseHandler.getKeyCaAgentHash(), agentHash);
        values.put(DatabaseHandler.getKeyCaTimeAccepted(), dateFormat.format(timeAccepted));
        values.put(DatabaseHandler.getKeyCaTimeJobRequest(), dateFormat.format(timeJobRequest));
        values.put(DatabaseHandler.getKeyCaTimeTerminated(), dateFormat.format(timeTerminated));
        values.put(DatabaseHandler.getKeyCaAgentStatus(), agentStatus.toString());
        
        DatabaseHandler dbHandler = DatabaseHandler.getDBHandler();

        // Express the need for an open Database
        SQLiteDatabase db = dbHandler.openDatabase();
        
        int rowsAffected = db.update(DatabaseHandler.getCachedAgentsTable(), values, DatabaseHandler.getKeyCaAgentId() +" = " + agentId, null);
        
        if(rowsAffected != 1){
            // Database not needed anymore
            dbHandler.closeDatabase();
            throw new DaoException("None or more than one Rows affected");
        }
        
        // Database not needed anymore
        dbHandler.closeDatabase();
        Log.d(CachedAgentDao.class.getName(), "Cached Agent with AgentId: " + agentId + " updated.");

    }
    
    /**
     * Search if the Cached Agent with agentId exists.
     * 
     * @param agentId
     * @return true if it exists; false otherwise.
     * @throws DaoException
     */
    public static boolean exists(long agentId) throws DaoException {
        
        Log.d(CachedAgentDao.class.getName(), "Searching for the existance of CachedJob with JobId: " + agentId);
        DatabaseHandler dbHandler = DatabaseHandler.getDBHandler();
        // Express the need for an open Database
        SQLiteDatabase db = dbHandler.openDatabase();
        
        Cursor cursor = db.query(DatabaseHandler.getCachedAgentsTable(), cachedagentTableColumns,
                DatabaseHandler.getKeyCaAgentId() + " = " + agentId, null, null, null, null);
        
        if(cursor.getCount() > 1){
            cursor.close();
            // Database not needed anymore
            dbHandler.close();
            
            Log.e(CachedAgentDao.class.getName(), "More than one Cached Agent with Id: " + agentId);
            throw new DaoException("More than one Cached Agent with Id: " + agentId);
        }
        
        if (cursor.moveToFirst()) {
            Log.d(CachedAgentDao.class.getName(), "1 row selected");
            cursor.close();
            // Database not needed anymore
            dbHandler.closeDatabase();
            return true;
        }
        
        Log.d(CachedAgentDao.class.getName(), "0 row selected");
        cursor.close();
        // Database not needed anymore
        dbHandler.closeDatabase();
        return false;
    }
    
    /**
     * Search for all the Cached Agents.
     * 
     * @return A set of all CachedAgentDaos.
     * @throws DaoException
     */
    public static Set<CachedAgentDao> findAll() throws DaoException {
        
        Log.d(CachedAgentDao.class.getName(), "Searching all Cached Agents");
        Set<CachedAgentDao> agentSet = new HashSet<CachedAgentDao>();

        DatabaseHandler dbHandler = DatabaseHandler.getDBHandler();
        // Express the need for an open Database
        SQLiteDatabase db = dbHandler.openDatabase();
        
        Cursor cursor = db.query(DatabaseHandler.getCachedAgentsTable(), cachedagentTableColumns, null, null, null, null, null);
        int rows = 0;
        
        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                
                agentSet.add(new CachedAgentDao(cursor.getLong(0), cursor.getString(1), cursor.getString(2), cursor.getString(3), 
                        cursor.getString(4), CachedAgentStatus.valueOf(cursor.getString(5))));
                
                rows++;
                cursor.moveToNext();
            }
        }
        Log.d(CachedAgentDao.class.getName(), rows + (rows == 1 ? " row " : " rows ") + "selected.");

        cursor.close();
        // Database not needed anymore
        dbHandler.closeDatabase();
        
        return agentSet;
    }

    private long agentId;
    private String agentHash;
    private Date timeAccepted;
    private Date timeJobRequest;
    private Date timeTerminated;
    private CachedAgentStatus agentStatus;

    public long getAgentId() {
        return agentId;
    }

    public String getAgentHash() {
        return agentHash;
    }

    public Date getTimeAccepted() {
        return timeAccepted;
    }

    public void setTimeAccepted(Date timeAccepted) {
        this.timeAccepted = timeAccepted;
    }

    public Date getTimeJobRequest() {
        return timeJobRequest;
    }

    public void setTimeJobRequest(Date timeJobRequest) {
        this.timeJobRequest = timeJobRequest;
    }

    public Date getTimeTerminated() {
        return timeTerminated;
    }

    public void setTimeTerminated(Date timeTerminated) {
        this.timeTerminated = timeTerminated;
    }

    public CachedAgentStatus getAgentStatus() {
        return agentStatus;
    }

    public void setAgentStatus(CachedAgentStatus agentStatus) {
        this.agentStatus = agentStatus;
    }

    public CachedAgentDao(long agentId, String agentHash, Date timeAccepted, Date timeJobRequest, Date timeTerminated, CachedAgentStatus agentStatus) {
        super();
        this.agentId = agentId;
        this.agentHash = agentHash;
        this.timeAccepted = timeAccepted;
        this.timeJobRequest = timeJobRequest;
        this.timeTerminated = timeTerminated;
        this.agentStatus = agentStatus;
    }
    
    @SuppressLint("SimpleDateFormat")
    public CachedAgentDao(long agentId, String agentHash, String timeAccepted, String timeJobRequest, String timeTerminated, CachedAgentStatus agentStatus) {
        super();
        
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        
        this.agentId = agentId;
        this.agentHash = agentHash;
        try{
            this.timeAccepted = format.parse(timeAccepted);
            this.timeJobRequest = format.parse(timeJobRequest);
            this.timeTerminated = format.parse(timeTerminated);
        }catch (ParseException e){
            Log.e(CachedAgentDao.class.getName(), "Parse error on Date parsing");
        }

        this.agentStatus = agentStatus;
    }
    
}
