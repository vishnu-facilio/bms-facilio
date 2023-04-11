package com.facilio.qa.rules.commands;

import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.FieldUtil;
import com.facilio.qa.context.QuestionContext;
import com.facilio.qa.rules.Constants;
import com.facilio.qa.rules.pojo.QAndARule;
import com.facilio.qa.rules.pojo.QAndARuleType;
import com.facilio.qa.rules.pojo.RuleCondition;
import com.facilio.v3.exception.ErrorCode;
import com.facilio.v3.util.V3Util;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.text.MessageFormat;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class DeserializeRulesCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        QAndARuleType type = (QAndARuleType) context.get(Constants.Command.RULE_TYPE);
        V3Util.throwRestException(type == null, ErrorCode.VALIDATION_ERROR, "Type cannot be empty while adding rules");
        List<Map<String, Object>> rules = (List<Map<String, Object>>) context.get(Constants.Command.RULES);

        if (CollectionUtils.isNotEmpty(rules)) {
            List<QuestionContext> questions = (List<QuestionContext>) context.get(FacilioConstants.QAndA.Command.QUESTION_LIST);
            Long templateId = Objects.requireNonNull((Long) context.get(FacilioConstants.QAndA.Command.TEMPLATE_ID), "Template cannot be null");
            Map<Long, QuestionContext> rulesSupportedQuestions = questions == null ? Collections.EMPTY_MAP :
                    questions.stream().filter(QuestionContext::isRuleSupported)
                            .collect(Collectors.toMap(QuestionContext::getId, Function.identity()));
            List<QAndARule> ruleList = new ArrayList<>();
            for (Map<String, Object> prop : rules) {
                Long questionId = (Long) prop.get("questionId");
                QuestionContext question = rulesSupportedQuestions.get(questionId);
                V3Util.throwRestException(question == null || !question.getQuestionType().isRuleSupported(), ErrorCode.VALIDATION_ERROR, "Invalid question id specified while adding rules");
                List<Map<String, Object>> conditions = (List<Map<String, Object>>) prop.remove("conditions");
//                V3Util.throwRestException(CollectionUtils.isEmpty(conditions), ErrorCode.VALIDATION_ERROR, "Rule conditions cannot be empty");
                List<RuleCondition> conditionList = CollectionUtils.isEmpty(conditions) ? Collections.EMPTY_LIST : // So this means we'll delete existing conditions for this rule
                                                    question.getQuestionType().getRuleHandler().deserializeConditions(type, question, conditions);
//                V3Util.throwRestException(CollectionUtils.isEmpty(conditionList), ErrorCode.VALIDATION_ERROR, "Invalid conditions during addition");
//                List<RuleCondition> conditionsWithActionValue = conditionList == null ? null : conditionList.stream().filter(RuleCondition::hasAction).collect(Collectors.toList());
//                if (CollectionUtils.isNotEmpty(conditionsWithActionValue)) {
                QAndARule rule = FieldUtil.getAsBeanFromMap(prop, type.getRuleClass());

                Long workflowId = (Long) prop.get("workflowId");
                if (workflowId == null && type.isActionMandatory()) {
                    V3Util.throwRestException(conditionList.stream().anyMatch(RuleCondition::actionIsEmpty), ErrorCode.VALIDATION_ERROR, MessageFormat.format("Condition {0} cannot be empty", type.getEmptyActionErrorValue()));
                }

                rule.setRuleConditions(conditionList);
                rule.setType(type);
                rule.setQuestionId(questionId);
                rule.setTemplateId(templateId);
                ruleList.add(rule);

                // For client response
                rule.setQuestion(question.getQuestion());
                rule.setQuestionType(question.getQuestionType());

                rule.beforeSave(question);
//                }
            }
            context.put(Constants.Command.RULES, ruleList);
        }

        return false;
    }
}
