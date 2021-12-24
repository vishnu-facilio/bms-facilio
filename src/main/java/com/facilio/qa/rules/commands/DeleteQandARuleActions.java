package com.facilio.qa.rules.commands;

import com.facilio.bmsconsole.util.ActionAPI;
import com.facilio.bmsconsole.workflow.rule.ActionContext;
import com.facilio.command.FacilioCommand;
import com.facilio.db.builder.GenericDeleteRecordBuilder;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.qa.QAndAUtil;
import com.facilio.qa.rules.Constants;
import com.facilio.qa.rules.pojo.ActionRuleCondition;
import com.facilio.qa.rules.pojo.QAndARuleType;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class DeleteQandARuleActions extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        QAndARuleType type = (QAndARuleType) context.get(Constants.Command.RULE_TYPE);

        if (!type.isConditionBasedActions()) {
            return false;
        }

        List<ActionRuleCondition> actionRuleConditions = (List<ActionRuleCondition>) context.get(Constants.Command.CONDITIONS_TO_BE_DELETED);
        if (CollectionUtils.isNotEmpty(actionRuleConditions)) {
            for (ActionRuleCondition actionRuleCondition : actionRuleConditions) {
                List<Map<String, Object>> ruleActionRel = QAndAUtil.fetchEvalRuleActionRel(actionRuleCondition.getId());
                if (CollectionUtils.isNotEmpty(ruleActionRel)) {
                    List<Long> actionIds = ruleActionRel.stream().map(p->(Long)p.get("actionId")).collect(Collectors.toList());
                    if (CollectionUtils.isNotEmpty(actionIds)) {
                        deleteEvalRuleAction(actionRuleCondition.getRuleId(),actionIds,actionRuleCondition.getId());
                        ActionAPI.deleteActions(actionIds);
                    }
                }
            }
        }
        return false;
    }

    private void deleteEvalRuleAction(long ruleId,List<Long> actionsIds,long conditionId) throws Exception {
        GenericDeleteRecordBuilder builder = new GenericDeleteRecordBuilder()
                .table(Constants.ModuleFactory.evalRuleActionRelModule().getTableName())
                .andCondition(CriteriaAPI.getCondition("ACTION_ID","actionId", StringUtils.join(actionsIds,","), NumberOperators.EQUALS))
                .andCondition(CriteriaAPI.getCondition("Q_AND_A_RULE_CONDITION_ID","conditionId", String.valueOf(conditionId), NumberOperators.EQUALS))
                .andCondition(CriteriaAPI.getCondition("RULE_ID","ruleId", String.valueOf(ruleId), NumberOperators.EQUALS));
        builder.delete();
    }
}
