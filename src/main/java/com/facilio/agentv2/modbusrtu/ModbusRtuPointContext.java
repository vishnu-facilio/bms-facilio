package com.facilio.agentv2.modbusrtu;

import com.facilio.agent.controller.FacilioControllerType;
import com.facilio.agentv2.AgentConstants;
import com.facilio.agentv2.point.Point;
import com.facilio.modules.FieldUtil;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;

import java.util.Map;

public class ModbusRtuPointContext extends Point {

    private static final Logger LOGGER = LogManager.getLogger(ModbusRtuPointContext.class.getName());


    private long registerNumber = -1;
    private long functionCode = -1;
    private Long modbusDataType ;

    public ModbusRtuPointContext(long agentId, long controllerId) {
        super(agentId, controllerId);
    }
    @Deprecated
    private ModbusRtuPointContext() { }


    public long getRegisterNumber() { return registerNumber; }
    public void setRegisterNumber(long registerNumber) {
        System.out.println("setting rn");
        this.registerNumber = registerNumber; }

    public long getFunctionCode() { return functionCode; }
    public void setFunctionCode(long functionCode) {
        System.out.println("setting fc");
        this.functionCode = functionCode; }

    public Long getModbusDataType() { return modbusDataType; }
    public void setModbusDataType(Long modbusDatatype) {
        System.out.println("setting mdt");
        this.modbusDataType = modbusDatatype; }

    @Override
    public FacilioControllerType getControllerType() {
        return FacilioControllerType.MODBUS_RTU;
    }

    public static Point getPointFromMap(Map<String,Object> pointMap) throws Exception {
        if (pointMap == null || pointMap.isEmpty()) {
            throw new Exception(" Map for controller can't be null or empty ->" + pointMap);
        }
        if(containsValueCheck(AgentConstants.FUNCTION_CODE,pointMap)
                && containsValueCheck(AgentConstants.MODBUS_DATA_TYPE,pointMap)
                && containsValueCheck(AgentConstants.REGISTER_NUMBER,pointMap)){
           /* ModbusRtuPoint point = new ModbusRtuPoint(agentId,controllerId);
            point.setFunctionCode(JsonUtil.getInt(pointMap.get(AgentConstants.FUNCTION_CODE)));
            point.setModbusDatatype(JsonUtil.getInt(pointMap.get(AgentConstants.MODBUS_DATA_TYPE)));
            point.setOffset(JsonUtil.getInt(pointMap.get(AgentConstants.REGISTER_NUMBER)));
            return point.getPointFromMap(pointMap);*/
           JSONObject jsonObject = new JSONObject();
           jsonObject.putAll(pointMap);
           return FieldUtil.getAsBeanFromJson(jsonObject, ModbusRtuPointContext.class);
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
        object.put(AgentConstants.REGISTER_NUMBER, getRegisterNumber());
        object.put(AgentConstants.FUNCTION_CODE,getFunctionCode());
        object.put(AgentConstants.MODBUS_DATA_TYPE,getModbusDataType());
        return object;
    }

    /*public static void main(String[] args) throws IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        JSONObject object = new JSONObject();
        object.put(AgentConstants.MODBUS_DATA_TYPE,3);
        object.put(AgentConstants.FUNCTION_CODE,5);
        object.put(AgentConstants.REGISTER_NUMBER,100);
        System.out.println(FieldUtil.getAsJSON(FieldUtil.getAsBeanFromMap(object, ModbusRtuPoint.class)));
        ModbusRtuPoint point = new ModbusRtuPoint();
        //point.setModbusDatatype(3);
        System.out.println(FieldUtil.getAsJSON(point));
    }*/
}
