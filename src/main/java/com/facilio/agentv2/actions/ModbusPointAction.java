package com.facilio.agentv2.actions;

import com.facilio.agent.controller.FacilioControllerType;
import com.facilio.agentv2.controller.Controller;
import com.facilio.agentv2.controller.GetControllerRequest;
import com.facilio.agentv2.device.Device;
import com.facilio.agentv2.device.FieldDeviceApi;
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
    private Long functionCode;
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

    public Long getFunctionCode() {
        return functionCode;
    }

    public void setFunctionCode(Long functionCode) {
        this.functionCode = functionCode;
    }

    public Long getModbusDataType() {
        return modbusDataType;
    }

    public void setModbusDataType(Long modbusDataType) {
        this.modbusDataType = modbusDataType;
    }



    public String createModbusPoint() {
        try {
            GetControllerRequest getControllerRequest = new GetControllerRequest()
                    .forDevice(getDeviceId());
            if (getControllerType() == FacilioControllerType.MODBUS_IP.asInt()) {
                Controller controller = getControllerRequest.ofType(FacilioControllerType.MODBUS_IP).getController();
                Objects.requireNonNull(controller,"controller can't be null");
                ModbusTcpPointContext tcpPointContext = new ModbusTcpPointContext( -1,controller.getId());
                tcpPointContext.setFunctionCode(functionCode);
                tcpPointContext.setModbusDataType(modbusDataType);
                tcpPointContext.setRegisterNumber(registerNumber);
                tcpPointContext.setName(name);
                ControllerMessenger.sendConfigureModbusTcpPoint(tcpPointContext);
                setResponseCode(HttpURLConnection.HTTP_OK);
                return SUCCESS;
            } else {
                Controller controller = getControllerRequest.ofType(FacilioControllerType.MODBUS_RTU).getController();
                Objects.requireNonNull(controller,"controller can't be null");
                ModbusRtuPointContext rtuPointContext = new ModbusRtuPointContext(-1, controller.getId());
                rtuPointContext.setFunctionCode(functionCode);
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
