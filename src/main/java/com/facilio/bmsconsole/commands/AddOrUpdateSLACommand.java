package com.facilio.bmsconsole.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.command.FacilioCommand;
import com.facilio.bmsconsole.workflow.rule.EventType;
import com.facilio.bmsconsole.workflow.rule.SLAWorkflowCommitmentRuleContext;
import com.facilio.bmsconsole.workflow.rule.WorkflowRuleContext;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import org.apache.commons.chain.Context;
import org.apache.commons.lang3.StringUtils;

public class AddOrUpdateSLACommand extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception {
        SLAWorkflowCommitmentRuleContext slaRule = (SLAWorkflowCommitmentRuleContext) context.get(FacilioConstants.ContextNames.SLA_RULE_MODULE);
        String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
        Long parentSLAPolicyId = (Long) context.get(FacilioConstants.ContextNames.SLA_POLICY_ID);
        if (slaRule != null) {
            validateSLA(slaRule);
            ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");

            if (StringUtils.isEmpty(moduleName)) {
                throw new IllegalArgumentException("Invalid module");
            }
            FacilioModule module = modBean.getModule(moduleName);
            if (module == null) {
                throw new IllegalArgumentException("Invalid module");
            }

            if (parentSLAPolicyId == null || parentSLAPolicyId <= 0) {
                throw new IllegalArgumentException("Illegal SLA Policy");
            }

            slaRule.setModule(module);
            slaRule.setOnSuccess(true);
            slaRule.setParentRuleId(parentSLAPolicyId);
            slaRule.setActivityType(EventType.SLA);
            slaRule.setRuleType(WorkflowRuleContext.RuleType.SLA_WORKFLOW_RULE);
            if (slaRule.getId() > 0) {
                FacilioChain chain = TransactionChainFactory.updateWorkflowRuleChain();
                FacilioContext updateWorkflowContext = chain.getContext();
                updateWorkflowContext.put(FacilioConstants.ContextNames.WORKFLOW_RULE, slaRule);
                chain.execute();
            }
            else {
                FacilioChain chain = TransactionChainFactory.addWorkflowRuleChain();
                FacilioContext addWorkflowContext = chain.getContext();
                addWorkflowContext.put(FacilioConstants.ContextNames.WORKFLOW_RULE, slaRule);
                chain.execute();
            }
        }
        return false;
    }

    private void validateSLA(SLAWorkflowCommitmentRuleContext slaContext) {
        if (StringUtils.isEmpty(slaContext.getName())) {
            throw new IllegalArgumentException("Name is mandatory");
        }
    }
}
