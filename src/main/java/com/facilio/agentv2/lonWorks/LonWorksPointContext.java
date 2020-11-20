package com.facilio.agentv2.lonWorks;

import java.util.Map;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;

import com.facilio.agent.controller.FacilioControllerType;
import com.facilio.agentv2.AgentConstants;
import com.facilio.agentv2.point.Point;
import com.facilio.modules.FieldUtil;
import com.fasterxml.jackson.annotation.JsonInclude;

public class LonWorksPointContext extends Point {

    private static final Logger LOGGER = LogManager.getLogger(LonWorksPointContext.class.getName());

    @JsonInclude
    private String targetComp;
    @JsonInclude
    private String targetName;
    @JsonInclude
    private String linkType;

    public String getTargetComp() {
        return targetComp;
    }

    public void setTargetComp(String targetComp) {
        this.targetComp = targetComp;
    }

    public String getTargetName() {
        return targetName;
    }

    public void setTargetName(String targetName) {
        this.targetName = targetName;
    }

    public String getLinkType() {
        return linkType;
    }

    public void setLinkType(String linkType) {
        this.linkType = linkType;
    }


    @Deprecated
    public LonWorksPointContext() {
    }

    @Override
    public String getIdentifier() {
        return null;
    }

    @Override
    public FacilioControllerType getControllerType() {
        return FacilioControllerType.LON_WORKS;
    }

    @Override
    public JSONObject getChildJSON() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(AgentConstants.ID,getId());
        jsonObject.put(AgentConstants.LINK_TYPE,linkType);
        jsonObject.put(AgentConstants.DEVICE_ID,getDeviceId());
        jsonObject.put(AgentConstants.CONTROLLER_ID, getControllerId());
        jsonObject.put(AgentConstants.TARGET_COMP,targetComp);
        jsonObject.put(AgentConstants.TARGET_NAME,targetName);
        return jsonObject;
    }
    
    public static Point getPointFromMap( Map<String, Object> pointMap) throws Exception {
        if (pointMap == null || pointMap.isEmpty()) {
            throw new Exception(" Map for controller can't be null or empty ->" + pointMap);
        }
          JSONObject jsonObject = new JSONObject();
          jsonObject.putAll(pointMap);
          LonWorksPointContext point = FieldUtil.getAsBeanFromJson(jsonObject, LonWorksPointContext.class);
          return point;
    }
}
