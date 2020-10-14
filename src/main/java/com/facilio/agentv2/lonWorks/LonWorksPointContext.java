package com.facilio.agentv2.lonWorks;

import com.facilio.agent.controller.FacilioControllerType;
import com.facilio.agentv2.AgentConstants;
import com.facilio.agentv2.bacnet.BacnetIpPointContext;
import com.facilio.agentv2.point.Point;
import com.fasterxml.jackson.annotation.JsonInclude;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;

public class LonWorksPointContext extends Point {

    private static final Logger LOGGER = LogManager.getLogger(BacnetIpPointContext.class.getName());

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
        jsonObject.put(AgentConstants.LINK_TYPE,linkType);
        jsonObject.put(AgentConstants.DEVICE_ID,getDeviceId());
        jsonObject.put(AgentConstants.TARGET_COMP,targetComp);
        jsonObject.put(AgentConstants.TARGET_NAME,targetName);
        return jsonObject;
    }
}
