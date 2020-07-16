package com.facilio.bmsconsole.util;

import com.facilio.bmsconsole.workflow.rule.CustomButtonRuleContext;
import com.facilio.bmsconsole.workflow.rule.WorkflowRuleContext;
import com.facilio.chain.FacilioContext;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.modules.*;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class CustomButtonAPI {
    public static List<WorkflowRuleContext> getCustomButtons(FacilioModule module, CustomButtonRuleContext.PositionType... positionTypes) throws Exception {
        if (positionTypes.length == 0) {
            throw new IllegalArgumentException("Position types should be given");
        }

        List<Integer> positionTypeInts = new ArrayList<>();
        for (CustomButtonRuleContext.PositionType positionType : positionTypes) {
            positionTypeInts.add(positionType.getIndex());
        }
        Criteria criteria = new Criteria();
        criteria.addAndCondition(CriteriaAPI.getCondition("POSITION_TYPE", "positionType",
                StringUtils.join(positionTypeInts, ","), NumberOperators.EQUALS));

        criteria.addAndCondition(CriteriaAPI.getCondition("MODULEID", "moduleId", String.valueOf(module.getModuleId()), NumberOperators.EQUALS));

        List<WorkflowRuleContext> customButtons = WorkflowRuleAPI.getExtendedWorkflowRules(ModuleFactory.getCustomButtonRuleModule(), FieldFactory.getCustomButtonRuleFields(),
                criteria, null, null, CustomButtonRuleContext.class);
        customButtons = WorkflowRuleAPI.getWorkFlowsFromMapList(FieldUtil.getAsMapList(customButtons, CustomButtonRuleContext.class), true, true);
        return customButtons;
    }

    public static List<WorkflowRuleContext> getExecutableCustomButtons(List<WorkflowRuleContext> customButtons, String moduleName, ModuleBaseWithCustomFields record, Context context) throws Exception {
        List<WorkflowRuleContext> availableButtons = null;
        if (CollectionUtils.isNotEmpty(customButtons)) {
            availableButtons = new ArrayList<>();
            Map<String, Object> recordPlaceHolders = WorkflowRuleAPI.getRecordPlaceHolders(moduleName, record, WorkflowRuleAPI.getOrgPlaceHolders());

            Iterator<WorkflowRuleContext> iterator = customButtons.iterator();
            while (iterator.hasNext()) {
                WorkflowRuleContext workflowRule = iterator.next();
                boolean evaluate = WorkflowRuleAPI.evaluateWorkflowAndExecuteActions(workflowRule, moduleName, record, null, recordPlaceHolders, (FacilioContext) context, false);
                if (evaluate) {
                    availableButtons.add(workflowRule);
                }
            }
        }
        return availableButtons;
    }
}
