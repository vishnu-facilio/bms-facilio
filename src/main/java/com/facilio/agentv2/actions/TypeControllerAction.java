package com.facilio.agentv2.actions;

import com.facilio.agent.controller.FacilioControllerType;
import com.facilio.agentv2.AgentConstants;
import com.facilio.agentv2.controller.Controller;
import com.facilio.agentv2.controller.ControllerApiV2;
import com.facilio.agentv2.point.PointsAPI;
import com.facilio.modules.FieldUtil;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

public class TypeControllerAction extends ControllerActions
{
    private static final Logger LOGGER = LogManager.getLogger(TypeControllerAction.class.getName());


    public Integer getControllerType() { return controllerType; }

    public void setControllerType(Integer controllerType) { this.controllerType = controllerType; }

    @NotNull
    @Min(value = 0,message = "Controller type can't be less than 1")
    private Integer controllerType;

    public String getControllerUsingIdType() {
        try {
                Controller controller = ControllerApiV2.getControllerIdType(getControllerId(), FacilioControllerType.valueOf(getControllerType()));
                if (controller != null) {
                    try {
                        setResult(AgentConstants.RESULT, SUCCESS);
                        setResult(AgentConstants.DATA, FieldUtil.getAsJSON(controller));
                    } catch (Exception e) {
                        LOGGER.info(" Exception occurred ", e);
                        setResult(AgentConstants.EXCEPTION, e.getMessage());
                        setResult(AgentConstants.RESULT, ERROR);
                    }
                }

        }catch (Exception e){
            LOGGER.info("Exception occurred while getting controller using id and type ",e);
            setResult(AgentConstants.EXCEPTION,e.getMessage());
            setResult(AgentConstants.RESULT,ERROR);
        }
        return SUCCESS;
    }

    /**
     * Lists all points for a controller
     * @return
     */
    public String getControllerPoints(){ //TODO use pointId
        try {
                setResult(AgentConstants.DATA, PointsAPI.getControllerPointsData(FacilioControllerType.valueOf(getControllerType()), getControllerId()));
                setResult(AgentConstants.RESULT, SUCCESS);
                return SUCCESS;
        }catch (Exception e){
            LOGGER.info("Exception occurred while getting point",e);
            setResult(AgentConstants.EXCEPTION,e.getMessage());
        }
        return SUCCESS;
    }
}
