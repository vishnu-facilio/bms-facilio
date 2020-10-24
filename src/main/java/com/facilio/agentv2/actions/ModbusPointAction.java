package com.facilio.agentv2.actions;

import com.facilio.agent.controller.FacilioControllerType;
import com.facilio.agentv2.AgentConstants;
import com.facilio.agentv2.controller.Controller;
import com.facilio.agentv2.controller.GetControllerRequest;
import com.facilio.agentv2.device.Device;
import com.facilio.agentv2.device.FieldDeviceApi;
import com.facilio.agentv2.iotmessage.ControllerMessenger;
import com.facilio.agentv2.modbusrtu.ModbusRtuPointContext;
import com.facilio.agentv2.modbustcp.ModbusTcpPointContext;
import org.apache.log4j.LogManager;
import org.json.simple.JSONObject;

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
            Device device = FieldDeviceApi.getDevice(getDeviceId());
            if (device != null) {
                GetControllerRequest getControllerRequest = new GetControllerRequest()
                        .forDevice(getDeviceId()).withAgentId(device.getAgentId());
                if (getControllerType() == FacilioControllerType.MODBUS_IP.asInt()) {
                    Controller controller = getControllerRequest.ofType(FacilioControllerType.MODBUS_IP).getController();
                    Objects.requireNonNull(controller, "controller can't be null");
                    ModbusTcpPointContext tcpPointContext = new ModbusTcpPointContext(-1, controller.getId());
                    tcpPointContext.setRegisterType(registerType);
                    tcpPointContext.setModbusDataType(modbusDataType);
                    tcpPointContext.setRegisterNumber(registerNumber);
                    tcpPointContext.setName(name);
                    ControllerMessenger.sendConfigureModbusTcpPoint(tcpPointContext);
                    setResponseCode(HttpURLConnection.HTTP_OK);
                    return SUCCESS;
                } else {
                    JSONObject controllerJSON = (JSONObject) device.getControllerProps().get(AgentConstants.CONTROLLER);
                    Controller controller = getControllerRequest.withControllerProperties(controllerJSON, FacilioControllerType.MODBUS_RTU).getController();
                    Objects.requireNonNull(controller, "controller can't be null");
                    ModbusRtuPointContext rtuPointContext = new ModbusRtuPointContext(-1, controller.getId());
                    rtuPointContext.setRegisterType(registerType);
                    rtuPointContext.setModbusDataType(modbusDataType);
                    rtuPointContext.setRegisterNumber(registerNumber);
                    rtuPointContext.setName(name);
                    ControllerMessenger.sendConfigureModbusRtuPoint(rtuPointContext);
                    setResponseCode(HttpURLConnection.HTTP_OK);
                    return SUCCESS;
                }
            } else {
                LOGGER.info("Device is null");
                return ERROR;
            }
        } catch (Exception e) {
            LOGGER.info("Exception while sending configure modbus point command", e);
            internalError();
        }
        return SUCCESS;
    }

}
