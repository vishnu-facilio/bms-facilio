package com.facilio.bmsconsole.instant.jobs;

import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.taskengine.job.InstantJob;
import com.facilio.workflows.context.WorkflowContext;
import com.facilio.workflows.util.WorkflowUtil;
import com.facilio.workflowv2.util.WorkflowV2Util;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

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
        newContext.put(WorkflowV2Util.WORKFLOW_PARAMS, context.get(FacilioConstants.ContextNames.WORK_FLOW_PARAMS));
        chain.execute();
    }
}
