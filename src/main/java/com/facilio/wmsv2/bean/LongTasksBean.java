package com.facilio.wmsv2.bean;

import org.json.simple.JSONObject;

public interface LongTasksBean {

    void addBulkWeatherStationMigration(JSONObject data) throws Exception;
    
    void populateVMData(JSONObject data) throws Exception;

    void addDefaultSignupDataToSandbox(JSONObject data) throws Exception;
    void createPackageForSandboxData(JSONObject data) throws Exception;
    void installPackageForSandboxData(JSONObject data) throws Exception;

}
