package com.facilio.agentv2.actions;

import com.facilio.agent.controller.FacilioControllerType;
import com.facilio.agentv2.AgentConstants;
import com.facilio.agentv2.point.PointsAPI;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.List;

public class controllerTypeIdsAction extends IdsAction
{

    private static final Logger LOGGER = LogManager.getLogger(controllerTypeIdsAction.class.getName());


    public Integer getControllerType() {
        return controllerType;
    }

    public void setControllerType(Integer controllerType) {
        this.controllerType = controllerType;
    }

    @NotNull
    @Min(value = 0,message = "controllerType can't be less than 1")
    @Max(99)
    private Integer controllerType;

    public String configureController(){
        try{
            List<Long> pointIds = getRecordIds();
            if( ! pointIds.isEmpty()) {
                PointsAPI.configureControllerAndPoints(pointIds, FacilioControllerType.valueOf(getControllerType()));
                setResult(AgentConstants.RESULT, SUCCESS);
            }
            else {
                throw new Exception(" ids can't be empty");
            }
        }catch (Exception e){
            LOGGER.info("Exception occurred while configuring controller",e);
            setResult(AgentConstants.RESULT,ERROR);
            setResult(AgentConstants.EXCEPTION,e.getMessage());
        }
        return SUCCESS;
    }
}
