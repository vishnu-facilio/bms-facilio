package com.facilio.agentnew.opcxmlda;

import com.facilio.agent.controller.FacilioControllerType;
import com.facilio.agentnew.AgentConstants;
import com.facilio.agentnew.controller.Controller;
import com.facilio.agentnew.point.Point;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;

import java.util.Map;
public class OpcXmlDaPoint extends Point {

    private static final Logger LOGGER = LogManager.getLogger(OpcXmlDaPoint.class.getName());

    private String path;

    public String getPath() { return path; }
    public void setPath(String path) {
        this.path = path;
    }

    @Override
    public FacilioControllerType getControllerType() {
        return FacilioControllerType.OPC_XML_DA;
    }

    public OpcXmlDaPoint(long agentId, long controllerId) {
        super(agentId, controllerId);
    }

    public static OpcXmlDaPoint getPointFromMap(long agentId, long controllerId, Map<String,Object> pointMap) throws Exception {
        if (pointMap == null || pointMap.isEmpty()) {
            throw new Exception(" Map for controller can't be null or empty ->" + pointMap);
        }
        if(containsValueCheck(AgentConstants.PATH,pointMap)){
            OpcXmlDaPoint point = new OpcXmlDaPoint(agentId,controllerId);
            point.setPath(((String)pointMap.get(AgentConstants.PATH)));
            return (OpcXmlDaPoint) point.getPointFromMap(pointMap);
        }
        throw new Exception("Mandatory fields like "+AgentConstants.PATH+" might be missing from the input parameter -> "+pointMap);
    }

    public static Point getPointFromMap(Controller controller, Map<String,Object> row) throws Exception {
        return getPointFromMap(controller.getAgentId(),controller.getId(),row);
    }

    @Override
    public JSONObject getChildJSON() {
        JSONObject childPointJSON = new JSONObject();
        childPointJSON.put(AgentConstants.ID, getId());
        childPointJSON.put(AgentConstants.CONTROLLER_ID,getControllerId());
        childPointJSON.put(AgentConstants.PATH, getPath());
        return childPointJSON;
    }
}
