package com.facilio.qa.context;

import com.facilio.qa.rules.pojo.QAndARule;
import com.facilio.qa.rules.pojo.QAndARuleType;
import com.facilio.qa.rules.pojo.RuleCondition;

import java.util.List;
import java.util.Map;

public interface RuleHandler {
    public QAndARule emptyRule(QAndARuleType type, QuestionContext question) throws Exception;

    public List<Map<String, Object>> emptyRuleConditions (QAndARuleType type, QuestionContext question) throws Exception;

    public List<Map<String, Object>> serializeConditions (QAndARuleType type, QuestionContext question, List<RuleCondition> conditions) throws Exception;

    public List<RuleCondition> deserializeConditions(QAndARuleType type, QuestionContext question, List<Map<String, Object>> conditionProps) throws Exception;
}
