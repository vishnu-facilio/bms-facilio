package com.facilio.agentv2.actions;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.agent.AgentType;
import com.facilio.agent.fw.constants.PublishType;
import com.facilio.agentv2.AgentConstants;
import com.facilio.agentv2.AgentUtilV2;
import com.facilio.agentv2.FacilioAgent;
import com.facilio.agentv2.cacheimpl.AgentBean;
import com.facilio.bmsconsole.context.ControllerType;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.FacilioException;
import com.facilio.queue.source.MessageSource;
import com.facilio.services.messageQueue.MessageQueue;
import com.facilio.services.messageQueue.MessageQueueFactory;
import com.facilio.services.procon.message.FacilioRecord;
import com.facilio.util.FacilioUtil;
import com.facilio.v3.V3Action;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j;
import org.apache.commons.collections4.CollectionUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Log4j
public class RestPushAgentV3Action extends V3Action {

    private static final int MAX_TS_RECORDS = 100;

    @Getter @Setter
    private JSONObject payload;
    @Getter
    private PublishType publishType;

    private void setPublishType(Integer publishTypeInt) {
        if (publishTypeInt != null) {
            publishType = PublishType.valueOf(publishTypeInt);
        }
    }

    public String push() throws Exception {
        JSONObject originalPayload = (JSONObject) payload.clone();
        try{

            validatePayload();

            AgentBean agentBean = AgentConstants.getAgentBean();
            String agentName = (String) payload.get(AgentConstants.AGENT);
            FacilioAgent agent = agentBean.getAgent(agentName);
            if (agent == null) {
                throw new FacilioException("Agent not found for the name " + agentName);
            }
            if(agent.getAgentTypeEnum() != AgentType.REST) {
                throw new FacilioException("Data can be sent only for rest agent in this api");
            }
            MessageSource ms = AgentUtilV2.getMessageSource(agent);
            MessageQueue mq = MessageQueueFactory.getMessageQueue(ms);

            if (!payload.containsKey(AgentConstants.PUBLISH_TYPE)) {
                payload.put(AgentConstants.PUBLISH_TYPE, publishType.asInt());
            }

            List<FacilioRecord> records = new ArrayList<>();
            records.add(new FacilioRecord(agentName, payload));

            String topic = AccountUtil.getCurrentOrg().getDomain();
            mq.put(topic, records);
        }
        catch (FacilioException e) {
            LOGGER.error(e.getMessage(), e);
            throw e;
        }
        catch (Exception e) {
            LOGGER.error("Error while processing rest payload ", e);
            LOGGER.info("Failed payload - " + originalPayload.toJSONString());
            throw new FacilioException("Error while processing the payload");
        }

        setData("result", "Data is queued successfully");

        return SUCCESS;
    }

    // TODO delete if not needed later
    private void convertToV3payloads(String agentName, List<FacilioRecord> records) throws FacilioException {
        List<Map<String, Object>> trendArr = (ArrayList<Map<String, Object>>) payload.remove(AgentConstants.TIMESERIES);
        if(CollectionUtils.isEmpty(trendArr)) {
            throw new FacilioException("Timeseries array is mandatory for v3.1");
        }
        int size = trendArr.size();
        if (size > MAX_TS_RECORDS) {
            throw new FacilioException("Cannot send more than " + MAX_TS_RECORDS + " records in a payload");
        }
        trendArr.forEach(trend -> {
            JSONObject newPayload = (JSONObject) payload.clone();
            newPayload.putAll(trend);
            records.add(new FacilioRecord(agentName, newPayload));
        });
    }

    private void addPayloadDefaultValues() {
        if (!payload.containsKey(AgentConstants.PUBLISH_TYPE)) {
            payload.put(AgentConstants.PUBLISH_TYPE, publishType.asInt());
        }
        if (!payload.containsKey(AgentConstants.CONTROLLER_TYPE)) {
            payload.put(AgentConstants.CONTROLLER_TYPE, ControllerType.MISC.getKey());
        }
        if (!payload.containsKey(AgentConstants.CONTROLLER)) {
            String parentUniqueNum = (String) payload.get(AgentConstants.UNIQUE_ID);
            Map<String, String> controller =  Collections.singletonMap(FacilioConstants.ContextNames.NAME, parentUniqueNum);
            payload.put(FacilioConstants.ContextNames.CONTROLLER, controller);
            // Controller can be empty in payload only for misc type
            payload.put(AgentConstants.CONTROLLER_TYPE, ControllerType.MISC.getKey());
        }
    }


    private void validatePayload() throws Exception {
        if (!payload.containsKey(AgentConstants.CONTROLLER) && !payload.containsKey(AgentConstants.UNIQUE_ID)) {
            throw new FacilioException("Controller name or Unique ID is mandatory");
        }
    }
}
