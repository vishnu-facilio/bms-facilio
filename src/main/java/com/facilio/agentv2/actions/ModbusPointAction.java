package com.facilio.agentv2.actions;

import com.facilio.agent.controller.FacilioControllerType;
import com.facilio.agentv2.iotmessage.ControllerMessenger;
import com.facilio.agentv2.modbusrtu.ModbusRtuPointContext;
import com.facilio.agentv2.modbustcp.ModbusTcpPointContext;
import org.apache.log4j.LogManager;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.net.HttpURLConnection;

public class ModbusPointAction extends AgentIdAction {
    private static final org.apache.log4j.Logger LOGGER = LogManager.getLogger(ModbusPointAction.class.getName());


    @NotNull
    @Min(1)
    private Long controllerId;
    @NotNull
    private Long registerNumber ;
    @NotNull
    @Min(0)
    private Long functionCode ;
    @NotNull
    @Min(0)
    private Long modbusDataType;

    @NotNull
    @Min(0)
    private Integer controllerType;

    public Integer getControllerType() {
        return controllerType;
    }

    public void setControllerType(Integer controllerType) {
        this.controllerType = controllerType;
    }


    public Long getControllerId() {
        return controllerId;
    }

    public void setControllerId(Long controllerId) {
        this.controllerId = controllerId;
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
        try{
        if (getControllerType() == FacilioControllerType.MODBUS_IP.asInt()) {
            ModbusTcpPointContext tcpPointContext = new ModbusTcpPointContext(getAgentId(), getControllerId());
            tcpPointContext.setFunctionCode(functionCode);
            tcpPointContext.setModbusDataType(modbusDataType);
            tcpPointContext.setRegisterNumber(registerNumber);
            ControllerMessenger.sendConfigureModbusTcpPoint(tcpPointContext);
            setResponseCode(HttpURLConnection.HTTP_OK);
            return SUCCESS;
        }else {
            ModbusRtuPointContext rtuPointContext = new ModbusRtuPointContext(getAgentId(),getControllerId());
            rtuPointContext.setFunctionCode(functionCode);
            rtuPointContext.setModbusDataType(modbusDataType);
            rtuPointContext.setRegisterNumber(registerNumber);
            ControllerMessenger.sendConfigureModbusRtuPoint(rtuPointContext);
            setResponseCode(HttpURLConnection.HTTP_OK);
            return SUCCESS;
        }
    }catch (Exception e){
            LOGGER.info(    "Exception while sending configure modbus point command",e);
        setResponseCode(HttpURLConnection.HTTP_INTERNAL_ERROR);
        }
        return SUCCESS;
    }

}
