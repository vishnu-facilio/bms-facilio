package com.facilio.qa.rules.pojo;

import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.modules.FieldUtil;
import com.facilio.qa.context.AnswerContext;
import com.facilio.qa.context.QuestionContext;
import com.facilio.qa.context.RuleHandler;
import com.facilio.qa.context.questions.RatingQuestionContext;
import com.facilio.v3.exception.ErrorCode;
import com.facilio.v3.util.V3Util;
import lombok.AllArgsConstructor;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@AllArgsConstructor
public enum RatingRuleHandler implements RuleHandler {

    RATING;

    @Override
    public List<Map<String, Object>> emptyRuleConditions(QAndARuleType type, QuestionContext question) throws Exception {
        List<Map<String,Object>> props = new ArrayList<>();
        RatingQuestionContext rq = (RatingQuestionContext) question;
        emptyConditions(rq.getRatingScale(), props);
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
            if (condition != null) {
                Map<String, Object> prop = FieldUtil.getAsProperties(condition);
                prop.remove("operator");
                prop.remove("value");
                prop.put("option", i);
                props.add(prop);
            }else {
                Map<String, Object> emptyMap = new HashMap<>();
                emptyMap.put("option",i);
                props.add(emptyMap);
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
            V3Util.throwRestException((value == null || value > ratingScale), ErrorCode.VALIDATION_ERROR, "errors.qa.ratingRuleHandler.ratingOptionCheck",true,null);
            //V3Util.throwRestException((value == null || value > ratingScale), ErrorCode.VALIDATION_ERROR, "Invalid option specified while adding Rating Rule",true,null);
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

    private void emptyConditions(int ratingScale, List<Map<String,Object>> props){
        IntStream.range(1, ratingScale + 1).forEach(i -> {
            Map<String, Object> prop = new HashMap<>();
            prop.put("option", i);
            props.add(prop);
        });
    }
}
