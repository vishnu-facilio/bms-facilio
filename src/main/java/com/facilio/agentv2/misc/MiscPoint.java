package com.facilio.agentv2.misc;

import com.facilio.agent.controller.FacilioControllerType;
import com.facilio.agentv2.AgentConstants;
import com.facilio.agentv2.JsonUtil;
import com.facilio.agentv2.point.Point;
import org.json.simple.JSONObject;

import java.util.Map;

public class MiscPoint extends Point {

    private String path;
    public MiscPoint(long agentId){
        super(agentId);
    }
    public MiscPoint(long agentId, long controllerId) {
        super(agentId, controllerId);
    }
    @Deprecated
    private MiscPoint() { }

    public String getPath() { return path; }
    public void setPath(String path) { this.path = path;}

    public static MiscPoint getPointFromMap( Map<String,Object> pointMap) throws Exception {
        if (pointMap == null || pointMap.isEmpty()) {
            throw new Exception(" Map for controller can't be null or empty ->" + pointMap);
        }
        if(containsValueCheck(AgentConstants.PATH,pointMap)){
            MiscPoint point = new MiscPoint();
            point.setPath(((String)pointMap.get(AgentConstants.PATH)));
            if(pointMap.containsKey(AgentConstants.PSEUDO)){
                point.setPseudo(JsonUtil.getBoolean(pointMap.get(AgentConstants.PSEUDO)));
            }
            return (MiscPoint) point.getPointObjectFromMap(pointMap);
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
