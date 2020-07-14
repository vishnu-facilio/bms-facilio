package com.facilio.bmsconsole.commands;

import com.facilio.bmsconsole.util.WorkflowRuleAPI;
import com.facilio.bmsconsole.workflow.rule.CustomButtonRuleContext;
import com.facilio.bmsconsole.workflow.rule.WorkflowRuleContext;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.modules.*;
import org.apache.commons.chain.Context;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class GetAvailableButtonsCommand extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception {
        ModuleBaseWithCustomFields moduleData = (ModuleBaseWithCustomFields) context.get(FacilioConstants.ContextNames.RECORD);
        String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
        int positionType = (int) context.get(FacilioConstants.ContextNames.POSITION_TYPE);
        if (moduleData != null) {
            CustomButtonRuleContext.PositionType positionTypeEnum = CustomButtonRuleContext.PositionType.valueOf(positionType);
            if (positionTypeEnum == null) {
                throw new IllegalArgumentException("Position type cannot be empty");
            }
            Criteria criteria = new Criteria();
            criteria.addAndCondition(CriteriaAPI.getCondition("POSITION_TYPE", "positionType",
                    String.valueOf(positionTypeEnum.getIndex()), NumberOperators.EQUALS));
            List<WorkflowRuleContext> customButtons = WorkflowRuleAPI.getExtendedWorkflowRules(ModuleFactory.getCustomButtonRuleModule(), FieldFactory.getCustomButtonRuleFields(),
                    criteria, null, null, CustomButtonRuleContext.class);
            customButtons = WorkflowRuleAPI.getWorkFlowsFromMapList(FieldUtil.getAsMapList(customButtons, CustomButtonRuleContext.class), true, true);

            Map<String, Object> recordPlaceHolders = WorkflowRuleAPI.getRecordPlaceHolders(moduleName, moduleData, WorkflowRuleAPI.getOrgPlaceHolders());

            Iterator<WorkflowRuleContext> iterator = customButtons.iterator();
            while (iterator.hasNext()) {
                WorkflowRuleContext stateFlow = iterator.next();
                boolean evaluate = WorkflowRuleAPI.evaluateWorkflowAndExecuteActions(stateFlow, moduleName, moduleData, null, recordPlaceHolders, (FacilioContext) context, false);
                if (!evaluate) {
                    iterator.remove();
                }
            }
            context.put(FacilioConstants.ContextNames.WORKFLOW_RULE_LIST, customButtons);
        }
        return false;
    }
}
