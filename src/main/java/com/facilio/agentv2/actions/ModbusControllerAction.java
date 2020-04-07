package com.facilio.agentv2.actions;

import com.facilio.agent.controller.FacilioControllerType;
import com.facilio.agentv2.AgentConstants;
import com.facilio.agentv2.controller.ControllerApiV2;
import com.facilio.agentv2.iotmessage.AgentMessenger;
import com.facilio.agentv2.modbusrtu.ModbusRtuControllerContext;
import com.facilio.agentv2.modbustcp.ModbusTcpControllerContext;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import java.net.HttpURLConnection;

public class ModbusControllerAction extends AgentIdAction {

    private static final Logger LOGGER = LogManager.getLogger(ControllerApiV2.class.getName());


    @NotNull
    @Positive
    private Long slaveId;

    @Size(max = 15)
    private String ip;
    @NotNull
    @Positive
    private int controllerType;
    private Long netwokId;

    public int getControllerType() {
        return controllerType;
    }

    public void setControllerType(int controllerType) {
        this.controllerType = controllerType;
    }

    public Long getSlaveId() {
        return slaveId;
    }

    public void setSlaveId(Long slaveId) {
        this.slaveId = slaveId;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public Long getNetwokId() {
        return netwokId;
    }

    public void setNetwokId(Long netwokId) {
        this.netwokId = netwokId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    private String name;


    public String addModbusController() {
        try {
            FacilioControllerType controllerType = FacilioControllerType.valueOf(getControllerType());
            if (controllerType == FacilioControllerType.MODBUS_RTU) {
                ModbusRtuControllerContext controllerContext = new ModbusRtuControllerContext();
                controllerContext.setAgentId(getAgentId());
                controllerContext.setNetworkId(getNetwokId());
                controllerContext.setSlaveId(getSlaveId().intValue());
                controllerContext.setName(name);
                if (AgentMessenger.sendConfigureModbusRtuControllerCommand(controllerContext)) {
                    setResult(AgentConstants.RESULT, SUCCESS);
                    setResponseCode(HttpURLConnection.HTTP_OK);
                    return SUCCESS;
                } else {
                    setResponseCode(HttpURLConnection.HTTP_BAD_REQUEST);
                    setResult(AgentConstants.RESULT, ERROR);
                }
            }
            if (controllerType == FacilioControllerType.MODBUS_IP) {
                ModbusTcpControllerContext controllerContext = new ModbusTcpControllerContext();
                controllerContext.setAgentId(getAgentId());
                controllerContext.setIpAddress(getIp());
                controllerContext.setSlaveId(slaveId.intValue());
                controllerContext.setName(name);
                if (AgentMessenger.sendConfigModbusIpControllerCommand(controllerContext)) {
                    setResult(AgentConstants.RESULT, SUCCESS);
                    setResponseCode(HttpURLConnection.HTTP_OK);
                    return SUCCESS;
                } else {
                    setResponseCode(HttpURLConnection.HTTP_BAD_REQUEST);
                    setResult(AgentConstants.RESULT, ERROR);
                }
            }
        } catch (Exception e) {
            setResponseCode(HttpURLConnection.HTTP_INTERNAL_ERROR);
            setResult(AgentConstants.EXCEPTION, e.getMessage());
            setResult(AgentConstants.RESULT, ERROR);
            LOGGER.info("Exception occurred while sending modbus controller config command ", e);
        }
        return SUCCESS;
    }
}
