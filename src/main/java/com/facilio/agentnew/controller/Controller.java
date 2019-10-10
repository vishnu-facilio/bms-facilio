package com.facilio.agentnew.controller;

import com.facilio.agent.AgentKeys;
import com.facilio.agentnew.AgentConstants;
import com.facilio.bmsconsole.context.AssetContext;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;

import java.util.Collection;
import java.util.Map;

public abstract class Controller extends AssetContext {

    private static final Logger LOGGER = LogManager.getLogger(Controller.class.getName());

    public final String IDENTIFIER_SEPERATOR = "_#_";

    private long id;
    private long orgId;
    private long agentId;
    private String name;
    private long dataInterval;
    private boolean writable;
    private boolean active;
    private int controllerType;
    private Object controllerProps;
    private int availablePoints = 0;
    //private int portNumber;
    private long createdTime = -1;
    private long lastModifiedTime = -1;
    private long lastDataRecievedTime = -1;
    private long deletedTime =-1;

    public Controller() { }

    public Controller(long agentId, long orgId) {
        this.agentId = agentId;
        this.orgId = orgId;
        setAvailablePoints(0);
    }

    /**
     * Make identifier must be implemented for each controller using  IDENTIFIER_SEPERATOR
     * @return the identifier itself.
     * @throws Exception
     */
    public abstract String makeIdentifier() throws Exception;

    public long getAgentId() {
        return agentId;
    }
    public void setAgentId(long agentId) {
        this.agentId = agentId;
    }

    @Override
    public String getName() { return name; }
    @Override
    public void setName(String name) { this.name = name; }

    public long getDataInterval() {
        return dataInterval;
    }
    public void setDataInterval(long dataInterval) {
        this.dataInterval = dataInterval;
    }

    public boolean isWritable() {
        return writable;
    }
    public void setWritable(boolean writable) {
        this.writable = writable;
    }

    public boolean isActive() {
        return active;
    }
    public void setActive(boolean active) {
        this.active = active;
    }

    public int getControllerType() {
        return controllerType;
    }
    public void setControllerType(int controllerType) {
        this.controllerType = controllerType;
    }

    public Object getControllerProps() {
        return controllerProps;
    }
    public void setControllerProps(Object controllerProps) {
        this.controllerProps = controllerProps;
    }

    public int getAvailablePoints() {
        return availablePoints;
    }
    public void setAvailablePoints(int availablePoints) {
        this.availablePoints = availablePoints;
    }

    public long getCreatedTime() {
        return createdTime;
    }
    public void setCreatedTime(long createdTime) {
        this.createdTime = createdTime;
    }

    public long getLastModifiedTime() {
        return lastModifiedTime;
    }
    public void setLastModifiedTime(long lastModifiedTime) {
        this.lastModifiedTime = lastModifiedTime;
    }

    public long getLastDataRecievedTime() {
        return lastDataRecievedTime;
    }
    public void setLastDataRecievedTime(long lastDataRecievedTime) {
        this.lastDataRecievedTime = lastDataRecievedTime;
    }

    public long getDeletedTime() {
        return deletedTime;
    }
    public void setDeletedTime(long deletedTime) {
        this.deletedTime = deletedTime;
    }


