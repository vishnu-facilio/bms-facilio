package com.facilio.qa.rules.pojo;

import com.facilio.modules.FieldUtil;
import com.facilio.qa.context.QuestionContext;
import com.facilio.qa.context.RuleHandler;

import java.util.List;
import java.util.Map;

public enum DefaultRuleHandler implements RuleHandler {
    INSTANCE; // Making it singleton since only one instance is needed

    @Override
    public QAndARule emptyRule(QAndARuleType type, QuestionContext question) throws Exception {
        return null;
    }

    @Override
    public List<Map<String, Object>> emptyRuleConditions(QAndARuleType type, QuestionContext question) throws Exception {
        return null;
    }

    @Override
    public List<Map<String, Object>> serializeConditions(QAndARuleType type, QuestionContext question, List<RuleCondition> conditions) throws Exception{
        return FieldUtil.getAsMapList(conditions, type.getRuleConditionClass());
    }

    @Override
    public List<RuleCondition> deserializeConditions(QAndARuleType type, QuestionContext question, List<Map<String, Object>> conditionProps) throws Exception {
        return FieldUtil.getAsBeanListFromMapList(conditionProps, type.getRuleConditionClass());
    }
}
