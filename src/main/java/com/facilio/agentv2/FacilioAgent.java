package com.facilio.agentv2;

import com.facilio.agent.AgentKeys;
import org.json.simple.JSONObject;

public class FacilioAgent {


    private  String name;
    private  String deviceDetails;
    private  boolean connectionStatus;
    private  String version;
    private  int state;
    private  long interval;
    private  int numberOfControllers;
    private  String type; // change it to int using some enum
    private long id;
    private long siteId;
    private long deletedTime = -1L ;
    private boolean writable;
    private  long lastModifiedTime = -1L;
    private  long lastUpdatedTime = -1L;
    private long lastDataReceivedTime = -1L ;
    private String displayName;
    private long createdTime = -1L;
    private long transformWorkflowId = -1;

    public int getProcessorVersion() {
        return processorVersion;
    }

    public void setProcessorVersion(int processorVersion) {
        this.processorVersion = processorVersion;
    }

    private int processorVersion = -1;

    public long getTransformWorkflowId() {
		return transformWorkflowId;
	}
	public void setTransformWorkflowId(long transformWorkflowId) {
		this.transformWorkflowId = transformWorkflowId;
	}
	public long getCreatedTime() {
        return createdTime;
    }
    public void setCreatedTime(long createdTime) { this.createdTime = createdTime; }

   // private HashSet<ControllerContext> controllers;

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public String getDeviceDetails() {
        return deviceDetails;
    }
    public void setDeviceDetails(String deviceDetails) {
        this.deviceDetails = deviceDetails;
    }

    public String getVersion() {
        return version;
    }
    public void setVersion(String version) {
        this.version = version;
    }

    public int getState() {
        return state;
    }
    public void setState(int state) {
        this.state = state;
    }

    public Boolean getConnectionStatus() {
        return connectionStatus;
    }
    public void setConnectionStatus(Boolean connectionStatus) {
        this.connectionStatus = connectionStatus;
    }

    public long getInterval() { return interval; }
    public void setInterval(long interval) {
        this.interval = interval;
    }

    public int getNumberOfControllers() {
        return numberOfControllers;
    }
    public void setNumberOfControllers(int numberOfControllers) {
        this.numberOfControllers = numberOfControllers;
    }

    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }

    public long getId() {
        return id;
    }
    public void setId(long id) {
        this.id = id;
    }

    public long getSiteId() {
        return siteId;
    }
    public void setSiteId(long siteId) {
        this.siteId = siteId;
    }

    public long getDeletedTime() {
        return deletedTime;
    }
    public void setDeletedTime(long deletedTime) {
        this.deletedTime = deletedTime;
    }

    public Boolean getWritable() {
        return writable;
    }
    public void setWritable(Boolean writable) {
        this.writable = writable;
    }

    public long getLastModifiedTime() {
        return lastModifiedTime;
    }
    public void setLastModifiedTime(long lastModifiedTime) {
        this.lastModifiedTime = lastModifiedTime;
    }

    public long getLastUpdatedTime() {
        return lastUpdatedTime;
    }
    public void setLastUpdatedTime(long lastUpdatedTime) {
        this.lastUpdatedTime = lastUpdatedTime;
    }

    public long getLastDataReceivedTime() {
        return lastDataReceivedTime;
    }
    public void setLastDataReceivedTime(long lastDataReceivedTime) {
        this.lastDataReceivedTime = lastDataReceivedTime;
    }

    public String getDisplayName() {
        return displayName;
    }
    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    /*public HashSet<ControllerContext> getControllers() {
        return controllers;
    }
    public void addControllers(ControllerContext controller){
        controllers.add(controller);
    }
    public void setControllers(HashSet<ControllerContext> controllers) {
        this.controllers = controllers;
    }*/


    public JSONObject toJSON(){
        JSONObject payload = new JSONObject();
        long currTime = System.currentTimeMillis();
        if(  ( getId() > 0 ) ){
            payload.put(AgentConstants.ID,getId());
        }
        payload.put(AgentKeys.NAME,  getName());
        payload.put(AgentKeys.DEVICE_DETAILS, getDeviceDetails());
        payload.put(AgentKeys.CONNECTION_STATUS, getConnectionStatus());
        payload.put(AgentKeys.VERSION, getVersion());
        payload.put(AgentKeys.STATE, getState());
        payload.put(AgentKeys.DATA_INTERVAL, getInterval());
        payload.put(AgentKeys.NUMBER_OF_CONTROLLERS,getNumberOfControllers());
        payload.put(AgentKeys.AGENT_TYPE, getType());
        payload.put(AgentKeys.SITE_ID, getSiteId());
        payload.put(AgentKeys.DELETED_TIME,getDeletedTime());
        payload.put(AgentKeys.DISPLAY_NAME,getDisplayName());
        payload.put(AgentKeys.WRITABLE, getVersion());
        if(createdTime>0){
            payload.put(AgentKeys.CREATED_TIME,getCreatedTime());
        }else {
            createdTime = currTime;
            payload.put(AgentKeys.CREATED_TIME,getCreatedTime());
        }
        if(getLastModifiedTime() > 0){
            payload.put(AgentKeys.LAST_MODIFIED_TIME, getLastModifiedTime());
        }else {
            payload.put(AgentKeys.LAST_MODIFIED_TIME,currTime);
        }
        if(getLastDataReceivedTime() > 0){
            payload.put(AgentKeys.LAST_DATA_RECEIVED_TIME, getLastDataReceivedTime());
        }else {
            payload.put(AgentKeys.LAST_DATA_RECEIVED_TIME,currTime);
        }
        return payload;
    }


}
