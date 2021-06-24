package com.facilio.qa.rules.pojo;

import com.facilio.modules.FieldUtil;
import com.facilio.qa.context.AnswerContext;
import com.facilio.qa.context.QuestionContext;
import com.facilio.qa.context.RuleHandler;
import lombok.AllArgsConstructor;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

@AllArgsConstructor
public enum DefaultRuleHandler implements RuleHandler {
    // Making it singleton since only one instance is needed
    NUMBER (AnswerContext::getNumberAnswer),
    DECIMAL (AnswerContext::getDecimalAnswer),
    SHORT_ANSWER (AnswerContext::getShortAnswer),
    LONG_ANSWER (AnswerContext::getLongAnswer),
    DATE_TIME (AnswerContext::getDateTimeAnswer)
    ;

    private Function<AnswerContext, Object> getAnswer;

    @Override
    public List<Map<String, Object>> emptyRuleConditions(QAndARuleType type, QuestionContext question) throws Exception {
        return Collections.EMPTY_LIST;
    }

    @Override
    public List<Map<String, Object>> serializeConditions(QAndARuleType type, QuestionContext question, List<RuleCondition> conditions) throws Exception{
        return FieldUtil.getAsMapList(conditions, type.getRuleConditionClass());
    }

    @Override
    public List<RuleCondition> deserializeConditions(QAndARuleType type, QuestionContext question, List<Map<String, Object>> conditionProps) throws Exception {
        return FieldUtil.getAsBeanListFromMapList(conditionProps, type.getRuleConditionClass());
    }

    @Override
    public List<Map<String, Object>> constructAnswersForEval(QAndARuleType type, QuestionContext question, AnswerContext answer) throws Exception {
        return RuleHandler.constructSingletonAnswerProp(question, getAnswer.apply(answer));
    }
}
