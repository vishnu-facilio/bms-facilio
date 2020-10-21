package com.facilio.bmsconsole.jobs;

import com.facilio.agentv2.AgentApiV2;
import com.facilio.agentv2.FacilioAgent;
import com.facilio.agentv2.actions.AddAgentAction;
import com.facilio.fw.FacilioException;
import com.facilio.tasker.job.FacilioJob;
import com.facilio.tasker.job.JobContext;
import com.facilio.timeseries.TimeSeriesAPI;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.List;

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

    private void pushToMessageQueue(List<JSONObject> payloads) throws Exception {
        for (JSONObject payload : payloads) {
            TimeSeriesAPI.processPayLoad(0, payload, null);
        }
    }

    private void getPayloadsFromWorkflowAndPushToMessageQueue(long workflowId, FacilioAgent agent) throws Exception {
        long lastDataReceivedTime = agent.getLastDataReceivedTime();
        LOGGER.info("Last received Time : " + lastDataReceivedTime);
        long interval = agent.getInterval() * 60 * 1000;
        long currentTime = System.currentTimeMillis();
        for (long noOfDataMissingIntervals = ((currentTime - lastDataReceivedTime) / interval) + 1L;
             noOfDataMissingIntervals > 0; noOfDataMissingIntervals--) {
            long nextTimestampToGetData = ((lastDataReceivedTime + interval) / interval) * interval;
            LOGGER.info("Next Timestamp " + nextTimestampToGetData);
            List<JSONObject> results = runWorkflow(workflowId, nextTimestampToGetData);
            Thread.sleep(2000);
            pushToMessageQueue(results);
            lastDataReceivedTime = lastDataReceivedTime + interval;
        }
    }

    private List<JSONObject> runWorkflow(long workflowId, long nextTimestampToGetData) {
        //TODO get data from workflow for the time 'nextTimestampToGetData'
        return new ArrayList<>();
    }
}
