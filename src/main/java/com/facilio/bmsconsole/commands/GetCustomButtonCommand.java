package com.facilio.bmsconsole.commands;

import com.facilio.bmsconsole.util.WorkflowRuleAPI;
import com.facilio.bmsconsole.workflow.rule.CustomButtonRuleContext;
import com.facilio.bmsconsole.workflow.rule.WorkflowRuleContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;
import org.apache.commons.chain.Context;

import java.util.List;

public class GetCustomButtonCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {

        long ruleId = (long) context.get(FacilioConstants.ContextNames.ID);
        if (ruleId <= 0){
            throw new IllegalArgumentException("Rule is invalid");
        }

        List<FacilioField> fields = FieldFactory.getCustomButtonRuleFields();
        fields.addAll(FieldFactory.getWorkflowRuleFields());
        FacilioModule workflowRuleModule = ModuleFactory.getWorkflowRuleModule();

        Criteria criteria = new Criteria();
        criteria.addAndCondition(CriteriaAPI.getIdCondition(ruleId,workflowRuleModule));

        List<WorkflowRuleContext> customButtonList =  WorkflowRuleAPI.getExtendedWorkflowRules(ModuleFactory.getCustomButtonRuleModule(),
                FieldFactory.getCustomButtonRuleFields(), criteria, null, null, CustomButtonRuleContext.class);


        customButtonList = WorkflowRuleAPI.getWorkFlowsFromMapList(FieldUtil.getAsMapList(customButtonList, CustomButtonRuleContext.class), true, true);

        context.put(FacilioConstants.ContextNames.WORKFLOW_RULE_LIST, customButtonList);

        return false;
    }

}
