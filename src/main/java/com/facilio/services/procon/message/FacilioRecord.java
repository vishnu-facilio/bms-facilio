package com.facilio.services.procon.message;

import org.json.simple.JSONObject;

public class FacilioRecord {

    private String partitionKey;
    private JSONObject data;
    private long timeStamp;
    private long id;
    private long size;

    public FacilioRecord(String partitionKey, JSONObject data) {
        this.data = data;
        size = 0;
        if( (data != null) && (!data.isEmpty()) ){
            size = data.size();
        }
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

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getSize() { return size; }

    public void setSize(long size) { this.size = size; }
}
