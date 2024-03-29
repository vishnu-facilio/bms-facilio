package com.facilio.agentv2.misc;

import com.facilio.agent.controller.FacilioControllerType;
import com.facilio.agentv2.AgentConstants;
import com.facilio.agentv2.JsonUtil;
import com.facilio.agentv2.point.Point;
import org.json.simple.JSONObject;

import java.io.Serializable;
import java.util.Map;

public class MiscPoint extends Point  implements Serializable {

    private String path;
    public MiscPoint(long agentId){
        super(agentId);
    }
    public MiscPoint(long agentId, long controllerId) {
        super(agentId, controllerId);
    }
    @Deprecated
    public MiscPoint() {
    }

    @Override
    public String getIdentifier() {
        return null;
    }

    public String getPath() { return path; }
    public void setPath(String path) { this.path = path;}

    public static MiscPoint getPointFromMap( Map<String,Object> pointMap) throws Exception {
        if (pointMap == null || pointMap.isEmpty()) {
            throw new Exception(" Map for controller can't be null or empty ->" + pointMap);
        }
        if (containsValueCheck(AgentConstants.NAME, pointMap)) {
            MiscPoint point = new MiscPoint();
            String name = pointMap.get(AgentConstants.NAME).toString();
            point.setName(name);
            if (pointMap.containsKey(AgentConstants.PATH)) {
                point.setPath(pointMap.get(AgentConstants.PATH).toString());
            } else {
                point.setPath(name);
            }
            if (pointMap.containsKey(AgentConstants.ID)) {
                point.setId((Long) pointMap.get(AgentConstants.ID));
            }
            if(pointMap.containsKey(AgentConstants.LOGICAL)){
                point.setLogical(JsonUtil.getBoolean(pointMap.get(AgentConstants.LOGICAL)));
            }
            return (MiscPoint) point.getPointObjectFromMap(pointMap);
        }
        throw new Exception("Mandatory fields like " + AgentConstants.NAME + " might be missing from the input parameter -> " + pointMap);
    }

    @Override
    public FacilioControllerType getControllerType() {
        return FacilioControllerType.MISC;
    }

    @Override
    public JSONObject getChildJSON() {
        JSONObject childPointJSON = new JSONObject();
        childPointJSON.put(AgentConstants.ID, getId());
        childPointJSON.put(AgentConstants.CONTROLLER_ID, getControllerId());
        childPointJSON.put(AgentConstants.PATH, getPath());
        return childPointJSON;
    }

}
