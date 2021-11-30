package com.facilio.qa.rules.pojo;

import com.facilio.db.criteria.operators.PickListOperators;
import com.facilio.modules.FieldUtil;
import com.facilio.qa.context.AnswerContext;
import com.facilio.qa.context.QuestionContext;
import com.facilio.qa.context.RuleHandler;
import com.facilio.qa.context.questions.RatingQuestionContext;
import com.facilio.qa.context.questions.handler.RatingOptionContext;
import com.facilio.v3.exception.ErrorCode;
import com.facilio.v3.util.V3Util;
import lombok.AllArgsConstructor;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@AllArgsConstructor
public enum RatingRuleHandler implements RuleHandler {

    RATING;

    @Override
    public List<Map<String, Object>> emptyRuleConditions(QAndARuleType type, QuestionContext question) throws Exception {
        return Collections.EMPTY_LIST;
    }

    @Override
    public List<Map<String, Object>> serializeConditions(QAndARuleType type, QuestionContext question, List<RuleCondition> conditions) throws Exception {
        return FieldUtil.getAsMapList(conditions, type.getRuleConditionClass());
    }

    @Override
    public List<RuleCondition> deserializeConditions(QAndARuleType type, QuestionContext question, List<Map<String, Object>> conditionProps) throws Exception {
        RatingQuestionContext rq = (RatingQuestionContext) question;
        Integer ratingScale = rq.getRatingScale();
        List<RuleCondition> conditions = new ArrayList<>();
        for (Map<String, Object> prop : conditionProps) {
            Long value = (Long) prop.remove("option");
            V3Util.throwRestException((value == null || value > ratingScale), ErrorCode.VALIDATION_ERROR, "Invalid option specified while adding Rating Rule");
            RuleCondition condition = FieldUtil.getAsBeanFromMap(prop, type.getRuleConditionClass());
            condition.setOperatorEnum(PickListOperators.IS);
            condition.setValue(value.toString());
            conditions.add(condition);
        }
        return conditions;
    }

    @Override
    public List<Map<String, Object>> constructAnswersForEval(QAndARuleType type, QuestionContext question, AnswerContext answer) throws Exception {
        return RuleHandler.constructSingletonAnswerProp(question, answer.getRatingAnswer());
    }
}
