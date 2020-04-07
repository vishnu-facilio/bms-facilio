package com.facilio.agentv2.actions;

import com.facilio.agentv2.AgentConstants;
import com.facilio.agentv2.controller.Controller;
import com.facilio.agentv2.iotmessage.AgentMessenger;
import com.facilio.agentv2.modbustcp.ModbusTcpControllerContext;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

public class BulkAddMosbusIpAction extends AgentActionV2 {

    private static final Logger LOGGER = LogManager.getLogger(BulkAddMosbusIpAction.class.getName());


    private List<ModbusTcpControllerContext> controllers;
    @NotNull
    @Min(1)
    private Long agentId;


    public List<ModbusTcpControllerContext> getControllers() { return controllers; }
    public void setControllers(List<ModbusTcpControllerContext> controllers) { this.controllers = controllers; }

    public Long getAgentId() { return agentId; }
    public void setAgentId(Long agentId) { this.agentId = agentId; }

    public String addControllers(){
        try{
            if(controllers != null){
                if(!controllers.isEmpty()){
                    controllers.forEach(controller -> controller.setAgentId(agentId));
                    AgentMessenger.sendConfigureModbusIpControllers(new ArrayList<Controller>(controllers));
                }else {
                    throw new Exception("controllers is empty");
                }
            }else {
                throw new Exception("controllers null");
            }
            ok();
        }catch (Exception e){
            LOGGER.info("Exception while bulk adding modbuscontrollers",e);
            internalError();
            setResult(AgentConstants.EXCEPTION,e.getMessage());
        }
        return SUCCESS;
    }
}
