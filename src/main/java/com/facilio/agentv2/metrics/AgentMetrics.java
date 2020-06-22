package com.facilio.agentv2.metrics;

import com.facilio.modules.ModuleBaseWithCustomFields;

public class AgentMetrics extends ModuleBaseWithCustomFields {
    private long siteId;
    private long agentId;
    private long createdTime;
    private long lastUpdatedTime;
    private long size;
    private int publishType = -1;
    private long numberOfMessages;

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


    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public int getPublishType() {
        return publishType;
    }

    public void setPublishType(int publishType) {
        this.publishType = publishType;
    }

    public long getNumberOfMessages() {
        return numberOfMessages;
    }

    public void setNumberOfMessages(long numberOfMessages) {
        this.numberOfMessages = numberOfMessages;
    }


}
