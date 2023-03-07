package com.facilio.agentv2.actions;

import com.facilio.agent.controller.FacilioControllerType;
import com.facilio.agentv2.controller.Controller;
import com.facilio.agentv2.controller.ControllerApiV2;
import com.facilio.agentv2.iotmessage.ControllerMessenger;
import com.facilio.agentv2.modbusrtu.ModbusRtuPointContext;
import com.facilio.agentv2.modbustcp.ModbusTcpPointContext;
import org.apache.log4j.LogManager;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.net.HttpURLConnection;
import java.util.Objects;

public class ModbusPointAction extends DeviceIdActions {
    private static final org.apache.log4j.Logger LOGGER = LogManager.getLogger(ModbusPointAction.class.getName());



    @NotNull
    private Long registerNumber;
    @NotNull
    @Min(0)
    private Long registerType;
    @NotNull
    @Min(0)
    private Long modbusDataType;

    @NotNull
    @Min(0)
    private Integer controllerType;

    @NotNull
    private String name;

    private Long controllerId;

    private Long agentId = -1L;

    private int interval = 0;

    public int getInterval() {
        return interval;
    }

    public void setInterval(int interval) {
        this.interval = interval;
    }

    public Long getAgentId () {
        return agentId;
    }

    public void setAgentId ( Long agentId ) {
        this.agentId = agentId;
    }

    public Long getControllerId () {
        return controllerId;
    }
    public void setControllerId ( Long controllerId ) {
        this.controllerId = controllerId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getControllerType() {
        return controllerType;
    }

    public void setControllerType(Integer controllerType) {
        this.controllerType = controllerType;
    }

    public Long getRegisterNumber() {
        return registerNumber;
    }

    public void setRegisterNumber(Long registerNumber) {
        this.registerNumber = registerNumber;
    }

    public Long getRegisterType() {
        return registerType;
    }

    public void setRegisterType(Long registerType) {
        this.registerType = registerType;
    }

    public Long getModbusDataType() {
        return modbusDataType;
    }

    public void setModbusDataType(Long modbusDataType) {
        this.modbusDataType = modbusDataType;
    }



    public String createModbusPoint() {
        try {
                Controller controller = ControllerApiV2.getController(controllerId,getAgentId());
                Objects.requireNonNull(controller, "controller can't be null");
                if (getControllerType() == FacilioControllerType.MODBUS_IP.asInt()) {
                    ModbusTcpPointContext tcpPointContext = new ModbusTcpPointContext(getAgentId(), controller.getId());
                    tcpPointContext.setRegisterType(registerType);
                    tcpPointContext.setModbusDataType(modbusDataType);
                    tcpPointContext.setRegisterNumber(registerNumber);
                    tcpPointContext.setName(name);
                    ControllerMessenger.sendAddModbusTcpPoint(tcpPointContext, interval);
                    setResponseCode(HttpURLConnection.HTTP_OK);
                    return SUCCESS;
                } else {
                    ModbusRtuPointContext rtuPointContext = new ModbusRtuPointContext(-1, controller.getId());
                    rtuPointContext.setRegisterType(registerType);
                    rtuPointContext.setModbusDataType(modbusDataType);
                    rtuPointContext.setRegisterNumber(registerNumber);
                    rtuPointContext.setName(name);
                    ControllerMessenger.sendConfigureModbusRtuPoint(rtuPointContext);
                    setResponseCode(HttpURLConnection.HTTP_OK);
                    return SUCCESS;
                }
        } catch (Exception e) {
            LOGGER.info("Exception while sending configure modbus point command", e);
            internalError();
        }
        return SUCCESS;
    }

}
