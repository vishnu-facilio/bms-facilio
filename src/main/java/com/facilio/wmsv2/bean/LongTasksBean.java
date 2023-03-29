package com.facilio.wmsv2.bean;

import org.json.simple.JSONObject;

public interface LongTasksBean {

    void addBulkWeatherStationMigration(JSONObject data) throws Exception;

}
