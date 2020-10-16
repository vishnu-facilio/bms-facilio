package com.facilio.bmsconsole.jobs;

import com.facilio.agentv2.AgentApiV2;
import com.facilio.agentv2.FacilioAgent;
import com.facilio.fw.FacilioException;
import com.facilio.tasker.job.FacilioJob;
import com.facilio.tasker.job.JobContext;
import com.facilio.timeseries.TimeSeriesAPI;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class CloudAgent extends FacilioJob {
    @Override
    public void execute(JobContext jc) throws Exception {
        long jobId = jc.getJobId();
        FacilioAgent agent = AgentApiV2.getAgent(jobId);
        if (agent != null) {
            long workflowId = agent.getWorkflowId();
            List<JSONObject> payloads = getPayloadsFromWorkflow(workflowId);
            for (JSONObject payload : payloads) {
                TimeSeriesAPI.processPayLoad(0, payload, null);
            }
        } else {
            throw new FacilioException("agent is null");
        }
    }

    private List<JSONObject> getPayloadsFromWorkflow(long workflowId) {
        //TODO
        return new ArrayList<>();
    }
}
