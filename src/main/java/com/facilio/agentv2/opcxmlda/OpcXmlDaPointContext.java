package com.facilio.agentv2.opcxmlda;

import com.facilio.agent.controller.FacilioControllerType;
import com.facilio.agentv2.AgentConstants;
import com.facilio.agentv2.point.Point;
import com.facilio.modules.FieldUtil;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;

import java.util.Map;
public class  OpcXmlDaPointContext extends Point {

    private static final Logger LOGGER = LogManager.getLogger(OpcXmlDaPointContext.class.getName());

    private String path;

    public String getPath() { return path; }
    public void setPath(String path) {
        this.path = path;
    }

    @Override
    public FacilioControllerType getControllerType() {
        return FacilioControllerType.OPC_XML_DA;
    }

    public OpcXmlDaPointContext(long agentId, long controllerId) {
        super(agentId, controllerId);
    }
    @Deprecated
    public OpcXmlDaPointContext() { }

    @Override
    public String getIdentifier() {
        return null;
    }

    public static OpcXmlDaPointContext getPointFromMap(Map<String,Object> pointMap) throws Exception {
        if (pointMap == null || pointMap.isEmpty()) {
            throw new Exception(" Map for controller can't be null or empty ->" + pointMap);
        }
        if(containsValueCheck(AgentConstants.PATH,pointMap)){
           /* OpcXmlDaPoint point = new OpcXmlDaPoint(agentId,controllerId);
            point.setPath(((String)pointMap.get(AgentConstants.PATH)));
            return (OpcXmlDaPoint) point.getPointFromMap(pointMap);*/
           JSONObject jsonObject = new JSONObject();
           jsonObject.putAll(pointMap);
           return FieldUtil.getAsBeanFromJson(jsonObject, OpcXmlDaPointContext.class);
        }
        throw new Exception("Mandatory fields like "+AgentConstants.PATH+" might be missing from the input parameter -> "+pointMap);
    }

    @Override
    public JSONObject getChildJSON() {
        JSONObject childPointJSON = new JSONObject();
        childPointJSON.put(AgentConstants.ID, getId());
        childPointJSON.put(AgentConstants.DEVICE_ID,getDeviceId());
        childPointJSON.put(AgentConstants.PATH, getPath());
        childPointJSON.put(AgentConstants.CONTROLLER_ID, getControllerId());
        return childPointJSON;
    }
}
