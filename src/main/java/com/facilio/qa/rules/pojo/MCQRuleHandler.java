package com.facilio.qa.rules.pojo;

import com.facilio.db.criteria.operators.PickListOperators;
import com.facilio.modules.FieldUtil;
import com.facilio.qa.context.QuestionContext;
import com.facilio.qa.context.RuleHandler;
import com.facilio.qa.context.questions.BaseMCQContext;
import com.facilio.qa.context.questions.MCQOptionContext;
import com.facilio.v3.exception.ErrorCode;
import com.facilio.v3.util.V3Util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public enum MCQRuleHandler implements RuleHandler {
    INSTANCE; // Making it singleton since only one instance is needed

    @Override
    public QAndARule emptyRule(QAndARuleType type, QuestionContext question) throws Exception {
        QAndARule rule = type.constructRule();
        rule.setConditions(emptyRuleConditions(type, question));
        return rule;
    }

    @Override
    public List<Map<String, Object>> emptyRuleConditions(QAndARuleType type, QuestionContext question) throws Exception {
        BaseMCQContext mcq = (BaseMCQContext) question;
        List<Map<String, Object>> props = new ArrayList<>();
        for (MCQOptionContext option : mcq.getOptions()) {
            props.add(emptyCondition(option));
        }
        return props;
    }

    @Override
    public List<Map<String, Object>> serializeConditions(QAndARuleType type, QuestionContext question, List<RuleCondition> conditions) throws Exception {
//        List<Map<String, Object>> props = FieldUtil.getAsMapList(conditions, type.getRuleConditionClass());
//        Map<String, Map<String, Object>> conditionMap = props.stream().collect(Collectors.toMap(, Function.identity()));
        BaseMCQContext mcq = (BaseMCQContext) question;
        Map<String, RuleCondition> conditionMap = conditions.stream().collect(Collectors.toMap(RuleCondition::getValue, Function.identity()));
        List<Map<String, Object>> props = new ArrayList<>();
        for (MCQOptionContext option : mcq.getOptions()) {
            RuleCondition condition = conditionMap.get(option._getId().toString()); //ID cannot be null
            if (condition == null) {
                props.add(emptyCondition(option));
            }
            else {
                Map<String, Object> prop = FieldUtil.getAsProperties(condition);
                prop.remove("operator");
                prop.remove("value");
                prop.put("option", option._getId());
                props.add(prop);
            }
        }

        return props;
    }

    @Override
    public List<RuleCondition> deserializeConditions(QAndARuleType type, QuestionContext question, List<Map<String, Object>> conditionProps) throws Exception {
        BaseMCQContext mcq = (BaseMCQContext) question;
        Map<Long, MCQOptionContext> options = mcq.getOptions().stream().collect(Collectors.toMap(MCQOptionContext::_getId, Function.identity()));
        List<RuleCondition> conditions = new ArrayList<>();
        for (Map<String, Object> prop : conditionProps) {
            Long value = (Long) prop.remove("option");
            MCQOptionContext option = options.get(value);
            V3Util.throwRestException(option == null, ErrorCode.VALIDATION_ERROR, "Invalid option specified while adding MCQ Rule");
            RuleCondition condition = FieldUtil.getAsBeanFromMap(prop, type.getRuleConditionClass());
            condition.setOperatorEnum(PickListOperators.IS);
            condition.setValue(value.toString());
            conditions.add(condition);
        }
        return conditions;
    }

    private Map<String, Object> emptyCondition(MCQOptionContext option) {
        return Collections.singletonMap("option", option._getId());
    }
}
