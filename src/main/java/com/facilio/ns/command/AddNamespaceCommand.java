package com.facilio.ns.command;

import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.ns.NamespaceConstants;
import com.facilio.ns.context.NSExecMode;
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
        V3Util.throwRestException(ns.getParentRuleId() == null, ErrorCode.VALIDATION_ERROR, "Parent rule id cannot be null.");

        if (context.get(WorkflowV2Util.WORKFLOW_CONTEXT) != null) {
            Long workflowId = ((WorkflowContext) context.get(WorkflowV2Util.WORKFLOW_CONTEXT)).getId();
            ns.setExecutorId(workflowId);
            ns.setWorkflowId(workflowId);
            ns.setExecMode(NSExecMode.WORKFLOW.getValue());
        } else {
            //other executor method handling
        }

        Long categoryId = (Long) context.get(FacilioConstants.ContextNames.PARENT_CATEGORY_ID); //todel
        ns.setCategoryId(categoryId);

        Long id = Constants.getNsBean().addNamespace(ns);

        context.put(NamespaceConstants.NAMESPACE_ID, id);
        return Boolean.FALSE;
    }
}
