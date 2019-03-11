package com.facilio.procon.message;

import org.json.simple.JSONObject;

public class FacilioRecord {

    private String partitionKey;
    private JSONObject data;
    private long timeStamp;
    private String id;

    public FacilioRecord(String partitionKey, JSONObject data) {
        this.data = data;
        this.partitionKey = partitionKey;
        timeStamp = System.currentTimeMillis();
    }

    public String getPartitionKey() {
        return partitionKey;
    }

    public void setPartitionKey(String partitionKey) {
        this.partitionKey = partitionKey;
    }

    public JSONObject getData() {
        return data;
    }

    public void setData(JSONObject data) {
        this.data = data;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
