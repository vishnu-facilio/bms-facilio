package com.facilio.agentv2.modbustcp;

import com.facilio.agent.controller.FacilioControllerType;
import com.facilio.agentv2.AgentConstants;
import com.facilio.agentv2.controller.Controller;
import com.facilio.agentv2.point.Point;
import com.facilio.modules.FieldUtil;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;

import java.util.Map;

public class ModbusTcpPoint extends Point {

    private static final Logger LOGGER = LogManager.getLogger(ModbusTcpPoint.class.getName());

    public ModbusTcpPoint(long agentId, long controllerId) {
        super(agentId, controllerId);
    }
    @Deprecated
    private ModbusTcpPoint() { }

    private int offset = -1;
    private int functionCode = -1;
    private Integer modbusDatatype = -1;

    public int getOffset() { return offset; }
    public void setOffset(int offset) { this.offset = offset; }

    public int getFunctionCode() { return functionCode; }
    public void setFunctionCode(int functionCode) { this.functionCode = functionCode; }

    public Integer getModbusDatatype() { return modbusDatatype; }
    public void setModbusDatatype(int modbusDatatype) { this.modbusDatatype = modbusDatatype; }

    @Override
    public FacilioControllerType getControllerType() {
        return FacilioControllerType.MODBUS_IP;
    }

    public static Point getPointFromMap(long agentId, long controllerId, Map<String,Object> pointMap) throws Exception {
        if (pointMap == null || pointMap.isEmpty()) {
            throw new Exception(" Map for controller can't be null or empty ->" + pointMap);
        }
        if(containsValueCheck(AgentConstants.FUNCTION_CODE,pointMap)
                && containsValueCheck(AgentConstants.MODBUS_DATA_TYPE,pointMap)
                && containsValueCheck(AgentConstants.REGISTER_NUMBER,pointMap)){
           /* ModbusTcpPoint point = new ModbusTcpPoint(agentId,controllerId);
            point.setFunctionCode(JsonUtil.getInt(pointMap.get(AgentConstants.FUNCTION_CODE)));
            point.setModbusDatatype(JsonUtil.getInt(pointMap.get(AgentConstants.MODBUS_DATA_TYPE)));
            point.setOffset(JsonUtil.getInt(pointMap.get(AgentConstants.REGISTER_NUMBER)));
            return point.getPointFromMap(pointMap);*/
            JSONObject jsonObject = new JSONObject();
            jsonObject.putAll(pointMap);
            return FieldUtil.getAsBeanFromJson(jsonObject,ModbusTcpPoint.class);
        }
        throw new Exception(" Mandatory fields like "+AgentConstants.REGISTER_NUMBER+" , "+AgentConstants.MODBUS_DATA_TYPE+" , "+AgentConstants.FUNCTION_CODE+" might be missing form input params -> "+pointMap);
    }

    public static Point getPointFromMap(Controller controller, Map<String,Object> row) throws Exception {
        return getPointFromMap(controller.getAgentId(),controller.getId(),row);
    }


    @Override
    public JSONObject getChildJSON() {
        JSONObject object = new JSONObject();
        object.put(AgentConstants.ID,getId());
        object.put(AgentConstants.CONTROLLER_ID,getControllerId());
        object.put(AgentConstants.REGISTER_NUMBER,getOffset());
        object.put(AgentConstants.FUNCTION_CODE,getFunctionCode());
        object.put(AgentConstants.MODBUS_DATA_TYPE,getModbusDatatype());
        return object;
    }
}
