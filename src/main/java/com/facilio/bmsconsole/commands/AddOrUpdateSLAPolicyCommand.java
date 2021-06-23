package com.facilio.bmsconsole.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.command.FacilioCommand;
import com.facilio.bmsconsole.util.WorkflowRuleAPI;
import com.facilio.bmsconsole.workflow.rule.EventType;
import com.facilio.bmsconsole.workflow.rule.SLAPolicyContext;
import com.facilio.bmsconsole.workflow.rule.WorkflowRuleContext;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import org.apache.commons.chain.Context;

public class AddOrUpdateSLAPolicyCommand extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception {
        SLAPolicyContext slaPolicy = (SLAPolicyContext) context.get(FacilioConstants.ContextNames.SLA_POLICY);
        String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
        if (slaPolicy != null) {
            ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
            FacilioModule module = modBean.getModule(moduleName);
            if (module == null) {
                throw new IllegalArgumentException("Module cannot be empty");
            }

            slaPolicy.setActivityType(EventType.SLA);
            slaPolicy.setModule(module);
            slaPolicy.setRuleType(WorkflowRuleContext.RuleType.SLA_POLICY_RULE);

            if (slaPolicy.getId() > 0) {
                FacilioChain chain = TransactionChainFactory.updateWorkflowRuleChain();
                FacilioContext ruleContext = chain.getContext();
                ruleContext.put(FacilioConstants.ContextNames.WORKFLOW_RULE, slaPolicy);
                chain.execute();
            }
            else {
                WorkflowRuleAPI.updateExecutionOrder(slaPolicy);
                FacilioChain chain = TransactionChainFactory.addWorkflowRuleChain();
                FacilioContext ruleContext = chain.getContext();
                ruleContext.put(FacilioConstants.ContextNames.WORKFLOW_RULE, slaPolicy);
                chain.execute();
            }
        }
        return false;
    }


}
