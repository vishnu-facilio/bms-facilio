package com.facilio.agentnew.point;

import com.facilio.agent.controller.FacilioControllerType;
import com.facilio.agentnew.AgentConstants;
import com.facilio.agentnew.JsonUtil;
import org.json.simple.JSONObject;

import java.util.Map;

public class MiscPoint extends Point {

    private String path;

    public MiscPoint(long agentId, long controllerId) {
        super(agentId, controllerId);
    }

    public String getPath() { return path; }
    public void setPath(String path) { this.path = path;}

    public static MiscPoint getPointFromMap(long agentId, long controllerID, Map<String,Object> pointMap) throws Exception {
        if (pointMap == null || pointMap.isEmpty()) {
            throw new Exception(" Map for controller can't be null or empty ->" + pointMap);
        }
        if(containsValueCheck(AgentConstants.PATH,pointMap)){
            MiscPoint point = new MiscPoint(agentId,controllerID);
            point.setPath(((String)pointMap.get(AgentConstants.PATH)));
            if(pointMap.containsKey(AgentConstants.PSEUDO)){
                point.setPseudo(JsonUtil.getBoolean(pointMap.get(AgentConstants.PSEUDO)));
            }
            return (MiscPoint) point.getPointFromMap(pointMap);
        }
        throw new Exception("Mandatory fields like "+AgentConstants.PATH+" might be missing from the input parameter -> "+pointMap);
    }

    @Override
    public FacilioControllerType getControllerType() {
        return FacilioControllerType.MISC;
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
