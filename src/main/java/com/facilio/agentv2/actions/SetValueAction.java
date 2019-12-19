package com.facilio.agentv2.actions;

import com.facilio.agent.controller.FacilioControllerType;
import com.facilio.agentv2.AgentConstants;
import com.facilio.agentv2.point.PointsAPI;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

public class SetValueAction extends PointIdAction
{

    public int getControllerType() {
        return controllerType;
    }

    public void setControllerType(int controllerType) {
        this.controllerType = controllerType;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    @NotNull
    @Min(1)
    private int controllerType;

    @NotNull
    private Object value;

    public String setValue(){
        try {
            if (PointsAPI.setValue(getPointId(), FacilioControllerType.valueOf(getControllerType()), getValue())) {
                setResult(AgentConstants.RESULT,SUCCESS);
                return SUCCESS;
            }
        }catch (Exception e){
            setResult(AgentConstants.EXCEPTION,e.getMessage());
        }
        setResult(AgentConstants.RESULT,ERROR);
        return SUCCESS;
    }
}
