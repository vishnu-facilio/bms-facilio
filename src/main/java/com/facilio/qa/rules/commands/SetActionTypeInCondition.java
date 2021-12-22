package com.facilio.qa.rules.commands;

import com.facilio.bmsconsole.util.ActionAPI;
import com.facilio.bmsconsole.workflow.rule.ActionContext;
import com.facilio.command.FacilioCommand;
import com.facilio.qa.QAndAUtil;
import com.facilio.qa.rules.Constants;
import com.facilio.qa.rules.pojo.QAndARule;
import com.facilio.qa.rules.pojo.QAndARuleType;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class SetActionTypeInCondition extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        QAndARuleType type = (QAndARuleType) context.get(Constants.Command.RULE_TYPE);
        List<QAndARule> rules = (List<QAndARule>) context.get(Constants.Command.RULES);
        if (!type.isConditionBasedActions()) {
            return false;
        }

        if (CollectionUtils.isNotEmpty(rules)) {
            for (QAndARule rule : rules) {
                List<Map<String, Object>> conditions = rule.getConditions();
                if (CollectionUtils.isNotEmpty(conditions)) {
                    for (Map<String, Object> condition : conditions) {
                        long conditionId = (long)condition.getOrDefault("id",0L);
                        if (conditionId > 0L){
                            List<Map<String, Object>> evalRuleActionRel = QAndAUtil.fetchEvalRuleActionRel(conditionId);
                            List<Long> actionsIds = evalRuleActionRel.stream().map(p -> (Long) p.get("actionId")).collect(Collectors.toList());
                            List<ActionContext> actions = (ActionAPI.getActions(Collections.unmodifiableList(actionsIds)));
                            List<Integer> actionsTypes = actions.stream().map(ActionContext::getActionType).collect(Collectors.toList());
                            condition.put("actionTypes",actionsTypes);
                        }
                    }
                }
            }
        }
        return false;
    }
}
