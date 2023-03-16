package com.facilio.qa.rules.pojo;

import com.facilio.db.criteria.operators.PickListOperators;
import com.facilio.modules.FieldUtil;
import com.facilio.qa.context.AnswerContext;
import com.facilio.qa.context.QuestionContext;
import com.facilio.qa.context.RuleHandler;
import com.facilio.qa.context.questions.BaseMCQContext;
import com.facilio.qa.context.questions.MCQOptionContext;
import com.facilio.util.FacilioUtil;
import com.facilio.v3.exception.ErrorCode;
import com.facilio.v3.util.V3Util;
import org.apache.commons.collections4.CollectionUtils;

import java.text.MessageFormat;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public enum MCQRuleHandler implements RuleHandler {
    // Making it singleton since only one instance is needed
    SINGLE,
    MULTI;

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
                addOptionProps(prop, option);
                props.add(prop);
            }
        }

        return props;
    }

    @Override
    public List<RuleCondition> deserializeConditions(QAndARuleType type, QuestionContext question, List<Map<String, Object>> conditionProps) throws Exception {
        BaseMCQContext mcq = (BaseMCQContext) question;
        Map<Long, MCQOptionContext> options = mcq.getOptions().stream().collect(Collectors.toMap(MCQOptionContext::getId, Function.identity()));
        List<RuleCondition> conditions = new ArrayList<>();
        for (Map<String, Object> prop : conditionProps) {
            Long value = (Long) prop.remove("option");
            MCQOptionContext option = options.get(value);
            V3Util.throwRestException(option == null, ErrorCode.VALIDATION_ERROR, "errors.qa.mcqRuleHandler.optionValidator",true,null);
            //V3Util.throwRestException(option == null, ErrorCode.VALIDATION_ERROR, "Invalid option specified while adding MCQ Rule",true,null);
            RuleCondition condition = FieldUtil.getAsBeanFromMap(prop, type.getRuleConditionClass());
            condition.setOperatorEnum(PickListOperators.IS);
            condition.setValue(value.toString());
            conditions.add(condition);
        }
        return conditions;
    }

    @Override
    public List<Map<String, Object>> constructAnswersForEval(QAndARuleType type, QuestionContext question, AnswerContext answer) throws Exception {
        switch (this) {
            case SINGLE:
                return RuleHandler.constructSingletonAnswerProp(question, answer.getEnumAnswer());
            case MULTI:
                FacilioUtil.throwIllegalArgumentException(CollectionUtils.isEmpty(answer.getMultiEnumAnswer()), MessageFormat.format("At least one option needs to be present for rule evaluation of question : {0}. This is not supposed to happen", question.getId()));
                List<Map<String, Object>> answerProps = new ArrayList<>();
                for (MCQOptionContext option : answer.getMultiEnumAnswer()) {
                    answerProps.add(Collections.singletonMap(RuleCondition.ANSWER_FIELD_NAME, option.getId()));
                }
                return answerProps;
            default:
                throw new RuntimeException("This is not supposed to happen");
        }
    }

    private void addOptionProps (Map<String, Object> props, MCQOptionContext option) {
        props.put("option", option.getId());
        props.put("label", option.getLabel());
    }

    private Map<String, Object> emptyCondition(MCQOptionContext option) {
        Map<String, Object> props = new HashMap<>(2);
        addOptionProps(props, option);
        return props;
    }
}
