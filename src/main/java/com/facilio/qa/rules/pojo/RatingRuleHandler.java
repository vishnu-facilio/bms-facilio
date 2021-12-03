package com.facilio.qa.rules.pojo;

import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.db.criteria.operators.PickListOperators;
import com.facilio.modules.FieldUtil;
import com.facilio.qa.context.AnswerContext;
import com.facilio.qa.context.QuestionContext;
import com.facilio.qa.context.RuleHandler;
import com.facilio.qa.context.questions.BaseMCQContext;
import com.facilio.qa.context.questions.MCQOptionContext;
import com.facilio.qa.context.questions.RatingQuestionContext;
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
        RatingQuestionContext rq = (RatingQuestionContext) question;
        int ratingScale = rq.getRatingScale() + 1;
        List<Map<String, Object>> props = new ArrayList<>();
        emptyCondition(ratingScale, props);
        return props;
    }

    @Override
    public List<Map<String, Object>> serializeConditions(QAndARuleType type, QuestionContext question, List<RuleCondition> conditions) throws Exception {
        RatingQuestionContext rq = (RatingQuestionContext) question;
        int ratingScale = rq.getRatingScale() + 1;
        Map<String, RuleCondition> conditionMap = conditions.stream().collect(Collectors.toMap(RuleCondition::getValue, Function.identity()));
        List<Map<String, Object>> props = new ArrayList<>();
        for (int i = 1; i < ratingScale; i++) {
            RuleCondition condition = conditionMap.get(String.valueOf(i)); //ID cannot be null
            if (condition == null) {
                emptyCondition(ratingScale, props);
            } else {
                Map<String, Object> prop = FieldUtil.getAsProperties(condition);
                prop.remove("operator");
                prop.remove("value");
                prop.put("option", i);
                props.add(prop);
            }
        }
        return props;
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
            condition.setOperatorEnum(NumberOperators.EQUALS);
            condition.setValue(value.toString());
            conditions.add(condition);
        }
        return conditions;
    }

    @Override
    public List<Map<String, Object>> constructAnswersForEval(QAndARuleType type, QuestionContext question, AnswerContext answer) throws Exception {
        return RuleHandler.constructSingletonAnswerProp(question, answer.getRatingAnswer());
    }

    private void emptyCondition(int ratingScale, List<Map<String, Object>> props) {
        for (int i = 1; i < ratingScale; i++) {
            Map<String, Object> prop = new HashMap<>();
            prop.put("option", i);
            props.add(prop);
        }
    }
}
