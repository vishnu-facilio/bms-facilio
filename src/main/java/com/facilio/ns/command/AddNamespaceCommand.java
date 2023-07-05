package com.facilio.ns.command;

import com.facilio.command.FacilioCommand;
import com.facilio.ns.NamespaceConstants;
import com.facilio.ns.context.NameSpaceContext;
import com.facilio.v3.context.Constants;
import com.facilio.v3.exception.ErrorCode;
import com.facilio.v3.util.V3Util;
import com.facilio.workflows.context.WorkflowContext;
import com.facilio.workflowv2.util.WorkflowV2Util;
import org.apache.commons.chain.Context;

public class AddNamespaceCommand extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception {
        NameSpaceContext ns = (NameSpaceContext) context.get(NamespaceConstants.NAMESPACE);
        V3Util.throwRestException(ns == null, ErrorCode.VALIDATION_ERROR, "Namespace cannot be null.");

        if(context.get(WorkflowV2Util.WORKFLOW_CONTEXT)!=null) {
            Long workflowId = ((WorkflowContext) context.get(WorkflowV2Util.WORKFLOW_CONTEXT)).getId();
            ns.setWorkflowId(workflowId);
        }

        Long parentRuleId = (Long) context.get(NamespaceConstants.PARENT_RULE_ID);
        ns.setStatus(true);

        if (parentRuleId != null) {
            ns.setParentRuleId(parentRuleId);
        }

        Long id = Constants.getNsBean().addNamespace(ns);

        context.put(NamespaceConstants.NAMESPACE_ID, id);
        return Boolean.FALSE;
    }
}
