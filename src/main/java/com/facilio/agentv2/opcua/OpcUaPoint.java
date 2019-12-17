package com.facilio.agentv2.opcua;

import com.facilio.agent.controller.FacilioControllerType;
import com.facilio.agentv2.AgentConstants;
import com.facilio.agentv2.point.Point;
import com.facilio.modules.FieldUtil;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;

import java.util.Map;

public class OpcUaPoint extends Point {

    private static final Logger LOGGER = LogManager.getLogger(OpcUaPoint.class.getName());


    private int namespace;
    private String identifier;

    public int getNamespace() {
        return namespace;
    }
    public void setNamespace(int namespace) {
        this.namespace = namespace;
    }

    public String getIdentifier() {
        return identifier;
    }
    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    @Override
    public FacilioControllerType getControllerType() {
        return FacilioControllerType.OPC_UA;
    }

    public OpcUaPoint(long agentId, long controllerId) {
        super(agentId, controllerId);
    }
    @Deprecated
    public OpcUaPoint() { }

    public static Point getPointFromMap( Map<String,Object> pointMap) throws Exception {
        if (pointMap == null || pointMap.isEmpty()) {
            throw new Exception(" Map for controller can't be null or empty ->" + pointMap);
        }
        if(containsValueCheck(AgentConstants.NAMESPACE, pointMap) && containsValueCheck(AgentConstants.IDENTIFIER, pointMap)){ //namespace
           /* OpcUaPoint point = new OpcUaPoint(agentId,controllerId);
            point.setNamespace(JsonUtil.getInt(pointMap.get(AgentConstants.NAMESPACE)));
            point.setIdentifier((String)pointMap.get(AgentConstants.IDENTIFIER));
            return point.getPointFromMap(pointMap);*/
           JSONObject jsonObject = new JSONObject();
           jsonObject.putAll(pointMap);
           return FieldUtil.getAsBeanFromJson(jsonObject,OpcUaPoint.class);
        }
        throw  new  Exception("Mandatory fields like "+AgentConstants.NAMESPACE+" , "+AgentConstants.IDENTIFIER+"  might be missing from input parameter -> "+pointMap);
    }

    @Override
    public JSONObject getChildJSON() {
        JSONObject opcUsPointJSON = new JSONObject();
        opcUsPointJSON.put(AgentConstants.ID,this.getId());
        opcUsPointJSON.put(AgentConstants.CONTROLLER_ID,getControllerId());
        opcUsPointJSON.put(AgentConstants.NAMESPACE,this.getNamespace());
        opcUsPointJSON.put(AgentConstants.IDENTIFIER,this.getIdentifier());
        return opcUsPointJSON;
    }

}
