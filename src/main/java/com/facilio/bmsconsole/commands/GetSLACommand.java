package com.facilio.bmsconsole.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.command.FacilioCommand;
import com.facilio.bmsconsole.util.WorkflowRuleAPI;
import com.facilio.bmsconsole.workflow.rule.WorkflowRuleContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import org.apache.commons.chain.Context;

public class GetSLACommand extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception {
        Long id = (Long) context.get(FacilioConstants.ContextNames.ID);
        if (id != null && id > 0) {
            ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");

            WorkflowRuleContext workflowRule = WorkflowRuleAPI.getWorkflowRule(id);

//            FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.SLA_MODULE);
//            List<FacilioField> allFields = modBean.getAllFields(FacilioConstants.ContextNames.SLA_MODULE);
//            Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(allFields);
//            SelectRecordsBuilder<SLAContext> builder = new SelectRecordsBuilder<SLAContext>()
//                    .module(module)
//                    .beanClass(SLAContext.class)
//                    .select(allFields)
//                    .andCondition(CriteriaAPI.getIdCondition(id, module))
//                    .fetchLookup((LookupField) fieldMap.get("slaRule"));
//            SLAContext slaContext = builder.fetchFirst();

            context.put(FacilioConstants.ContextNames.SLA_RULE_MODULE, workflowRule);
        }
        return false;
    }
}
