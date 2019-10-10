package com.facilio.agentnew.modbusrtu;

import com.facilio.agent.controller.FacilioControllerType;
import com.facilio.agentnew.AgentConstants;
import com.facilio.agentnew.JsonUtil;
import com.facilio.agentnew.controller.Controller;
import com.facilio.agentnew.point.Point;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;

import java.util.Map;

public class ModbusRtuPoint extends Point {

    private static final Logger LOGGER = LogManager.getLogger(ModbusRtuPoint.class.getName());


    private int offset = -1;
    private int functionCode = -1;
    private Integer modbusDatatype;

    public ModbusRtuPoint(long agentId, long controllerId) {
        super(agentId, controllerId);
    }


    public int getOffset() { return offset; }
    public void setOffset(int offset) { this.offset = offset; }

    public int getFunctionCode() { return functionCode; }
    public void setFunctionCode(int functionCode) { this.functionCode = functionCode; }

    public Integer getModbusDatatype() { return modbusDatatype; }
    public void setModbusDatatype(int modbusDatatype) { this.modbusDatatype = modbusDatatype; }

    @Override
    public FacilioControllerType getControllerType() {
        return FacilioControllerType.MODBUS_RTU;
    }

    public static Point getPointFromMap(Controller controller, Map<String,Object> row) throws Exception {
        return getPointFromMap(controller.getAgentId(),controller.getId(),row);
    }

    public static Point getPointFromMap(long agentId, long controllerId, Map<String,Object> pointMap) throws Exception {
        if (pointMap == null || pointMap.isEmpty()) {
            throw new Exception(" Map for controller can't be null or empty ->" + pointMap);
        }
        if(containsValueCheck(AgentConstants.FUNCTION_CODE,pointMap)
                && containsValueCheck(AgentConstants.MODBUS_DATA_TYPE,pointMap)
                && containsValueCheck(AgentConstants.REGISTER_NUMBER,pointMap)){
            ModbusRtuPoint point = new ModbusRtuPoint(agentId,controllerId);
            point.setFunctionCode(JsonUtil.getInt(pointMap.get(AgentConstants.FUNCTION_CODE)));
            point.setModbusDatatype(JsonUtil.getInt(pointMap.get(AgentConstants.MODBUS_DATA_TYPE)));
            point.setOffset(JsonUtil.getInt(pointMap.get(AgentConstants.REGISTER_NUMBER)));
            return point.getPointFromMap(pointMap);
        }
        throw new Exception(" Mandatory fields like "+AgentConstants.REGISTER_NUMBER+" , "+AgentConstants.MODBUS_DATA_TYPE+" , "+AgentConstants.FUNCTION_CODE+" might be missing form input params -> "+pointMap);
    }


    @Override
    public JSONObject getChildJSON() {
        JSONObject object = new JSONObject();
        if( getId() > 0 ){
            object.put(AgentConstants.ID,getId());
        }
        object.put(AgentConstants.CONTROLLER_ID,getControllerId());
        object.put(AgentConstants.REGISTER_NUMBER,getOffset());
        object.put(AgentConstants.FUNCTION_CODE,getFunctionCode());
        object.put(AgentConstants.MODBUS_DATA_TYPE,getModbusDatatype());
        return object;
    }
}
