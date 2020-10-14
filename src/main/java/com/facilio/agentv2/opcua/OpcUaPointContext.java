package com.facilio.agentv2.opcua;

import com.facilio.agent.controller.FacilioControllerType;
import com.facilio.agentv2.AgentConstants;
import com.facilio.agentv2.point.Point;
import com.facilio.modules.FieldUtil;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;

import java.util.Map;

public class OpcUaPointContext extends Point {

    private static final Logger LOGGER = LogManager.getLogger(OpcUaPointContext.class.getName());


    private int namespace;
    private String uaPointIdentifier;

    public int getNamespace() {
        return namespace;
    }
    public void setNamespace(int namespace) {
        this.namespace = namespace;
    }

    public String getIdentifier() {
        return null;
    }

    public String getUaPointIdentifier() {
        return uaPointIdentifier;
    }

    public void setUaPointIdentifier(String identifier) {
        this.uaPointIdentifier = identifier;
    }

    @Override
    public FacilioControllerType getControllerType() {
        return FacilioControllerType.OPC_UA;
    }

    public OpcUaPointContext(long agentId, long controllerId) {
        super(agentId, controllerId);
    }
    @Deprecated
    public OpcUaPointContext() { }

    public static Point getPointFromMap( Map<String,Object> pointMap) throws Exception {
        if (pointMap == null || pointMap.isEmpty()) {
            throw new Exception(" Map for controller can't be null or empty ->" + pointMap);
        }
        if (containsValueCheck(AgentConstants.NAMESPACE, pointMap) && containsValueCheck(AgentConstants.UA_POINT_IDENTIFIER, pointMap)) { //namespace
           /* OpcUaPoint point = new OpcUaPoint(agentId,controllerId);
            point.setNamespace(JsonUtil.getInt(pointMap.get(AgentConstants.NAMESPACE)));
            point.setIdentifier((String)pointMap.get(AgentConstants.IDENTIFIER));
            return point.getPointFromMap(pointMap);*/
           JSONObject jsonObject = new JSONObject();
           jsonObject.putAll(pointMap);
           return FieldUtil.getAsBeanFromJson(jsonObject, OpcUaPointContext.class);
        }
        throw new Exception("Mandatory fields like " + AgentConstants.NAMESPACE + " , " + AgentConstants.UA_POINT_IDENTIFIER + "  might be missing from input parameter -> " + pointMap);
    }

    @Override
    public JSONObject getChildJSON() {
        JSONObject opcUsPointJSON = new JSONObject();
        opcUsPointJSON.put(AgentConstants.ID,this.getId());
        opcUsPointJSON.put(AgentConstants.DEVICE_ID,getDeviceId());
        opcUsPointJSON.put(AgentConstants.NAMESPACE,this.getNamespace());
        opcUsPointJSON.put(AgentConstants.UA_POINT_IDENTIFIER, this.getUaPointIdentifier());
        return opcUsPointJSON;
    }

}
