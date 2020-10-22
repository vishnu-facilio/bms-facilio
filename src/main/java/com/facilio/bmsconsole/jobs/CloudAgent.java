package com.facilio.bmsconsole.jobs;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;

import com.facilio.agentv2.AgentApiV2;
import com.facilio.agentv2.FacilioAgent;
import com.facilio.agentv2.actions.AddAgentAction;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.fw.FacilioException;
import com.facilio.tasker.job.FacilioJob;
import com.facilio.tasker.job.JobContext;
import com.facilio.timeseries.TimeSeriesAPI;
import com.facilio.workflows.context.WorkflowContext;
import com.facilio.workflows.util.WorkflowUtil;
import com.facilio.workflowv2.util.WorkflowV2Util;

public class CloudAgent extends FacilioJob {
    private static final Logger LOGGER = LogManager.getLogger(AddAgentAction.class.getName());
    @Override
    public void execute(JobContext jc) throws Exception {
        long jobId = jc.getJobId();
        FacilioAgent agent = AgentApiV2.getAgent(jobId);
        if (agent != null && agent.getType().equals("rest")) {
            long workflowId = agent.getWorkflowId();
            getPayloadsFromWorkflowAndPushToMessageQueue(workflowId, agent);
        } else {
            if (agent != null) {
                LOGGER.info("Agent Type : " + agent.getType());
            } else {
                LOGGER.info("Agent is null ");
            }
            throw new FacilioException("agent is null or invalid agent type");
        }
    }

    private void pushToMessageQueue(List<Map<String, Object>> results) throws Exception {
        for (Map<String, Object> payload : results) {
        		JSONObject obj = new JSONObject();
        		obj.putAll(payload);
            TimeSeriesAPI.processPayLoad(0, obj, null);
        }
    }

    private void getPayloadsFromWorkflowAndPushToMessageQueue(long workflowId, FacilioAgent agent) throws Exception {
        long lastDataReceivedTime = agent.getLastDataReceivedTime();
        long threeMonths = 3 * 30 * 24 * 3600 * 1000L;
        if (System.currentTimeMillis() - lastDataReceivedTime > threeMonths) {
            pushToMessageQueue(runWorkflow(workflowId, System.currentTimeMillis()));
        }
        LOGGER.info("Last received Time : " + lastDataReceivedTime);
        long interval = agent.getInterval() * 60 * 1000;
        long currentTime = System.currentTimeMillis();
        for (long noOfDataMissingIntervals = ((currentTime - lastDataReceivedTime) / interval) + 1L;
             noOfDataMissingIntervals > 0; noOfDataMissingIntervals--) {
            long nextTimestampToGetData = ((lastDataReceivedTime + interval) / interval) * interval;
            LOGGER.info("Next Timestamp " + nextTimestampToGetData);
            List<Map<String, Object>> results = runWorkflow(workflowId, nextTimestampToGetData);
            Thread.sleep(2000);
            pushToMessageQueue(results);
            lastDataReceivedTime = lastDataReceivedTime + interval;
        }
        agent.setLastDataReceivedTime(lastDataReceivedTime);
        AgentApiV2.updateAgentLastDataRevievedTime(agent);
    }

    private List<Map<String,Object>> runWorkflow(long workflowId, long nextTimestampToGetData) throws Exception {
    		WorkflowContext workflowContext = WorkflowUtil.getWorkflowContext(workflowId);
		workflowContext.setLogNeeded(true);


		FacilioChain chain = TransactionChainFactory.getExecuteWorkflowChain();
		
		FacilioContext newContext = chain.getContext();
		
		List<Object> props = new ArrayList<>();
		props.add(nextTimestampToGetData);
		
		newContext.put(WorkflowV2Util.WORKFLOW_CONTEXT, workflowContext);
		newContext.put(WorkflowV2Util.WORKFLOW_PARAMS, props);

		chain.execute();
		
		return (List<Map<String, Object>>) workflowContext.getReturnValue();
    }
}
