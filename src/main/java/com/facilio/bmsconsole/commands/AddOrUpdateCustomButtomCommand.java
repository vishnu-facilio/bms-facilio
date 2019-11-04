package com.facilio.bmsconsole.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.workflow.rule.CustomButtonRuleContext;
import com.facilio.bmsconsole.workflow.rule.EventType;
import com.facilio.bmsconsole.workflow.rule.StateflowTransitionContext;
import com.facilio.bmsconsole.workflow.rule.WorkflowRuleContext;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import org.apache.commons.chain.Context;

public class AddOrUpdateCustomButtomCommand extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception {
        CustomButtonRuleContext rule = (CustomButtonRuleContext) context.get(FacilioConstants.ContextNames.WORKFLOW_RULE);
        FacilioModule module = (FacilioModule) context.get(FacilioConstants.ContextNames.MODULE);
        String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
        if (rule != null) {
            rule.setRuleType(WorkflowRuleContext.RuleType.CUSTOM_BUTTON);
            rule.setActivityType(EventType.CUSTOM_BUTTON);

            ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
            if (module == null) {
                module = modBean.getModule(moduleName);
            }
            if (module == null) {
                throw new IllegalArgumentException("Module name cannot be empty");
            }

            rule.setModuleName(module.getName());


            FacilioChain chain;
            if (rule.getId() < 0) {
                chain = TransactionChainFactory.addWorkflowRuleChain();
            }
            else {
                chain = TransactionChainFactory.updateWorkflowRuleChain();
            }
            FacilioContext workflowContext = chain.getContext();
            workflowContext.put(FacilioConstants.ContextNames.WORKFLOW_RULE, rule);
            chain.execute();
        }
        return false;
    }
}
