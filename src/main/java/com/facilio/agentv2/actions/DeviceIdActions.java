package com.facilio.agentv2.actions;

import com.facilio.agentv2.AgentConstants;
import com.facilio.agentv2.device.FieldDeviceApi;
import com.facilio.agentv2.modbusrtu.ModbusImportUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.net.HttpURLConnection;
import java.util.List;
import java.util.Map;

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
    public String getPenfingPointImports(){
        try{
            List<Map<String, Object>> maps = ModbusImportUtils.getpendingPointsImports(getDeviceId());
            setResult(AgentConstants.DATA,maps);
            ok();
        }catch (Exception e){
            LOGGER.info("Exception occurred while getting pending controller imports for agentId "+getDeviceId()+" ",e);
            setResult(AgentConstants.EXCEPTION,e.getMessage());
            internalError();
        }
        return SUCCESS;
    }
    public String penfingPointImportsCount(){
        try{
            setResult(AgentConstants.DATA,ModbusImportUtils.getPendingPointImportCount(deviceId));
        }catch (Exception e){
            LOGGER.info("Exception occurred while getting pending point imports count for agentId "+deviceId+" ",e);
            setResult(AgentConstants.EXCEPTION,e.getMessage());
            internalError();
        }
        return SUCCESS;
    }
}
