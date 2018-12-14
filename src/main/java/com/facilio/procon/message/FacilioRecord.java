package com.facilio.procon.message;

import org.json.simple.JSONObject;

public class FacilioRecord {

    String partitionKey;
    JSONObject data;
    long timeStamp;

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
}
