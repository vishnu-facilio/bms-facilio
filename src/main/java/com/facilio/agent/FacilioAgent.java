package com.facilio.agent;

public class FacilioAgent
{

    private  String  agentName ;
    private  String  agentDeviceDetails;
    private  String  agentVersion;
    private  Integer agentState ;
    private  Boolean agentConnStatus ;
    private  Long agentDataInterval ;
    private  Integer agentNumberOfControllers;
    private  String  agentType;
    private  Long lastModifiedTime;
    private Long lastDataRecievedTime;

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

    public String getAgentType() {
        return agentType;
    }

    public void setAgentType(String agentType) {
        this.agentType = agentType;
    }



    public Long getLastDataRecievedTime() {
        return lastDataRecievedTime;
    }

    public void setLastDataRecievedTime(Long lastDataRecievedTime) {
        this.lastDataRecievedTime = lastDataRecievedTime;
    }










}
