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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@AllArgsConstructor
public enum RatingRuleHandler implements RuleHandler {

    STAR_RATING,
    SMILEY_RATING;

    @Override
    public List<Map<String, Object>> emptyRuleConditions(QAndARuleType type, QuestionContext question) throws Exception {
        RatingQuestionContext rq = (RatingQuestionContext) question;
        List<Map<String, Object>> props = new ArrayList<>();
        for (RatingOptionContext option : rq.getOptions()) {
            props.add(emptyCondition(option));
        }
        return props;
    }

    @Override
    public List<Map<String, Object>> serializeConditions(QAndARuleType type, QuestionContext question, List<RuleCondition> conditions) throws Exception {
        RatingQuestionContext rq = (RatingQuestionContext) question;
        Map<String, RuleCondition> conditionMap = conditions.stream().collect(Collectors.toMap(RuleCondition::getValue, Function.identity()));
        List<Map<String, Object>> props = new ArrayList<>();
        for (RatingOptionContext option : rq.getOptions()) {
            RuleCondition condition = conditionMap.get(option._getId().toString()); //ID cannot be null
            if (condition == null) {
                props.add(emptyCondition(option));
            } else {
                Map<String, Object> prop = FieldUtil.getAsProperties(condition);
                prop.remove("operator");
                prop.remove("value");
                addOptionProps(prop, option);
                props.add(prop);
            }
        }

        return props;
    }

    @Override
    public List<RuleCondition> deserializeConditions(QAndARuleType type, QuestionContext question, List<Map<String, Object>> conditionProps) throws Exception {
        RatingQuestionContext rq = (RatingQuestionContext) question;
        Map<Long, RatingOptionContext> options = rq.getOptions().stream().collect(Collectors.toMap(RatingOptionContext::_getId, Function.identity()));
        List<RuleCondition> conditions = new ArrayList<>();
        for (Map<String, Object> prop : conditionProps) {
            Long value = (Long) prop.remove("option");
            RatingOptionContext option = options.get(value);
            V3Util.throwRestException(option == null, ErrorCode.VALIDATION_ERROR, "Invalid option specified while adding Star Rating Rule");
            RuleCondition condition = FieldUtil.getAsBeanFromMap(prop, type.getRuleConditionClass());
            condition.setOperatorEnum(PickListOperators.IS);
            condition.setValue(value.toString());
            conditions.add(condition);
        }
        return conditions;
    }

    @Override
    public List<Map<String, Object>> constructAnswersForEval(QAndARuleType type, QuestionContext question, AnswerContext answer) throws Exception {
        return RuleHandler.constructSingletonAnswerProp(question, answer.getEnumAnswer());
    }

    private void addOptionProps(Map<String, Object> props, RatingOptionContext option) {
        props.put("option", option._getId());
        props.put("label", option.getLabel());
    }

    private Map<String, Object> emptyCondition(RatingOptionContext option) {
        Map<String, Object> props = new HashMap<>(2);
        addOptionProps(props, option);
        return props;
    }
}
