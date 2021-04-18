package com.facilio.bmsconsole.instant.jobs;

import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.context.ResourceContext;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.tasker.job.FacilioJob;
import com.facilio.tasker.job.InstantJob;
import com.facilio.tasker.job.JobContext;
import com.facilio.workflows.context.WorkflowContext;
import com.facilio.workflows.util.WorkflowUtil;
import com.facilio.workflowv2.util.WorkflowV2Util;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

public class PostTimeseriesWorkflowExecutionJob extends InstantJob {
    private static final Logger LOGGER = LogManager.getLogger(PostTimeseriesWorkflowExecutionJob.class.getName());

    @Override
    public void execute(FacilioContext context) throws Exception {
        Long recordId = (Long) context.get(FacilioConstants.ContextNames.RECORD_ID);
        LOGGER.info("Record Id : " + recordId);
        WorkflowContext workflowContext = WorkflowUtil.getWorkflowContext(recordId);
        FacilioChain chain = TransactionChainFactory.getExecuteWorkflowChain();
        FacilioContext newContext = chain.getContext();
        newContext.put(WorkflowV2Util.WORKFLOW_CONTEXT, workflowContext);
        List<ResourceContext> resources = (List<ResourceContext>) context.get(FacilioConstants.ContextNames.WORK_FLOW_PARAMS);
        List<Object> params = new ArrayList<>();
        params.add(resources);
        newContext.put(WorkflowV2Util.WORKFLOW_PARAMS, params);
        chain.execute();
    }
}
