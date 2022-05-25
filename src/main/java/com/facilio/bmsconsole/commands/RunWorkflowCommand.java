package com.facilio.bmsconsole.commands;

import com.facilio.agentv2.AgentConstants;
import com.facilio.agentv2.CloudAgentUtil;
import com.facilio.command.FacilioCommand;
import com.facilio.workflows.context.WorkflowContext;
import com.facilio.workflows.util.WorkflowUtil;
import org.apache.commons.chain.Context;

import java.util.Map;

public class RunWorkflowCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        WorkflowContext workflowContext = WorkflowUtil.getWorkflowContextFromMap((Map<String, Object>) context.get(AgentConstants.WORKFLOW));

        String workflowResponse = CloudAgentUtil.runWorkflow(workflowContext);

        context.put(AgentConstants.WORKFLOW_RESPONSE,workflowResponse);
        return false;
    }
}
