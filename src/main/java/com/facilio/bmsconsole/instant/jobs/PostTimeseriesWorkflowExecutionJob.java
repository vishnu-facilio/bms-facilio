package com.facilio.bmsconsole.instant.jobs;

import com.facilio.agentv2.triggers.PostTimeseriesTriggerContext;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.FieldUtil;
import com.facilio.taskengine.job.InstantJob;
import com.facilio.workflows.context.WorkflowContext;
import com.facilio.workflows.util.WorkflowUtil;
import com.facilio.workflowv2.util.WorkflowV2Util;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

public class PostTimeseriesWorkflowExecutionJob extends InstantJob {
    private static final Logger LOGGER = LogManager.getLogger(PostTimeseriesWorkflowExecutionJob.class.getName());

    @Override
    public void execute(FacilioContext context) throws Exception {
        Long workflowId = (Long) context.get(FacilioConstants.ContextNames.TYPE_PRIMARY_ID);
        Long recordId = (Long) context.get(FacilioConstants.ContextNames.RECORD_ID);
        PostTimeseriesTriggerContext trigger = (PostTimeseriesTriggerContext) context.get(FacilioConstants.ContextNames.TRIGGER);
        
        List params = new ArrayList<>();
        params.add(recordId);
        params.add(FieldUtil.getAsProperties(trigger.getAgent()));
        
        WorkflowContext workflowContext = WorkflowUtil.getWorkflowContext(workflowId);
        FacilioChain chain = TransactionChainFactory.getExecuteWorkflowChain();
        FacilioContext newContext = chain.getContext();
        newContext.put(WorkflowV2Util.WORKFLOW_CONTEXT, workflowContext);
        newContext.put(WorkflowV2Util.WORKFLOW_PARAMS, params);
        chain.execute();
    }
}
