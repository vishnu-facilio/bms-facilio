package com.facilio.agentnew;

import com.facilio.agent.AgentKeys;
import org.json.simple.JSONObject;

public class FacilioAgent {



    private  String name;
    private  String deviceDetails;
    private  Boolean connectionStatus;
    private  String version;
    private  Integer state;
    private  Long interval;
    private  Integer numberOfControllers;
    private  String type;
    private Long id;
    private Long siteId;
    private Long deletedTime;
    private Boolean writable;
    private  Long lastModifiedTime = -1L;
    private  Long lastUpdatedTime = -1L;
    private Long lastDataReceivedTime = -1L;
    private String displayName;
    private Long createdTime = -1L ;


    public Long getCreatedTime() {
        return createdTime;
    }

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

    public Integer getState() {
        return state;
    }
    public void setState(Integer state) {
        this.state = state;
    }

    public Boolean getConnectionStatus() {
        return connectionStatus;
    }
    public void setConnectionStatus(Boolean connectionStatus) {
        this.connectionStatus = connectionStatus;
    }

    public Long getInterval() {
        return interval;
    }
    public void setInterval(Long interval) {
        this.interval = interval;
    }

    public Integer getNumberOfControllers() {
        return numberOfControllers;
    }
    public void setNumberOfControllers(Integer numberOfControllers) {
        this.numberOfControllers = numberOfControllers;
    }

    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    public Long getSiteId() {
        return siteId;
    }
    public void setSiteId(Long siteId) {
        this.siteId = siteId;
    }

    public Long getDeletedTime() {
        return deletedTime;
    }
    public void setDeletedTime(Long deletedTime) {
        this.deletedTime = deletedTime;
    }

    public Boolean getWritable() {
        return writable;
    }
    public void setWritable(Boolean writable) {
        this.writable = writable;
    }

    public Long getLastModifiedTime() {
        return lastModifiedTime;
    }
    public void setLastModifiedTime(Long lastModifiedTime) {
        this.lastModifiedTime = lastModifiedTime;
    }

    public Long getLastUpdatedTime() {
        return lastUpdatedTime;
    }
    public void setLastUpdatedTime(Long lastUpdatedTime) {
        this.lastUpdatedTime = lastUpdatedTime;
    }

    public Long getLastDataReceivedTime() {
        return lastDataReceivedTime;
    }
    public void setLastDataReceivedTime(Long lastDataReceivedTime) {
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
        if( (getId() != null) && ( getId() > 0 ) ){
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
