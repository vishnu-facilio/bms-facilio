package com.facilio.bmsconsole.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.SLAContext;
import com.facilio.bmsconsole.workflow.rule.SLAWorkflowRuleContext;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.InsertRecordBuilder;
import org.apache.commons.chain.Context;
import org.apache.commons.lang3.StringUtils;

public class AddOrUpdateSLACommand extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception {
        SLAContext slaContext = (SLAContext) context.get(FacilioConstants.ContextNames.SLA);
        String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
        if (slaContext != null) {
            validateSLA(slaContext);
            ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");

            if (slaContext.getId() > 0) {
                // edit
            }
            else {
                if (StringUtils.isEmpty(moduleName)) {
                    throw new IllegalArgumentException("Invalid module");
                }
                FacilioModule module = modBean.getModule(moduleName);
                if (module == null) {
                    throw new IllegalArgumentException("Invalid module");
                }

                slaContext.setSlaModuleId(module.getModuleId());

                SLAWorkflowRuleContext slaRule = slaContext.getSlaRule();

                FacilioChain chain = TransactionChainFactory.addWorkflowRuleChain();
                FacilioContext addWorkflowContext = chain.getContext();
                addWorkflowContext.put(FacilioConstants.ContextNames.WORKFLOW_RULE, slaRule);
                chain.execute();

                InsertRecordBuilder<SLAContext> builder = new InsertRecordBuilder<SLAContext>()
                        .moduleName(FacilioConstants.ContextNames.SLA_MODULE)
                        .fields(modBean.getAllFields(FacilioConstants.ContextNames.SLA_MODULE));
                builder.insert(slaContext);
            }
        }
        return false;
    }

    private void validateSLA(SLAContext slaContext) {
        if (StringUtils.isEmpty(slaContext.getName())) {
            throw new IllegalArgumentException("Name is mandatory");
        }

        if (slaContext.getSlaRule() == null) {
            throw new IllegalArgumentException("SLA rule cannot be empty");
        }

        if (slaContext.getTypeEnum() == null) {
            slaContext.setType(SLAContext.Type.OPEN);
        }
    }
}
