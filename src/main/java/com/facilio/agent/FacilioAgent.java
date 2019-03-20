package com.facilio.agent;

import com.facilio.bmsconsole.context.ControllerContext;

import java.util.HashSet;

public class FacilioAgent
{

    private  String  agentName ;
    private  String  agentDeviceDetails;
    private  String  agentVersion;
    private  Integer agentState ;
    private  Boolean agentConnStatus ;
    private  Long agentDataInterval ;
    private  Integer agentNumberOfControllers;
    private  Integer  agentType;
    private  Long lastModifiedTime;
    private Long lastDataRecievedTime;
    private Long deletedTime;
    private Long siteId;
    private Long id;
    private HashSet<ControllerContext> controllers;

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

    public Long getDeletedTime() { return deletedTime; }

    public void setDeletedTime(Long deletedTime) { this.deletedTime = deletedTime; }

    public Boolean getWritable() { return writable; }

    public void setWritable(Boolean writable) { this.writable = writable; }

    private Boolean writable;

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

    private  Long lastUpdatedTime;

    public String getAgentName() {
        return agentName;
    }

    public void setAgentName(String agentName) {
        this.agentName = agentName;
    }

    public String getAgentDeviceDetails() {
        return agentDeviceDetails;
    }

    public void setAgentDeviceDetails(String agentDeviceDetails) {
        this.agentDeviceDetails = agentDeviceDetails;
    }

    public String getAgentVersion() {
        return agentVersion;
    }

    public void setAgentVersion(String agentVersion) {
        this.agentVersion = agentVersion;
    }

    public Integer getAgentState() {
        return agentState;
    }

    public void setAgentState(Integer agentState) {
        this.agentState = agentState;
    }

    public Boolean getAgentConnStatus() {
        return agentConnStatus;
    }

    public void setAgentConnStatus(Boolean agentConnStatus) {
        this.agentConnStatus = agentConnStatus;
    }

    public Long getAgentDataInterval() {
        return agentDataInterval;
    }

    public void setAgentDataInterval(Long agentDataInterval) {
        this.agentDataInterval = agentDataInterval;
    }

    public Integer getAgentNumberOfControllers() {
        return agentNumberOfControllers;
    }

    public void setAgentNumberOfControllers(Integer agentNumberOfControllers) {
        this.agentNumberOfControllers = agentNumberOfControllers;
    }

    public Integer getAgentType() {
        return agentType;
    }

    public void setAgentType(Integer agentType) {
        this.agentType = agentType;
    }



    public Long getLastDataRecievedTime() {
        return lastDataRecievedTime;
    }

    public void setLastDataRecievedTime(Long lastDataRecievedTime) {
        this.lastDataRecievedTime = lastDataRecievedTime;
    }

    public HashSet<ControllerContext> getControllers() {
        return controllers;
    }

    public void setControllers(HashSet<ControllerContext> controllers) {
        this.controllers = controllers;
    }

    public void addController(ControllerContext controller) {
        if(controllers == null) {
            controllers = new HashSet<>();
        }
        controllers.add(controller);
    }
}
