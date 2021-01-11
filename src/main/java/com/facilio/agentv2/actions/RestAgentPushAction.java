package com.facilio.agentv2.actions;


import com.facilio.accounts.util.AccountUtil;
import com.facilio.agentv2.AgentConstants;
import com.facilio.bmsconsole.actions.FacilioAction;
import com.facilio.services.kafka.FacilioKafkaProducer;
import com.facilio.services.procon.message.FacilioRecord;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;

import javax.validation.constraints.NotNull;

public class RestAgentPushAction extends FacilioAction {
    private static final Logger LOGGER = LogManager.getLogger(RestAgentPushAction.class.getName());
    private String sender;

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getAgent(JSONObject payload) throws Exception {
        if (payload.containsKey(AgentConstants.AGENT)) {
            return payload.get(AgentConstants.AGENT).toString();
        } else {
            throw new Exception("Agent name not found");
        }
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
        try {
            if (getAgent(getPayload()).equals(getSender())) {
                LOGGER.info("payload : " + getPayload());
                FacilioKafkaProducer producer = new FacilioKafkaProducer();
                String topic = AccountUtil.getCurrentOrg().getDomain();
                JSONObject data = new JSONObject();
                data.put("data", getPayload().toJSONString());
                FacilioRecord record = new FacilioRecord(getSender(), data);
                try {
                    producer.putRecord(topic, record);
                } catch (Exception e) {
                    LOGGER.info("Exception while put record ", e);
                    return ERROR;
                } finally {
                    producer.close();
                }
                return SUCCESS;
            } else {
                LOGGER.info("Agent did not match");
                return ERROR;
            }
        } catch (Exception ex) {
            LOGGER.info(ex);
            return ERROR;
        }

    }
}
