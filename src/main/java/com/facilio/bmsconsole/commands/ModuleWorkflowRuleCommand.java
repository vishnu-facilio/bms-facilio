package com.facilio.bmsconsole.commands;

import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;
import org.apache.commons.lang3.StringUtils;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.workflow.rule.WorkflowRuleContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;

public class ModuleWorkflowRuleCommand extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception {
        WorkflowRuleContext workflowRule = (WorkflowRuleContext) context.get(FacilioConstants.ContextNames.WORKFLOW_RULE);
        String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);

        if (workflowRule != null && StringUtils.isNotEmpty(moduleName)) {
            if (workflowRule.getRuleTypeEnum() == null) {
                workflowRule.setRuleType(WorkflowRuleContext.RuleType.MODULE_RULE);
            }
            ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
            FacilioModule module = modBean.getModule(moduleName);
            if (module == null) {
                throw new IllegalArgumentException("Invalid module name");
            }

            workflowRule.setModule(module);
        }

        return false;
    }
}
