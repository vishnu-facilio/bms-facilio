package com.facilio.agentv2.actions;

import com.facilio.agentv2.AgentConstants;
import com.facilio.agentv2.device.FieldDeviceApi;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.net.HttpURLConnection;

public class DeviceIdActions extends AgentActionV2 {

    private static final Logger LOGGER = LogManager.getLogger(DeviceIdActions.class.getName());


    public Long getDeviceId() { return deviceId; }

    public void setDeviceId(Long deviceId) { this.deviceId = deviceId; }

    private Long deviceId;

    public String device(){
        try {
            setResult(AgentConstants.RESULT, FieldDeviceApi.getDevice(getDeviceId()));
        }catch (Exception e){
            LOGGER.info("Exception occurred while getting devices");
            setResult(AgentConstants.RESULT,ERROR);
            setResult(AgentConstants.EXCEPTION,e.getMessage());
            setResponseCode(HttpURLConnection.HTTP_OK);
        }
        return SUCCESS;
    }
}
