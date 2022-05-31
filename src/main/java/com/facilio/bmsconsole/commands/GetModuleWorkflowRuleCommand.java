package com.facilio.bmsconsole.commands;

import com.facilio.command.FacilioCommand;
import com.facilio.util.FacilioUtil;
import org.apache.commons.chain.Context;
import org.apache.commons.lang3.StringUtils;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.util.WorkflowRuleAPI;
import com.facilio.bmsconsole.workflow.rule.WorkflowRuleContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;

public class GetModuleWorkflowRuleCommand extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception {

        Integer ruleId = (Integer) context.get(FacilioConstants.ContextNames.ID);
        FacilioUtil.throwIllegalArgumentException(ruleId == null || ruleId <= 0, "RuleId is invalid");
        WorkflowRuleContext workflowRule = WorkflowRuleAPI.getWorkflowRule(ruleId);
        FacilioUtil.throwIllegalArgumentException(workflowRule == null, "Invalid workflow rule");
        context.put(FacilioConstants.ContextNames.WORKFLOW_RULE, workflowRule);

        return false;
    }
}
