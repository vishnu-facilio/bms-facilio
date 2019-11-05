package com.facilio.agentnew.niagara;

import com.facilio.agent.controller.FacilioControllerType;
import com.facilio.agentnew.AgentConstants;
import com.facilio.agentnew.controller.Controller;
import com.facilio.agentnew.point.Point;
import com.facilio.modules.FieldUtil;
import org.json.simple.JSONObject;

import java.util.Map;

public class NiagaraPoint extends Point {

    private String path;

    public NiagaraPoint(long agentId, long controllerId) {
        super(agentId, controllerId);
    }
    @Deprecated
    private NiagaraPoint() { }

    public String getPath() { return path; }
    public void setPath(String path) { this.path = path; }

    @Override
    public FacilioControllerType getControllerType() {
        return FacilioControllerType.NIAGARA;
    }

    @Override
    public JSONObject getChildJSON() {
        JSONObject niagaraPointJSON = new JSONObject();
        niagaraPointJSON.put(AgentConstants.ID,getId());
        niagaraPointJSON.put(AgentConstants.CONTROLLER_ID,getControllerId());
        niagaraPointJSON.put(AgentConstants.PATH,path);
        return niagaraPointJSON;
    }

    public static NiagaraPoint getPointFromMap(long agentId, long controllerId , Map<String,Object> pointMap) throws Exception {
        if (pointMap == null || pointMap.isEmpty()) {
            throw new Exception(" Map for controller can't be null or empty ->" + pointMap);
        }
        if(containsValueCheck(AgentConstants.PATH,pointMap)){
           /* NiagaraPoint point = new NiagaraPoint(agentId,controllerId);
            point.setPath(((String)pointMap.get(AgentConstants.PATH)));
            return (NiagaraPoint) point.getPointFromMap(pointMap);*/
           JSONObject jsonObject = new JSONObject();
           jsonObject.putAll(pointMap);
           return FieldUtil.getAsBeanFromJson(jsonObject,NiagaraPoint.class);
        }
        throw new Exception("Mandatory fields like "+AgentConstants.PATH+" might be missing from the input parameter -> "+pointMap);
    }

    public static Point getPointFromMap(Controller controller, Map<String,Object> row) throws Exception {
        return getPointFromMap(controller.getAgentId(),controller.getId(),row);
    }
}
