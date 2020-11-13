package com.facilio.agentv2.niagara;

import com.facilio.agent.controller.FacilioControllerType;
import com.facilio.agentv2.AgentConstants;
import com.facilio.agentv2.point.Point;
import com.facilio.modules.FieldUtil;
import org.json.simple.JSONObject;

import java.util.Map;

public class NiagaraPointContext extends Point {

    private String path;

    public NiagaraPointContext(long agentId, long controllerId) {
        super(agentId, controllerId);
    }
    @Deprecated
    public NiagaraPointContext() { }

    @Override
    public String getIdentifier() {
        return null;
    }

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
        niagaraPointJSON.put(AgentConstants.DEVICE_ID,getDeviceId());
        niagaraPointJSON.put(AgentConstants.PATH,path);
        niagaraPointJSON.put(AgentConstants.CONTROLLER_ID, getControllerId());
        return niagaraPointJSON;
    }

    public static NiagaraPointContext getPointFromMap(Map<String,Object> pointMap) throws Exception {
        if (pointMap == null || pointMap.isEmpty()) {
            throw new Exception(" Map for controller can't be null or empty ->" + pointMap);
        }
        if(containsValueCheck(AgentConstants.PATH,pointMap)){
           /* NiagaraPoint point = new NiagaraPoint(agentId,controllerId);
            point.setPath(((String)pointMap.get(AgentConstants.PATH)));
            return (NiagaraPoint) point.getPointFromMap(pointMap);*/
           JSONObject jsonObject = new JSONObject();
           jsonObject.putAll(pointMap);
           return FieldUtil.getAsBeanFromJson(jsonObject, NiagaraPointContext.class);
        }
        throw new Exception("Mandatory fields like "+AgentConstants.PATH+" might be missing from the input parameter -> "+pointMap);
    }

}
