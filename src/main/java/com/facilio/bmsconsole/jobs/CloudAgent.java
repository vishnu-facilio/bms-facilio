package com.facilio.bmsconsole.jobs;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;

import com.facilio.agentv2.AgentApiV2;
import com.facilio.agentv2.FacilioAgent;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.fw.FacilioException;
import com.facilio.tasker.job.FacilioJob;
import com.facilio.tasker.job.JobContext;
import com.facilio.time.DateTimeUtil;
import com.facilio.time.SecondsChronoUnit;
import com.facilio.timeseries.TimeSeriesAPI;
import com.facilio.workflows.context.WorkflowContext;
import com.facilio.workflows.util.WorkflowUtil;
import com.facilio.workflowv2.util.WorkflowV2Util;

public class CloudAgent extends FacilioJob {
    private static final Logger LOGGER = LogManager.getLogger(CloudAgent.class.getName());
    @Override
    public void execute(JobContext jc) throws Exception {
        long jobId = jc.getJobId();
        FacilioAgent agent = AgentApiV2.getAgent(jobId);
        if (agent != null) {
            if (agent.getWorkflowId() != -1) {
            		getPayloadsFromWorkflowAndPushToMessageQueue(agent);
            }
            
        } else {
            if (agent != null) {
                LOGGER.info("Agent Type : " + agent.getType());
            } else {
                LOGGER.info("Agent is null ");
            }
            throw new FacilioException("agent is null or invalid agent type");
        }
    }

    private void pushToMessageQueue(List<JSONObject> results) throws Exception {
        for (JSONObject payload: results) {
            TimeSeriesAPI.processPayLoad(0, payload, null);
        }
    }

    private void getPayloadsFromWorkflowAndPushToMessageQueue(FacilioAgent agent) throws Exception {
        long lastDataReceivedTime = agent.getLastDataReceivedTime();
        long threeMonths = 3 * 30 * 24 * 3600 * 1000L;
        long currentTime = System.currentTimeMillis();
        long toTime = adjustTimestamp(currentTime, agent.getInterval());
        long interval = agent.getInterval() * 60 * 1000;
        
        if (toTime - lastDataReceivedTime > threeMonths) {
            long fromTime = toTime - interval;
        		processResult(agent, fromTime, toTime);
            return;
        }
        LOGGER.info("Last received Time : " + lastDataReceivedTime);
        
        for (long noOfDataMissingIntervals = ((toTime - lastDataReceivedTime) / interval);
             noOfDataMissingIntervals > 0; noOfDataMissingIntervals--) {
            long nextTimestampToGetData = ((lastDataReceivedTime + interval) / interval) * interval;
            LOGGER.info("Next Timestamp " + nextTimestampToGetData);
            processResult(agent, lastDataReceivedTime, nextTimestampToGetData);
            lastDataReceivedTime = nextTimestampToGetData;
            Thread.sleep(2000);
        }
    }
    
    private void processResult(FacilioAgent agent, long fromTime, long toTime) throws Exception {
    		List<JSONObject> results = runWorkflow(agent.getWorkflowId(), fromTime, toTime, agent);
        LOGGER.info("results : " + results);
        if (results == null) {
        		throw new FacilioException("Fetching data from cloud failed");
        }
        else if (!results.isEmpty()) {
            	pushToMessageQueue(results);            	
        }
        
        updateLastRecievedTime(agent, toTime);
    }

    private void updateLastRecievedTime(FacilioAgent agent, long lastDataReceivedTime) {
        agent.setLastDataReceivedTime(lastDataReceivedTime);
        AgentApiV2.updateAgentLastDataRevievedTime(agent);
    }

    private List<JSONObject> runWorkflow(long workflowId, long fromTime, long toTime, FacilioAgent agent) throws Exception {
        WorkflowContext workflowContext = WorkflowUtil.getWorkflowContext(workflowId);

		FacilioChain chain = TransactionChainFactory.getExecuteWorkflowChain();

		FacilioContext newContext = chain.getContext();

		List<Object> props = new ArrayList<>();
		props.add(fromTime);
		props.add(toTime);

		newContext.put(WorkflowV2Util.WORKFLOW_CONTEXT, workflowContext);
		newContext.put(WorkflowV2Util.WORKFLOW_PARAMS, props);

		chain.execute();
		
		List<JSONObject> payloads = null;

        List<Map<String, Object>> results = (List<Map<String, Object>>) workflowContext.getReturnValue();
        if (results != null) {
        		payloads = new ArrayList<>();
        		for (Map<String, Object> result: results) {
        			JSONObject payload = new JSONObject();
        			payloads.add(payload);
        			
        			payload.put("publishType", 6);
        			payload.put("agent", agent.getName());
        			payload.put("controllerType", 0);

        			Long timeStamp = (Long) result.get("timestamp");
        			if (timeStamp == null) {
        				timeStamp = fromTime;
        			}
        			payload.put("actual_timestamp", timeStamp);
        			payload.put("timeStamp", timeStamp);
        			
        			JSONObject controller = new JSONObject();
        			controller.put("name", result.remove("controller"));
        			payload.put("controller", controller);
        			
        			Map<String, Object> data = (Map<String, Object>) result.get("data");
        			if (data != null) {
        				payload.put("data", Collections.singletonList(data));
        			}
        		}
        }
        return payloads;
    }
    
    private long adjustTimestamp(long time, long interval) {
		ZonedDateTime zdt = DateTimeUtil.getDateTime(time);
		zdt = zdt.truncatedTo(new SecondsChronoUnit(interval * 60));
		return DateTimeUtil.getMillis(zdt, true);
	}
}

