package com.facilio.bmsconsole.util;

import com.facilio.bmsconsole.workflow.rule.CustomButtonRuleContext;
import com.facilio.bmsconsole.workflow.rule.SystemButtonRuleContext;
import com.facilio.bmsconsole.workflow.rule.WorkflowRuleContext;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class SystemButtonApi {

    public static List<WorkflowRuleContext> getSystemButtons(FacilioModule module, CustomButtonRuleContext.PositionType... positionTypes) throws Exception{
        if(positionTypes.length == 0){
            throw new IllegalArgumentException("Position Type cannot be null");
        }
        List<Integer> positionTypeInts = new ArrayList<>();
        for (CustomButtonRuleContext.PositionType positionType : positionTypes) {
            positionTypeInts.add(positionType.getIndex());
        }

        Criteria criteria = new Criteria();
        criteria.addAndCondition(CriteriaAPI.getCondition("POSITION_TYPE","positionType", StringUtils.join(positionTypeInts,","),
                NumberOperators.EQUALS));
        criteria.addAndCondition(CriteriaAPI.getCondition("MODULEID","moduleId",String.valueOf(module.getModuleId()),NumberOperators.EQUALS));

        List<WorkflowRuleContext> systemButtons = WorkflowRuleAPI.getExtendedWorkflowRules(ModuleFactory.getSystemButtonRuleModule(), FieldFactory.getSystemButtonRuleFields(),
                criteria,null,null, SystemButtonRuleContext.class);
        systemButtons = WorkflowRuleAPI.getWorkFlowsFromMapList(FieldUtil.getAsMapList(systemButtons,SystemButtonRuleContext.class),true,true);

        return systemButtons;
    }
}
