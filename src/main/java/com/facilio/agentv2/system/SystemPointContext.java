package com.facilio.agentv2.system;

import com.facilio.agent.controller.FacilioControllerType;
import com.facilio.agentv2.AgentConstants;
import com.facilio.agentv2.point.Point;
import com.facilio.modules.FieldUtil;
import org.json.simple.JSONObject;

import java.util.Map;

public class SystemPointContext extends Point {

    public SystemPointContext(long agentId) {
        super(agentId);
    }

    public SystemPointContext(long agentId, long controllerId) {
        super(agentId, controllerId);
    }

    public SystemPointContext() {
    }

    public static Point getPointFromMap(Map<String, Object> payload) {
        return FieldUtil.getAsBeanFromMap(payload,SystemPointContext.class);
    }

    @Override
    public FacilioControllerType getControllerType() {
        return FacilioControllerType.SYSTEM;
    }

    @Override
    public JSONObject getChildJSON() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(AgentConstants.ID,getId());
        jsonObject.put(AgentConstants.NAME,getName());
        jsonObject.put(AgentConstants.CONTROLLER_ID,getControllerId());
        jsonObject.put(AgentConstants.DEVICE_ID,getDeviceId());
        return jsonObject;
    }
}