    /**
     * Gives Controller as JSON including child controller's details.
     * @return {@link JSONObject}
     */
    public JSONObject toJSON() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.putAll(getParentJSON());
        jsonObject.putAll(getChildJSON());
        return jsonObject;
    }

    /**
     * Gives child Controller as JSON .
     *
     * used to insert respective child controller in its table.
     * @return {@link JSONObject}
     */
    public abstract JSONObject getChildJSON();

    /**
     * gives Controller as JSON with just parent controller's data.
     *
     * used for inserting controller in controller table.
     * @return
     */
    public JSONObject getParentJSON() {
        JSONObject object = new JSONObject();
        object.put(AgentConstants.NAME, getName());
        object.put(AgentKeys.AGENT_ID, agentId);
        object.put(AgentConstants.DATA_INTERVAL, getDataInterval());
        object.put(AgentConstants.WRITABLE, isWritable());
        object.put(AgentConstants.ACTIVE, isActive());
        object.put(AgentConstants.CONTROLLER_TYPE, getControllerType());
        object.put(AgentConstants.CONTROLLER_PROPS, "controllerProps");
        object.put(AgentConstants.AVAILABLE_POINTS, getAvailablePoints());
        if (getCreatedTime() < 0) {
            object.put(AgentConstants.CREATED_TIME, System.currentTimeMillis());
        }
        object.put(AgentConstants.CREATED_TIME, getCreatedTime());
        if (getLastModifiedTime() < 0) {
            object.put(AgentConstants.LAST_MODIFIED_TIME, System.currentTimeMillis());
        }
        object.put(AgentConstants.LAST_MODIFIED_TIME, getLastModifiedTime());
        object.put(AgentConstants.LAST_DATA_SENT_TIME, getLastDataRecievedTime());
        object.put(AgentConstants.DELETED_TIME, -1);
        return object;
    }

    /**
     * Builds a {@link Controller} from map.
     * @param row can be {@link Map<String, Object>} from database or {@link JSONObject} from agent.
     * @return {@link Controller} which will be an instance of corresponding child controller's Class.
     */
    public Controller getControllerFromJSON(Map<String, Object> row){
        LOGGER.info(this.hashCode()+" get child jsin from bmscontroller " +this.getChildJSON());
        if(containsValueCheck(AgentConstants.ID,row)){
            setId((Long) row.get(AgentConstants.ID));
        }
        if(containsValueCheck(AgentConstants.NAME,row)){
            setName((String) row.get(AgentConstants.NAME));
        }
        if(containsValueCheck(AgentConstants.DATA_INTERVAL,row)){
            setDataInterval((Long) row.get(AgentConstants.DATA_INTERVAL));
        }
        if(containsValueCheck(AgentConstants.WRITABLE,row)){
            setWritable((Boolean) row.get(AgentConstants.WRITABLE));
        }
        if(containsValueCheck(AgentConstants.ACTIVE,row)){
            setActive((Boolean) row.get(AgentConstants.ACTIVE));
        }
        if(containsValueCheck(AgentConstants.CONTROLLER_TYPE,row)){
            setControllerType(Math.toIntExact((Long) row.get(AgentConstants.CONTROLLER_TYPE)));
        }
        if(containsValueCheck(AgentConstants.CONTROLLER_PROPS,row)){
            setControllerProps(row.get(AgentConstants.CONTROLLER_PROPS));
        }
        if(containsValueCheck(AgentConstants.AVAILABLE_POINTS,row)){
            setAvailablePoints(Math.toIntExact((Long) row.get(AgentConstants.AVAILABLE_POINTS)));
        }
        if(containsValueCheck(AgentConstants.CREATED_TIME,row)){
            setCreatedTime((Long) row.get(AgentConstants.CREATED_TIME));
        }
        if(containsValueCheck(AgentConstants.LAST_MODIFIED_TIME,row)){
           setLastModifiedTime((Long) row.get(AgentConstants.LAST_MODIFIED_TIME));
        }
        if(containsValueCheck(AgentConstants.LAST_DATA_SENT_TIME,row)){
           setLastDataRecievedTime((Long) row.get(AgentConstants.LAST_MODIFIED_TIME));
        }
        if(containsValueCheck(AgentConstants.DELETED_TIME,row)){ setDeletedTime((Long) row.get(AgentConstants.DELETED_TIME));
        }
        return this;
    }

    /**
     * This method checks {@link Map<String, Object>} for the parameter key and if it's value isn't null.
     * @param key {@link String} which is the key whose value one wants.
     * @param jsonObject {@link JSONObject} which has values with respective keys.
     * @return true if there's the specified key and its value is't null, and false if key is missing or has a null value.
     */
    public static boolean containsValueCheck(String key, Map<String, Object> jsonObject){
        if(jsonObject.containsKey(key) && ( jsonObject.get(key) != null) ){
            return true;
        }
        return false;
    }

    /**
     * Checks if an object is null or empty.
     * @param object
     * @return
     */
    public static boolean isNotNull(Object object){
        if(object != null){
            if( object instanceof String){
                return ! ((String) object).isEmpty();
            }
            if( object instanceof Collection){
                return ! ( ((Collection)object).isEmpty() );
            }
            if( object instanceof Map){
                return ! ( ((Map)object).isEmpty() );
            }
        }
        return (object != null);
    }
}
