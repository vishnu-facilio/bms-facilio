package com.facilio.qa.rules.commands;

import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.qa.context.QuestionContext;
import com.facilio.qa.context.RuleHandler;
import com.facilio.qa.rules.Constants;
import com.facilio.qa.rules.pojo.QAndARule;
import com.facilio.qa.rules.pojo.QAndARuleType;
import com.facilio.qa.rules.pojo.RuleCondition;
import com.facilio.v3.exception.ErrorCode;
import com.facilio.v3.util.V3Util;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class FetchAndSerializeRulesOfAPage extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        QAndARuleType type = (QAndARuleType) context.get(Constants.Command.RULE_TYPE);
        V3Util.throwRestException(type == null, ErrorCode.VALIDATION_ERROR, "Type cannot be empty while fetching rules");
        List<QuestionContext> questions = (List<QuestionContext>) context.get(FacilioConstants.QAndA.Command.QUESTION_LIST);
        Long templateId = Objects.requireNonNull((Long) context.get(FacilioConstants.QAndA.Command.TEMPLATE_ID), "Template cannot be null");
        if (CollectionUtils.isNotEmpty(questions)) {
            List<QuestionContext> ruleQuestions = questions.stream().filter(QuestionContext::isRuleSupported).collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(ruleQuestions)) {
                List<QAndARule> rules = Constants.getRuleBean().getRulesOfQuestionsOfType(templateId, getQuestionIds(ruleQuestions), type);
                Map<Long, QAndARule> questionVsRules = rules == null ? Collections.EMPTY_MAP : questionVsRules(rules);
                for (QuestionContext question : ruleQuestions) {
                    QAndARule serializedRule = serializeConditions(type, question, templateId, questionVsRules.get(question.getId()));
                    if (serializedRule != null) {
                        getSerializedRules().add(serializedRule);
                    }
                }
            }
        }
        context.put(Constants.Command.RULES, serializedRules == null ? Collections.EMPTY_LIST : serializedRules);

        return false;
    }

    private List<QAndARule> serializedRules;
    private List<QAndARule> getSerializedRules() {
        return serializedRules = serializedRules == null ? new ArrayList<>() : serializedRules;
    }

    private Map<Long, QAndARule> questionVsRules (List<QAndARule> rules) {
        return rules.stream()
                .collect(Collectors.toMap(
                        QAndARule::getQuestionId,
                        Function.identity()
                ));
    }

    private Collection<Long> getQuestionIds (List<QuestionContext> questions) {
        return questions.stream()
                .map(QuestionContext::getId)
                .collect(Collectors.toList());
    }

    private QAndARule serializeConditions(QAndARuleType type, QuestionContext question, Long templateId, QAndARule rule) throws Exception {
        RuleHandler handler = Objects.requireNonNull(question.getQuestionType().getRuleHandler(), "Rule handler cannot be null when rule is supported for a question");

        if (rule == null) {
            rule = handler.emptyRule(type, question);
        }
        else {
            List<RuleCondition> conditions = rule.getRuleConditions();
            if (CollectionUtils.isNotEmpty(conditions)) {
                conditions.stream().forEach(this::removeUnnecessaryProps);
                List<Map<String, Object>> clientConditions = handler.serializeConditions(type, question, conditions);
                rule.setConditions(clientConditions);
            }
            else {
                rule.setConditions(handler.emptyRuleConditions(type, question));
            }
        }

        if (rule != null) {
            rule.setQuestionId(question.getId());
            rule.setQuestion(question.getQuestion());
            rule.setQuestionType(question.getQuestionType());
            rule.setQuestionDescription(question.getDescription());
            rule.setRuleConditions(null);
            rule.setTemplateId(null);
            rule.setType(null);
        }

        return rule;
    }

    private void removeUnnecessaryProps (RuleCondition condition) {
        condition.setCriteriaId(null);
        condition.setRuleId(null);
    }
}
