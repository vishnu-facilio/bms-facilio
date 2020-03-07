package com.facilio.agentv2.metrics;

public class AgentMetrics {
    private long siteId;
    private long agentId;
    private long createdTime;
    private long lastUpdatedTime;
    private long dataSize;
    private int publishType = -1;
    private long numberOfMsgs;

    public long getLastUpdatedTime() { return lastUpdatedTime; }

    public void setLastUpdatedTime(long lastUpdatedTime) { this.lastUpdatedTime = lastUpdatedTime; }

    public long getSiteId() {
        return siteId;
    }

    public void setSiteId(long siteId) {
        this.siteId = siteId;
    }

    public long getAgentId() {
        return agentId;
    }

    public void setAgentId(long agentId) {
        this.agentId = agentId;
    }

    public long getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(long createdTime) {
        this.createdTime = createdTime;
    }


    public long getDataSize() {
        return dataSize;
    }

    public void setDataSize(long dataSize) {
        this.dataSize = dataSize;
    }

    public int getPublishType() {
        return publishType;
    }

    public void setPublishType(int publishType) {
        this.publishType = publishType;
    }

    public long getNumberOfMsgs() {
        return numberOfMsgs;
    }

    public void setNumberOfMsgs(long numberOfMsgs) {
        this.numberOfMsgs = numberOfMsgs;
    }


}
