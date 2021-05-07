package com.facilio.agentv2.rdm;

import com.facilio.agent.controller.FacilioControllerType;
import com.facilio.agentv2.AgentConstants;
import com.facilio.agentv2.point.Point;
import com.facilio.modules.FieldUtil;
import org.apache.kafka.common.protocol.types.Field;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

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
            RdmPointContext point = new RdmPointContext();

            if (jsonObject.containsKey("controllerId")) {
                point.setControllerId((Long) jsonObject.get("controllerId"));
            }
            if (jsonObject.containsKey("thresholdJson")) {
                point.setThresholdJSON(jsonObject.get("thresholdJson").toString());
            }
            if (jsonObject.containsKey("dataType")) {
                point.setDataType((Integer) jsonObject.get("dataType"));
            }
            if (jsonObject.containsKey("pointType")) {
                point.setPointType((Integer) jsonObject.get("pointType"));
            }
            if (jsonObject.containsKey("deviceName")) {
                point.setDeviceName(jsonObject.get("deviceName").toString());
            }
            if (jsonObject.containsKey("deviceId")) {
                point.setDeviceId((Long) jsonObject.get("deviceId"));
            }
            if (jsonObject.containsKey("logical")) {
                point.setLogical((Boolean) jsonObject.get("logical"));
            }
            if (jsonObject.containsKey("orgId")) {
                point.setOrgId((Long) jsonObject.get("orgId"));
            }
            if (jsonObject.containsKey("writable")) {
                point.setWritable((Boolean) jsonObject.get("writable"));
            }
            if (jsonObject.containsKey("agentWritable")) {
                point.setAgentWritable((Boolean) jsonObject.get("agentWritable"));
            }
            if (jsonObject.containsKey("configureStatus")) {
                point.setConfigureStatus((Integer) jsonObject.get("configureStatus"));
            }
            point.setPath(jsonObject.get("path").toString());
            point.setRdmPointClass(jsonObject.get("rdmPointClass").toString());
            point.setDetails((JSONObject) new JSONParser().parse(jsonObject.get("details").toString()));
            return point;
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
