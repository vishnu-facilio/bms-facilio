package com.facilio.agentnew.device;

import com.facilio.agentnew.AgentConstants;
import org.json.simple.JSONObject;

public class Device
{
    private long orgId;
    private long agentId;
    private long siteId;
    private String name;
    private JSONObject controllerProps;
    private Long createdTime;


    public Device(long orgId, long agentId) {
        this.orgId = orgId;
        this.agentId = agentId;
    }

    /**
     * device as {@link JSONObject}.
     * @return
     */
    public JSONObject toJSON(){
        JSONObject deviceJSON = new JSONObject();
        deviceJSON.put(AgentConstants.AGENT_ID,agentId);
        deviceJSON.put(AgentConstants.SITE_ID,siteId);
        deviceJSON.put(AgentConstants.NAME,name);
        deviceJSON.put(AgentConstants.CONTROLLER_PROPS,controllerProps);
        deviceJSON.put(AgentConstants.CREATED_TIME,createdTime);
        return deviceJSON;
    }


    public Long getCreatedTime() { return createdTime; }
    public void setCreatedTime(Long createdTime) { this.createdTime = createdTime; }

    public long getSiteId() { return siteId; }
    public void setSiteId(long siteId) { this.siteId = siteId; }

    public long getOrgId() { return orgId; }

    public long getAgentId() { return agentId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public JSONObject getControllerProps() { return controllerProps; }
    public void setControllerProps(JSONObject controllerProps) { this.controllerProps = controllerProps; }

}
