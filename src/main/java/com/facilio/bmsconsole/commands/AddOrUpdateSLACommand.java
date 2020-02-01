package com.facilio.bmsconsole.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.workflow.rule.EventType;
import com.facilio.bmsconsole.workflow.rule.SLAWorkflowRuleContext;
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
        SLAWorkflowRuleContext slaRule = (SLAWorkflowRuleContext) context.get(FacilioConstants.ContextNames.SLA_RULE_MODULE);
        String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
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
//            slaRule.setSlaModuleId(module.getModuleId());

//            SLAWorkflowRuleContext slaRule = slaRule.getSlaRule();
            slaRule.setActivityType(EventType.SLA);
            if (slaRule.getId() > 0) {
                FacilioChain chain = TransactionChainFactory.updateWorkflowRuleChain();
                FacilioContext updateWorkflowContext = chain.getContext();
                updateWorkflowContext.put(FacilioConstants.ContextNames.WORKFLOW_RULE, slaRule);
                chain.execute();

//                FacilioModule slaModule = modBean.getModule(FacilioConstants.ContextNames.SLA_MODULE);
//                UpdateRecordBuilder<SLAContext> builder = new UpdateRecordBuilder<SLAContext>()
//                        .module(slaModule)
//                        .fields(modBean.getAllFields(FacilioConstants.ContextNames.SLA_MODULE))
//                        .andCondition(CriteriaAPI.getIdCondition(slaRule.getId(), slaModule));
//                builder.update(slaRule);
            }
            else {
                FacilioChain chain = TransactionChainFactory.addWorkflowRuleChain();
                FacilioContext addWorkflowContext = chain.getContext();
                addWorkflowContext.put(FacilioConstants.ContextNames.WORKFLOW_RULE, slaRule);
                chain.execute();

//                InsertRecordBuilder<SLAContext> builder = new InsertRecordBuilder<SLAContext>()
//                        .moduleName(FacilioConstants.ContextNames.SLA_MODULE)
//                        .fields(modBean.getAllFields(FacilioConstants.ContextNames.SLA_MODULE));
//                builder.insert(slaRule);
            }
        }
        return false;
    }

    private void validateSLA(SLAWorkflowRuleContext slaContext) {
        if (StringUtils.isEmpty(slaContext.getName())) {
            throw new IllegalArgumentException("Name is mandatory");
        }

//        if (slaContext.getSlaRule() == null) {
//            throw new IllegalArgumentException("SLA rule cannot be empty");
//        }

//        if (slaContext.getTypeEnum() == null) {
//            slaContext.setType(SLAContext.Type.OPEN);
//        }
    }
}
