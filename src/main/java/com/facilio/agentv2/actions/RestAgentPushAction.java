package com.facilio.agentv2.actions;


import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.actions.FacilioAction;
import com.facilio.services.kafka.FacilioKafkaProducer;
import com.facilio.services.procon.message.FacilioRecord;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;

import javax.validation.constraints.NotNull;

public class RestAgentPushAction extends FacilioAction {
    private static final Logger LOGGER = LogManager.getLogger(RestAgentPushAction.class.getName());
    @NotNull
    private String agent;

    public String getAgent() {
        return agent;
    }

    public void setAgent(String agent) {
        this.agent = agent;
    }

    @NotNull
    private JSONObject payload;

    public JSONObject getPayload() {
        return payload;
    }

    public void setPayload(JSONObject payload) {
        this.payload = payload;
    }

    public String push() {
        LOGGER.info("payload : " + getPayload());
        FacilioKafkaProducer producer = new FacilioKafkaProducer();
        String topic = AccountUtil.getCurrentOrg().getDomain();
        FacilioRecord record = new FacilioRecord(getAgent(), getPayload());
        try {
            producer.putRecord(topic, record);
        } catch (Exception e) {
            LOGGER.info("Exception while put record ", e);
            return ERROR;
        } finally {
            producer.close();
        }
        return SUCCESS;
    }
}
