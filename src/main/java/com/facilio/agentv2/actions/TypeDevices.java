package com.facilio.agentv2.actions;

import com.facilio.agentv2.AgentConstants;
import com.facilio.agentv2.point.PointsAPI;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

public class TypeDevices extends DeviceIdActions {

    private static final Logger LOGGER = LogManager.getLogger(TypeDevices.class.getName());


    public Integer getControllerType() { return controllerType; }

    public void setControllerType(Integer controllerType) { this.controllerType = controllerType; }

    @NotNull
    @Min(value = 1,message = "controller type can't be less than 1")
    private Integer controllerType;

    /**
     * gets points for a device of a particular type.
     * @return
     */
    public String listDeviceTypePoints(){
        try{
            if(checkValue(getDeviceId()) && (getControllerType()>0)){
                setResult(AgentConstants.DATA, PointsAPI.getDevicePoints(getDeviceId(),getControllerType()));
            }else {
                throw new Exception(" deviceId ->"+getDeviceId());
            }
        }catch (Exception e){
            LOGGER.info("Exception occurred while getting device points",e);
            setResult(AgentConstants.EXCEPTION,e.getMessage());
            setResult(AgentConstants.RESULT,ERROR);
        }
        return SUCCESS;
    }
}
