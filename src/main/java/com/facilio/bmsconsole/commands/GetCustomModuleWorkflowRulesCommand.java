package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.List;

import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;
import org.apache.commons.lang3.StringUtils;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.util.WorkflowRuleAPI;
import com.facilio.bmsconsole.workflow.rule.WorkflowRuleContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;

public class GetCustomModuleWorkflowRulesCommand extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception {
        String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
        Integer ruleType = (Integer) context.get(FacilioConstants.ContextNames.RULE_TYPE);

        WorkflowRuleContext.RuleType type = null;
        if (ruleType != null) {
            type = WorkflowRuleContext.RuleType.valueOf(ruleType);
        }

        if (StringUtils.isNotEmpty(moduleName)) {
            ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
            FacilioModule module = modBean.getModule(moduleName);
            if (module == null) {
                throw new IllegalArgumentException("Invalid module name");
            }

            Criteria criteria = new Criteria();
            criteria.addAndCondition(CriteriaAPI.getCondition("MODULEID", "moduleId", String.valueOf(module.getModuleId()), NumberOperators.EQUALS));

            if (type == null) {
                type = WorkflowRuleContext.RuleType.MODULE_RULE;
            }
            List<WorkflowRuleContext> workflowRules = WorkflowRuleAPI.getWorkflowRules(type, true, criteria, null, null, "EXECUTION_ORDER");
            if (workflowRules == null) {
                workflowRules = new ArrayList<>();
            }
            context.put(FacilioConstants.ContextNames.WORKFLOW_RULE_LIST, workflowRules);
        }
        return false;
    }
}
