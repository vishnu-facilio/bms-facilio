package com.facilio.agentv2.rdm;

import com.facilio.agent.controller.FacilioControllerType;
import com.facilio.agentv2.AgentConstants;
import com.facilio.agentv2.point.Point;
import com.facilio.modules.FieldUtil;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;

import java.util.Map;

public class RdmPointContext extends Point {

    private static final Logger LOGGER = LogManager.getLogger(RdmPointContext.class.getName());

    private String path;

    public JSONObject getDetails() {
        return details;
    }

    public void setDetails(JSONObject details) {
        this.details = details;
    }

    private JSONObject details;

    public String getRdmPointClass() {
        return rdmPointClass;
    }

    public void setRdmPointClass(String rdmPointClass) {
        this.rdmPointClass = rdmPointClass;
    }

    private String rdmPointClass;


    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    @Override
    public FacilioControllerType getControllerType() {
        return FacilioControllerType.RDM;
    }

    public RdmPointContext(long agentId, long controllerId) {
        super(agentId, controllerId);
    }

    @Deprecated
    public RdmPointContext() {
    }

    @Override
    public String getIdentifier() {
        return getPath();
    }

    public static RdmPointContext getPointFromMap(Map<String, Object> pointMap) throws Exception {
        if (pointMap == null || pointMap.isEmpty()) {
            throw new Exception(" Map for controller can't be null or empty ->" + pointMap);
        }
        if (containsValueCheck(AgentConstants.PATH, pointMap)) {
           /* RdmPoint point = new RdmPoint(agentId,controllerId);
            point.setPath(((String)pointMap.get(AgentConstants.PATH)));
            return (RdmPoint) point.getPointFromMap(pointMap);*/
            JSONObject jsonObject = new JSONObject();
            jsonObject.putAll(pointMap);
            return FieldUtil.getAsBeanFromJson(jsonObject, RdmPointContext.class);
        }
        throw new Exception("Mandatory fields like " + AgentConstants.PATH + " might be missing from the input parameter -> " + pointMap);
    }

    @Override
    public JSONObject getChildJSON() {
        JSONObject childPointJSON = new JSONObject();
        childPointJSON.put(AgentConstants.ID, getId());
        childPointJSON.put(AgentConstants.DEVICE_ID, getDeviceId());
        childPointJSON.put(AgentConstants.PATH, getPath());
        childPointJSON.put(AgentConstants.RDM_POINT_CLASS, getRdmPointClass());
        childPointJSON.put(AgentConstants.CONTROLLER_ID, getControllerId());
        childPointJSON.put(AgentConstants.DETAILS, getDetails());
        return childPointJSON;
    }
}
