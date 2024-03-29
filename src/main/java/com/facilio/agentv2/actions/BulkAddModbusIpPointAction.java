package com.facilio.agentv2.actions;

import com.facilio.agentv2.AgentConstants;
import com.facilio.agentv2.controller.Controller;
import com.facilio.agentv2.controller.GetControllerRequest;
import com.facilio.agentv2.iotmessage.ControllerMessenger;
import com.facilio.agentv2.modbustcp.ModbusTcpPointContext;
import org.apache.log4j.LogManager;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class BulkAddModbusIpPointAction extends AgentActionV2
{

    private static final org.apache.log4j.Logger LOGGER = LogManager.getLogger(BulkAddModbusIpPointAction.class.getName());


    @NotNull
    private Long controllerId;
    @NotNull
    List<ModbusTcpPointContext> points;


    public Long getControllerId() {
        return controllerId;
    }

    public void setControllerId(Long controllerId) {
        this.controllerId = controllerId;
    }

    public List<ModbusTcpPointContext> getPoints() { return points; }
    public void setPoints(List<ModbusTcpPointContext> points) { this.points = points; }

    public String addPoints(){
        try{
            GetControllerRequest getControllerRequest = new GetControllerRequest()
                    .withControllerId(controllerId);
            Controller controller = getControllerRequest.getController();
            Objects.requireNonNull(controller);
            LOGGER.info(points);
            LOGGER.info("points size "+points);
            for (ModbusTcpPointContext point : points) {
                point.setControllerId(controller.getId());
            }
            ControllerMessenger.sendConfigureModbusTcpPoints(controller,new ArrayList<>(points));
            ok();
        }catch (Exception e){
         LOGGER.info("Exception while adding points",e);
         internalError();
         setResult(AgentConstants.EXCEPTION,e.getMessage());
        }
        return SUCCESS;
    }
}
