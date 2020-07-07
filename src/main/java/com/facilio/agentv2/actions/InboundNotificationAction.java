package com.facilio.agentv2.actions;

import com.facilio.agentv2.AgentApiV2;
import com.facilio.bmsconsole.actions.FacilioAction;
import com.opensymphony.xwork2.ActionSupport;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class InboundNotificationAction extends FacilioAction {
    private static final Logger LOGGER = LogManager.getLogger(InboundNotificationAction.class.getName());

    private String data;

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String execute() throws Exception{
        LOGGER.info("process notification called");
        LOGGER.info(getData());
        return "success";
    }
}
