package com.facilio.agentnew.opcua;

import com.facilio.agent.controller.FacilioControllerType;
import com.facilio.agentnew.AgentConstants;
import com.facilio.agentnew.JsonUtil;
import com.facilio.agentnew.controller.Controller;
import com.facilio.agentnew.point.Point;
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

    public static Point getPointFromMap(long agentId, long controllerId, Map<String,Object> pointMap) throws Exception {
        if (pointMap == null || pointMap.isEmpty()) {
            throw new Exception(" Map for controller can't be null or empty ->" + pointMap);
        }
        OpcUaPoint point = new OpcUaPoint(agentId,controllerId);
        if(containsValueCheck(AgentConstants.NAMESPACE, pointMap) && containsValueCheck(AgentConstants.IDENTIFIER, pointMap)){
            point.setNamespace(JsonUtil.getInt(pointMap.get(AgentConstants.NAMESPACE)));
            point.setIdentifier((String)pointMap.get(AgentConstants.IDENTIFIER));
            return point.getPointFromMap(pointMap);
        }
        throw  new  Exception("Mandatory fields like "+AgentConstants.NAMESPACE+" , "+AgentConstants.IDENTIFIER+"  might be missing from input parameter -> "+pointMap);
    }

    public static Point getPointFromMap(Controller controller, Map<String,Object> row) throws Exception {
        return getPointFromMap(controller.getAgentId(),controller.getId(),row);
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
