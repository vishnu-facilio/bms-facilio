package com.facilio.bmsconsoleV3.actions;

import com.facilio.bmsconsole.actions.FacilioAction;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.service.FacilioHttpStatus;
import com.facilio.util.FacilioUtil;
import com.facilio.v3.V3Action;
import com.facilio.v3.exception.ErrorCode;
import com.facilio.v3.exception.RESTException;
import com.facilio.workflows.context.WorkflowContext;
import com.facilio.workflows.util.WorkflowUtil;
import com.facilio.workflowv2.util.WorkflowV2Util;
import com.opensymphony.xwork2.ActionSupport;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.extern.log4j.Log4j;
import org.apache.struts2.ServletActionContext;

import javax.servlet.http.HttpServletResponse;

@Log4j
@Setter@Getter
public class V3WorkflowAction extends V3Action {

    private WorkflowContext workflow;
    @NonNull private Long workflowId;

    public String runWorkflow() throws Exception {

        workflow = WorkflowUtil.getWorkflowContext(workflowId);
        int code=0;
        try {
            if (workflow == null) {
                code = FacilioHttpStatus.SC_BAD_REQUEST;
                throw new IllegalArgumentException("Invalid workflow id");
            } else {
                FacilioChain chain = TransactionChainFactory.getExecuteWorkflowChain();
                FacilioContext context = chain.getContext();
                context.put(WorkflowV2Util.WORKFLOW_CONTEXT, workflow);
                chain.execute();
                if (workflow != null && workflow.getErrorListener() != null) {
                    code = FacilioHttpStatus.FC_SCRIPT_SYNTAX_ERROR;
                    throw new RuntimeException(workflow.getErrorListener().getErrorsAsString());
                }
                setCode((Integer) context.get(FacilioConstants.ContextNames.STATUS));
                setData(FacilioConstants.ContextNames.RESULT, workflow.getReturnValue());
            }
        } catch (Exception e) {
            code = code > 0 ? code : FacilioHttpStatus.FC_SCRIPT_EXECUTION_ERROR;
            setCode(code);
            setData("error",e.getMessage());
            httpServletResponse.setStatus(code,e.getMessage());
        }
        return SUCCESS;
    }
}
