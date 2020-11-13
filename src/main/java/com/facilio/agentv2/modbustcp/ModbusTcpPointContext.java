package com.facilio.agentv2.modbustcp;

import com.facilio.agent.controller.FacilioControllerType;
import com.facilio.agentv2.AgentConstants;
import com.facilio.agentv2.point.Point;
import com.facilio.modules.FieldUtil;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;

import javax.validation.constraints.NotNull;
import java.util.Map;

public class ModbusTcpPointContext extends Point {

    private static final Logger LOGGER = LogManager.getLogger(ModbusTcpPointContext.class.getName());

    public ModbusTcpPointContext(long agentId, long controllerId) {
        super(agentId, controllerId);
    }
    @Deprecated
    public ModbusTcpPointContext() { }

    @Override
    public String getIdentifier() {
        return null;
    }


    private long registerNumber = -1;
    private long registerType = -1;
    @NotNull
    private Long modbusDataType ;

    public long getRegisterNumber() { return registerNumber; }
    public void setRegisterNumber(long registerNumber) { this.registerNumber = registerNumber; }

    public long getRegisterType() { return registerType; }
    public void setRegisterType(long registerType) { this.registerType = registerType; }

    public Long getModbusDataType() { return modbusDataType; }
    public void setModbusDataType(Long modbusDataType) { this.modbusDataType = modbusDataType; }

    @Override
    public FacilioControllerType getControllerType() {
        return FacilioControllerType.MODBUS_IP;
    }

    public static Point getPointFromMap(Map<String,Object> pointMap) throws Exception {
        if (pointMap == null || pointMap.isEmpty()) {
            throw new Exception(" Map for controller can't be null or empty ->" + pointMap);
        }
        if(containsValueCheck(AgentConstants.REGISTER_TYPE,pointMap)
                && containsValueCheck(AgentConstants.MODBUS_DATA_TYPE,pointMap)
                && containsValueCheck(AgentConstants.REGISTER_NUMBER,pointMap)){
           /* ModbusTcpPoint point = new ModbusTcpPoint(agentId,controllerId);
            point.setFunctionCode(JsonUtil.getInt(pointMap.get(AgentConstants.FUNCTION_CODE)));
            point.setModbusDatatype(JsonUtil.getInt(pointMap.get(AgentConstants.MODBUS_DATA_TYPE)));
            point.setOffset(JsonUtil.getInt(pointMap.get(AgentConstants.REGISTER_NUMBER)));
            return point.getPointFromMap(pointMap);*/
            JSONObject jsonObject = new JSONObject();
            jsonObject.putAll(pointMap);
            return FieldUtil.getAsBeanFromJson(jsonObject, ModbusTcpPointContext.class);
        }
        throw new Exception(" Mandatory fields like "+AgentConstants.REGISTER_NUMBER+" , "+AgentConstants.MODBUS_DATA_TYPE+" , "+AgentConstants.REGISTER_TYPE +" might be missing form input params -> "+pointMap);
    }


    @Override
    public JSONObject getChildJSON() {
        JSONObject object = new JSONObject();
        object.put(AgentConstants.ID,getId());
        object.put(AgentConstants.DEVICE_ID,getDeviceId());
        object.put(AgentConstants.CONTROLLER_ID, getControllerId());
        object.put(AgentConstants.REGISTER_NUMBER, getRegisterNumber());
        object.put(AgentConstants.REGISTER_TYPE, getRegisterType());
        object.put(AgentConstants.MODBUS_DATA_TYPE, getModbusDataType());
        return object;
    }
}
