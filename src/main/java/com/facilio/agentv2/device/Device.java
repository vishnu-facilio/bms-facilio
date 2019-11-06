package com.facilio.agentv2.device;

import com.facilio.agentv2.AgentConstants;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class Device
{
    private long orgId;
    private long agentId;
    private long siteId;
    private String name;

    public JSONObject getControllerProps() { return controllerProps; }
    public void setControllerProps(JSONObject controllerProps) { this.controllerProps = controllerProps; }

    public String getPropsStr() {
        if(controllerProps != null){
            return controllerProps.toString();
        }
        return null;
    }

    public void setPropsStr(String propsStr) throws ParseException {
        JSONParser parser = new JSONParser();
        this.controllerProps = (JSONObject) parser.parse(propsStr);
    }

    private JSONObject controllerProps;
    private Long createdTime;


    public Device(long orgId, long agentId) {
        this.orgId = orgId;
        this.agentId = agentId;
    }

    public Device() {
    }

    public long getOrgId() { return orgId; }
    public void setOrgId(long orgId) { this.orgId = orgId; }

    public long getAgentId() { return agentId; }
    public void setAgentId(long agentId) { this.agentId = agentId; }

    public long getSiteId() { return siteId; }
    public void setSiteId(long siteId) { this.siteId = siteId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }


    public Long getCreatedTime() { return createdTime; }
    public void setCreatedTime(Long createdTime) { this.createdTime = createdTime; }





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


}
