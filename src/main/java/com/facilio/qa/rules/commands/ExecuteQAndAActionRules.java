package com.facilio.qa.rules.commands;

import com.facilio.bmsconsole.util.ActionAPI;
import com.facilio.bmsconsole.workflow.rule.ActionContext;
import com.facilio.chain.FacilioContext;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.ModuleFactory;
import com.facilio.qa.rules.pojo.ActionRule;
import com.facilio.qa.rules.pojo.QAndARule;
import com.facilio.qa.rules.pojo.QAndARuleType;
import org.apache.commons.collections4.CollectionUtils;

import java.util.Collection;
import java.util.List;

public class ExecuteQAndAActionRules extends ExecuteQAndARulesCommand{

    public ExecuteQAndAActionRules() {
        super(QAndARuleType.WORKFLOW);
    }

    @Override
    protected <T extends QAndARule> List<T> fetchRules(QAndARuleType type, Long templateId, Collection<Long> questionIds, FacilioContext context) throws Exception {
        List<ActionRule> rules = super.fetchRules(type, templateId, questionIds, context);
        if (CollectionUtils.isNotEmpty(rules)) {
           for (ActionRule rule: rules){
               rule.setActions(getActiveActionsFromWorkflowRule(rule.getId()));
           }
        }
        return (List<T>) rules;
    }

    private static List<ActionContext> getActiveActionsFromWorkflowRule(long ruleId) throws Exception {
        FacilioModule module = ModuleFactory.getActionModule();
        GenericSelectRecordBuilder actionBuilder = new GenericSelectRecordBuilder()
                .select(FieldFactory.getActionFields())
                .table(module.getTableName())
                .innerJoin("Eval_Rule_Action_Rel")
                .on("Action.ID = Eval_Rule_Action_Rel.ACTION_ID")
                .andCustomWhere("Eval_Rule_Action_Rel.RULE_ID = ? AND Action.STATUS = ?", ruleId, true);
        return ActionAPI.getActionsFromPropList(actionBuilder.get());
    }
}
