package com.facilio.agentv2.actions;

import com.facilio.agentv2.AgentApiV2;
import com.facilio.bmsconsole.actions.FacilioAction;
import com.opensymphony.xwork2.ActionSupport;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class InboundNotificationAction extends FacilioAction {
    private static final Logger LOGGER = LogManager.getLogger(InboundNotificationAction.class.getName());
    private String site_name;
    private String alarm_type;
    private String alarm_status;
    private String apartment;
    private JSONArray blocks;
    private String block;
    private String gateway;
    private long timestamp;
    private String inlet;

    public String getSite_name() {
        return site_name;
    }

    public void setSite_name(String site_name) {
        this.site_name = site_name;
    }

    public String getAlarm_type() {
        return alarm_type;
    }

    public void setAlarm_type(String alarm_type) {
        this.alarm_type = alarm_type;
    }

    public String getAlarm_status() {
        return alarm_status;
    }

    public void setAlarm_status(String alarm_status) {
        this.alarm_status = alarm_status;
    }

    public String getApartment() {
        return apartment;
    }

    public void setApartment(String apartment) {
        this.apartment = apartment;
    }

    public JSONArray getBlocks() {
        return blocks;
    }

    public void setBlocks(JSONArray blocks) {
        this.blocks = blocks;
    }

    public String getBlock() {
        return block;
    }

    public void setBlock(String block) {
        this.block = block;
    }

    public String getGateway() {
        return gateway;
    }

    public void setGateway(String gateway) {
        this.gateway = gateway;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getInlet() {
        return inlet;
    }

    public void setInlet(String inlet) {
        this.inlet = inlet;
    }




    public String execute() throws Exception{
        LOGGER.info("process notification called");
        LOGGER.info("site_name :" + getSite_name());
        LOGGER.info("alarm_type :" + getAlarm_type());
        LOGGER.info("alarm_status :" + getAlarm_status());
        LOGGER.info("apartment :" + getApartment());
        LOGGER.info("blocks :" + getBlocks());
        LOGGER.info("block :" + getBlock());
        LOGGER.info("gateway :" + getGateway());
        LOGGER.info("timestamp :" + getTimestamp());
        LOGGER.info("inlet : " + getInlet());
        return "success";
    }
}
