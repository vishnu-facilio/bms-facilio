package com.facilio.qa.rules.commands;

import com.facilio.bmsconsole.commands.FacilioCommand;
import com.facilio.qa.rules.Constants;
import com.facilio.qa.rules.pojo.QAndARule;
import com.facilio.qa.rules.pojo.QAndARuleType;
import com.facilio.qa.rules.pojo.RuleCondition;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

public class AddConditionsCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        List<QAndARule> rules = (List<QAndARule>) context.get(Constants.Command.RULES);
        if (CollectionUtils.isNotEmpty(rules)) {
            QAndARuleType type = (QAndARuleType) context.get(Constants.Command.RULE_TYPE);
            List<RuleCondition> conditions = new ArrayList<>();
            for (QAndARule rule : rules) {
                IntStream.range(0, rule.getRuleConditions().size()).forEach(i -> setDefaultPropsForCondition(i, rule));
                conditions.addAll(rule.getRuleConditions());

                rule.setRuleConditions(null); // To remove from client response
            }
            Constants.getRuleBean().addConditions(conditions, type);

            // Handle other additions if needed
        }
        return false;
    }

    private <C extends RuleCondition> C setDefaultPropsForCondition (int i, QAndARule<C> rule) {
        C condition = rule.getRuleConditions().get(i);
        condition.setRuleId(rule.getId());
        condition.setSequence(i + 1);
        return condition;
    }
}
