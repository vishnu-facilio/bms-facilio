package com.facilio.qa.rules.pojo;

import com.facilio.bmsconsole.templates.Template;
import com.facilio.bmsconsole.util.ActionAPI;
import com.facilio.bmsconsole.workflow.rule.ActionContext;
import com.facilio.bmsconsole.workflow.rule.ActionType;
import com.facilio.db.criteria.operators.PickListOperators;
import com.facilio.modules.FieldUtil;
import com.facilio.qa.QAndAUtil;
import com.facilio.qa.context.AnswerContext;
import com.facilio.qa.context.QuestionContext;
import com.facilio.qa.context.RuleHandler;
import com.facilio.qa.context.questions.BaseMCQContext;
import com.facilio.qa.context.questions.MCQOptionContext;
import com.facilio.util.FacilioUtil;
import com.facilio.v3.exception.ErrorCode;
import com.facilio.v3.util.V3Util;
import org.apache.commons.collections4.CollectionUtils;
import org.json.simple.JSONObject;

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
            V3Util.throwRestException(option == null, ErrorCode.VALIDATION_ERROR, "Invalid option specified while adding MCQ Rule");
            RuleCondition condition = FieldUtil.getAsBeanFromMap(prop, type.getRuleConditionClass());
            condition.setOperatorEnum(PickListOperators.IS);
            condition.setValue(value.toString());
            conditions.add(condition);
        }
        return conditions;
    }

    public void constructConditionsForClone(QAndARuleType type, QuestionContext question, List<Map<String, Object>> conditionProps) throws Exception {
        List<Map<String, Object>> conditions = new ArrayList<>();
        BaseMCQContext mcq = (BaseMCQContext) question;
        List<MCQOptionContext> options = mcq.getOptions();
        Map<Integer, Long> positionVsIdContext = options.stream().collect(Collectors.toMap(MCQOptionContext::getPosition, MCQOptionContext::getId));
        for (Map<String, Object> condition : conditionProps) {
            if (positionVsIdContext.containsKey(condition.get("sequence"))) {
                Long optionId = positionVsIdContext.get(condition.get("sequence"));
                if (type.equals(QAndARuleType.SCORING)) {
                    condition.put("option", optionId);
                    condition.put("id", null);
                } else if (type.equals(QAndARuleType.WORKFLOW)) {
                    Long conditionId = (Long) condition.get("id");
                    if (conditionId != null) {
                        List<Map<String, Object>> evalRuleActionRel = QAndAUtil.fetchEvalRuleActionRel(conditionId);
                        List<Long> actionsIds = evalRuleActionRel.stream().map(p -> (Long) p.get("actionId")).collect(Collectors.toList());
                        List<ActionContext> actions = (ActionAPI.getActions(Collections.unmodifiableList(actionsIds)));
                        List<ActionContext> actionsCloned = new ArrayList<>();
                        for (ActionContext action : actions) {
                            Template template = action.getTemplate();
                            JSONObject tempJson = template.getOriginalTemplate();
                            ActionContext actionContextClone = new ActionContext();
                            actionContextClone.setActionType(action.getActionType());
                            actionContextClone.setTemplateJson(constructTemplateJson(tempJson, action.getActionType(), question));
                            actionsCloned.add(actionContextClone);
                        }
                        Map<String, Object> clonedCondition = new HashMap<>();
                        clonedCondition.put("id", -1);
                        clonedCondition.put("label", condition.get("label"));
                        clonedCondition.put("option", optionId);
                        clonedCondition.put("actions", FieldUtil.getAsMapList(actionsCloned, ActionContext.class));
                        conditions.add(clonedCondition);
                    }
                }
            }
        }
        if(type == QAndARuleType.WORKFLOW){
            conditionProps.clear();
            conditionProps.addAll(conditions);
        }
    }

    public JSONObject constructTemplateJson(JSONObject tempJson, int actionType,QuestionContext question){
        if(actionType== ActionType.WORKFLOW_ACTION.getVal()) {
            JSONObject templateJson = new JSONObject();
            JSONObject workflowContext = (JSONObject) tempJson.get("workflowContext");
            Boolean isV2Script = (Boolean) workflowContext.get("isV2Script");
            String workflowV2String = (String) workflowContext.get("workflowV2String");
            Map<String, Object> resultWorkflowContext = new HashMap<>();
            resultWorkflowContext.put("isV2Script", isV2Script);
            resultWorkflowContext.put("workflowV2String", workflowV2String);
            templateJson.put("resultWorkflowContext", resultWorkflowContext);
            return templateJson;
        } else{
            tempJson.put("name","Email_Template_for_Q_and_A_Rule_"+question.getClonedQuestionId());
        }
        return tempJson;
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

    @Override
    public void beforeQuestionClone(QuestionContext question) throws Exception {

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
