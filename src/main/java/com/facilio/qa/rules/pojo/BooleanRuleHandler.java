package com.facilio.qa.rules.pojo;

import com.facilio.db.criteria.operators.BooleanOperators;
import com.facilio.db.criteria.operators.PickListOperators;
import com.facilio.modules.FieldUtil;
import com.facilio.qa.context.QuestionContext;
import com.facilio.qa.context.RuleHandler;
import com.facilio.qa.context.questions.BooleanQuestionContext;
import com.facilio.qa.context.questions.MCQOptionContext;
import com.facilio.v3.exception.ErrorCode;
import com.facilio.v3.exception.RESTException;
import com.facilio.v3.util.V3Util;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public enum BooleanRuleHandler implements RuleHandler {
    INSTANCE
    ;

    private static final String TRUE = "true";
    private static final String FALSE = "false";

    @Override
    public QAndARule emptyRule(QAndARuleType type, QuestionContext question) throws Exception {
        QAndARule rule = type.constructRule();
        rule.setConditions(emptyRuleConditions(type, question));
        return rule;
    }

    @Override
    public List<Map<String, Object>> emptyRuleConditions(QAndARuleType type, QuestionContext question) throws Exception {
        List<Map<String, Object>> props = new ArrayList<>();
        props.add(emptyCondition(TRUE));
        props.add(emptyCondition(FALSE));
        return props;
    }

    @Override
    public List<Map<String, Object>> serializeConditions(QAndARuleType type, QuestionContext question, List<RuleCondition> conditions) throws Exception {
        Map<String, RuleCondition> conditionMap = conditions.stream().collect(Collectors.toMap(RuleCondition::getValue, Function.identity()));
        List<Map<String, Object>> props = new ArrayList<>();
        props.add(serialize(conditionMap, TRUE));
        props.add(serialize(conditionMap, FALSE));
        return props;
    }

    @Override
    public List<RuleCondition> deserializeConditions(QAndARuleType type, QuestionContext question, List<Map<String, Object>> conditionProps) throws Exception {
        List<RuleCondition> conditions = new ArrayList<>();
        for (Map<String, Object> prop : conditionProps) {
            String value = Objects.requireNonNull(prop.remove("option"), "Option cannot be null in boolean rule").toString();
            String booleanVal = null;
            if (TRUE.equalsIgnoreCase(value)) {
                booleanVal = TRUE;
            }
            else if (FALSE.equalsIgnoreCase(value)) {
                booleanVal = FALSE;
            }
            else {
                throw new RESTException(ErrorCode.VALIDATION_ERROR, "Invalid option specified for boolean question rule");
            }
            RuleCondition condition = FieldUtil.getAsBeanFromMap(prop, type.getRuleConditionClass());
            condition.setOperatorEnum(BooleanOperators.IS);
            condition.setValue(booleanVal);
            conditions.add(condition);
        }
        return conditions;
    }

    private Map<String, Object> serialize (Map<String, RuleCondition> conditionMap, String value) throws Exception {
        RuleCondition condition = conditionMap.get(value); //ID cannot be null
        if (condition == null) {
            return emptyCondition(value);
        }
        else {
            Map<String, Object> prop = FieldUtil.getAsProperties(condition);
            prop.remove("operator");
            prop.remove("value");
            prop.put("option", value);
            return prop;
        }
    }

    private Map<String, Object> emptyCondition(String value) {
        return Collections.singletonMap("option", value);
    }
}
