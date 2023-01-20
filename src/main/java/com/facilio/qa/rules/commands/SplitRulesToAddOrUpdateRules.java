package com.facilio.qa.rules.commands;

import com.facilio.command.FacilioCommand;
import com.facilio.qa.rules.Constants;
import com.facilio.qa.rules.bean.QAndARuleBean;
import com.facilio.qa.rules.pojo.QAndARule;
import com.facilio.qa.rules.pojo.QAndARuleType;
import com.facilio.qa.rules.pojo.RuleCondition;
import com.facilio.qa.rules.pojo.ScoringRule;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class SplitRulesToAddOrUpdateRules extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        List<QAndARule> rules = (List<QAndARule>) context.get(Constants.Command.RULES);
        if (CollectionUtils.isNotEmpty(rules)) {
            QAndARuleType type = (QAndARuleType) context.get(Constants.Command.RULE_TYPE);
            Long templateId = rules.get(0).getTemplateId(); // Assuming all rules will be of same id
            QAndARuleBean ruleBean = Constants.getRuleBean();
            List<QAndARule> oldRules = ruleBean.getRulesOfQuestionsOfType(templateId, rules.stream().map(QAndARule::getQuestionId).collect(Collectors.toList()), type);
            Map<Long, QAndARule> oldRuleMap = oldRules == null ? Collections.EMPTY_MAP :
                                                oldRules.stream().collect(Collectors.toMap(QAndARule::getQuestionId, Function.identity()));

            for (QAndARule rule : rules) {
                QAndARule oldRule = oldRuleMap.get(rule.getQuestionId());
                ScoringRule scoringRule = null;
                if(type == QAndARuleType.SCORING){
                    scoringRule =(ScoringRule) rule;
                }
                if (oldRule == null) {
                    if (CollectionUtils.isNotEmpty(rule.getRuleConditions()) || (scoringRule!=null && Objects.nonNull(scoringRule) && scoringRule.getWorkflowId()!=null) ) { // Preventing empty rules getting added
                        getRulesToBeAdded().add(rule);
                    }
                }
                else {
                    if (CollectionUtils.isNotEmpty(oldRule.getRuleConditions())) {
                        getConditionsToBeDeleted().addAll(oldRule.getRuleConditions());
                    }
                    oldRule.copyDefaultProps(rule);
                    getRulesToBeUpdated().add(rule);
                }
            }

            context.put(Constants.Command.RULES_TO_BE_ADDED, rulesToBeAdded);
            context.put(Constants.Command.RULES_TO_BE_UPDATED, rulesToBeUpdated);
            context.put(Constants.Command.CONDITIONS_TO_BE_DELETED, conditionsToBeDeleted);

        }
        return false;
    }

    private List<RuleCondition> conditionsToBeDeleted;
    private List<RuleCondition> getConditionsToBeDeleted() {
        return conditionsToBeDeleted = conditionsToBeDeleted == null ? new ArrayList<>() : conditionsToBeDeleted;
    }

    private List<QAndARule> rulesToBeAdded;
    private List<QAndARule> getRulesToBeAdded() {
        return rulesToBeAdded = rulesToBeAdded == null ? new ArrayList<>() : rulesToBeAdded;
    }

    private List<QAndARule> rulesToBeUpdated;
    private List<QAndARule> getRulesToBeUpdated() {
        return rulesToBeUpdated = rulesToBeUpdated == null ? new ArrayList<>() : rulesToBeUpdated;
    }
}
