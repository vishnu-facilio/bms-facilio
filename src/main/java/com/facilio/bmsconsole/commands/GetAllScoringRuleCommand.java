package com.facilio.bmsconsole.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.scoringrule.ScoringRuleContext;
import com.facilio.bmsconsole.util.WorkflowRuleAPI;
import com.facilio.bmsconsole.workflow.rule.StateFlowRuleContext;
import com.facilio.bmsconsole.workflow.rule.WorkflowRuleContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import org.apache.commons.chain.Context;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class GetAllScoringRuleCommand extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception {
        String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
        if (StringUtils.isEmpty(moduleName)) {
            throw new Exception("Module cannot be empty");
        }

        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(moduleName);
        if (module == null) {
            throw new IllegalArgumentException("Module cannot be empty");
        }

        Criteria criteria = new Criteria();
        criteria.addAndCondition(CriteriaAPI.getCondition("MODULEID", "moduleId", String.valueOf(module.getModuleId()), NumberOperators.EQUALS));
        criteria.addAndCondition(CriteriaAPI.getCondition("RULE_TYPE", "ruleType", String.valueOf(WorkflowRuleContext.RuleType.SCORING_RULE.getIntVal()), NumberOperators.EQUALS));

        List<WorkflowRuleContext> workflowRules = WorkflowRuleAPI.getExtendedWorkflowRules(ModuleFactory.getScoringRuleModule(), FieldFactory.getScoringRuleFields(),
                criteria, null, null, ScoringRuleContext.class);
        workflowRules = WorkflowRuleAPI.getWorkFlowsFromMapList(FieldUtil.getAsMapList(workflowRules, StateFlowRuleContext.class), true, true);
        if (workflowRules == null) {
            workflowRules = new ArrayList<>();
        };
        context.put(FacilioConstants.ContextNames.WORKFLOW_RULE_LIST, workflowRules);

        return false;
    }
}
