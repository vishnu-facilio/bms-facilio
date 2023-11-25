package com.facilio.bmsconsole.commands;

import java.util.Collections;

import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;
import org.json.simple.JSONObject;

import com.facilio.agentv2.AgentConstants;
import com.facilio.agentv2.FacilioAgent;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.workflows.context.WorkflowContext;
import com.facilio.workflows.util.WorkflowUtil;
import com.facilio.workflowv2.util.WorkflowV2Util;

public class ProcessCustomDataCommand extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception {
        JSONObject payload = (JSONObject) context.get(AgentConstants.DATA);
        FacilioAgent agent = (FacilioAgent) context.get(AgentConstants.AGENT);

        if (agent.getTransformWorkflowId() > 0) {
            WorkflowContext transformWorkflow = WorkflowUtil.getWorkflowContext(agent.getTransformWorkflowId());
            FacilioChain chain = TransactionChainFactory.getExecuteWorkflowChain();
            FacilioContext newContext = chain.getContext();

            newContext.put(WorkflowV2Util.WORKFLOW_CONTEXT, transformWorkflow);
            newContext.put(WorkflowV2Util.WORKFLOW_PARAMS, Collections.singletonList(payload));

            chain.execute();

            context.put(AgentConstants.DATA, transformWorkflow.getReturnValue());
        }

        return false;
    }

}
