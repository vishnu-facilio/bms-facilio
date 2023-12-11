package com.facilio.qa.context;

import com.facilio.qa.rules.pojo.QAndARule;
import com.facilio.qa.rules.pojo.QAndARuleType;
import com.facilio.qa.rules.pojo.RuleCondition;
import com.facilio.util.FacilioUtil;

import java.text.MessageFormat;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public interface RuleHandler {
    public default QAndARule emptyRule(QAndARuleType type, QuestionContext question) throws Exception {
        QAndARule rule = type.constructRule();
        rule.setConditions(emptyRuleConditions(type, question));
        return rule;
    }

    public List<Map<String, Object>> emptyRuleConditions (QAndARuleType type, QuestionContext question) throws Exception;

    public List<Map<String, Object>> serializeConditions (QAndARuleType type, QuestionContext question, List<RuleCondition> conditions) throws Exception;

    public List<RuleCondition> deserializeConditions(QAndARuleType type, QuestionContext question, List<Map<String, Object>> conditionProps) throws Exception;

    public void constructConditionsForClone(QAndARuleType type, QuestionContext question, List<Map<String, Object>> conditionProps) throws Exception;

    public List<Map<String, Object>> constructAnswersForEval(QAndARuleType type, QuestionContext question, AnswerContext answer) throws Exception;

	default boolean evalMisc(RuleCondition condition,Map<String,Object> props){
		return true;
	}

    public static List<Map<String, Object>> constructSingletonAnswerProp (QuestionContext question, Object answer) {
        FacilioUtil.throwIllegalArgumentException(answer == null, MessageFormat.format("Answer cannot be null during rule evaluation of question : {0}", question.getId()));
        return Collections.singletonList(Collections.singletonMap(RuleCondition.ANSWER_FIELD_NAME, answer));
    }

    public void beforeQuestionClone(QuestionContext question) throws Exception;
}
