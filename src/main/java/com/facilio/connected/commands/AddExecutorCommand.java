package com.facilio.connected.commands;

import com.facilio.chain.FacilioChain;
import com.facilio.command.FacilioCommand;
import com.facilio.ns.NamespaceConstants;
import com.facilio.ns.context.NSExecMode;
import com.facilio.ns.context.NameSpaceContext;
import com.facilio.v3.exception.ErrorCode;
import com.facilio.v3.util.V3Util;
import com.facilio.workflows.command.AddWorkflowCommand;
import com.facilio.workflows.context.WorkflowContext;
import com.facilio.workflowv2.util.WorkflowV2Util;
import org.apache.commons.chain.Context;

public class AddExecutorCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        NameSpaceContext ns = (NameSpaceContext) context.get(NamespaceConstants.NAMESPACE);

        if(ns.getExecModeEnum() == null) {//TODO: This block should be removed (added for backward compatability)
            if(ns.getWorkflowContext() != null) {
                ns.setExecModeEnum(NSExecMode.WORKFLOW);
            } else if (ns.getExecutorId() == null) {
                ns.setExecModeEnum(NSExecMode.NONE);
            }
        }

        ns.setExecModeEnum(ns.getExecModeEnum() == null ? NSExecMode.NONE : ns.getExecModeEnum());

        switch (ns.getExecModeEnum()) {
            case WORKFLOW:
                FacilioChain c = FacilioChain.getTransactionChain();
                c.getContext().putAll(context);
                c.addCommand(new AddWorkflowCommand());
                c.execute();
                Long executorId = ((WorkflowContext) context.get(WorkflowV2Util.WORKFLOW_CONTEXT)).getId();
                ns.setExecutorId(executorId);
                ns.setWorkflowId(executorId); //TODO: need to be clean up
                break;
            case WEAVE:
                V3Util.throwRestException(ns.getParentRuleId() == null, ErrorCode.VALIDATION_ERROR, "Weave executor id cannot be null.");
                Long weaveId = ns.getExecutorId();
                //TODO: Add validation here whether weaver present or not
                break;
            case NONE:
            case CODE_EXEC:
            default:
                //Nothing to do
                break;
        }

        return false;
    }
}
