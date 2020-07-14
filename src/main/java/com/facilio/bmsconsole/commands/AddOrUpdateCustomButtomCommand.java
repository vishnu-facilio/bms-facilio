package com.facilio.bmsconsole.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.workflow.rule.CustomButtonRuleContext;
import com.facilio.bmsconsole.workflow.rule.EventType;
import com.facilio.bmsconsole.workflow.rule.WorkflowRuleContext;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

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

            rule.setModule(module);

            if (rule.getButtonTypeEnum() == null) {
                throw new IllegalArgumentException("Button type cannot be empty");
            }
            if (rule.getPositionTypeEnum() == null) {
                throw new IllegalArgumentException("Position type cannot be empty");
            }

            if (rule.getPositionTypeEnum() == CustomButtonRuleContext.PositionType.LIST_TOP) {
                if (rule.getCriteria() != null) {
                    throw new IllegalArgumentException("Criteria cannot be applied with list top position type");
                }
                if (rule.getWorkflow() != null) {
                    throw new IllegalArgumentException("Workflow cannot be applied with list top position type");
                }
            }

            if (rule.getButtonTypeEnum() == CustomButtonRuleContext.ButtonType.SHOW_WIDGET) {
                if (CollectionUtils.isNotEmpty(rule.getActions())) {
                    throw new IllegalArgumentException("Actions cannot be configured with UI ButtonType");
                }
            }

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
