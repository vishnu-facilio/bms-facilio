package com.facilio.qa.rules.pojo;

import com.facilio.bmsconsole.templates.Template;
import com.facilio.bmsconsole.util.ActionAPI;
import com.facilio.bmsconsole.workflow.rule.ActionContext;
import com.facilio.bmsconsole.workflow.rule.ActionType;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.modules.FieldUtil;
import com.facilio.qa.QAndAUtil;
import com.facilio.qa.context.AnswerContext;
import com.facilio.qa.context.QuestionContext;
import com.facilio.qa.context.RuleHandler;
import com.facilio.qa.context.questions.RatingQuestionContext;
import com.facilio.v3.exception.ErrorCode;
import com.facilio.v3.util.V3Util;
import lombok.AllArgsConstructor;
import org.json.simple.JSONObject;

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
    public void constructConditionsForClone(QAndARuleType type, QuestionContext question, List<Map<String, Object>> conditionProps) throws Exception {
        List<Map<String,Object>> conditions = new ArrayList<>();
        for(Map<String, Object> condition:conditionProps) {
            Long conditionId = (Long) condition.get("id");
            if(conditionId!=null && type == QAndARuleType.WORKFLOW){
                List<Map<String, Object>> evalRuleActionRel = QAndAUtil.fetchEvalRuleActionRel(conditionId);
                List<Long> actionsIds = evalRuleActionRel.stream().map(p -> (Long) p.get("actionId")).collect(Collectors.toList());
                List<ActionContext> actions = (ActionAPI.getActions(Collections.unmodifiableList(actionsIds)));
                List<ActionContext> actionsCloned = new ArrayList<>();
                for(ActionContext action : actions){
                    Template template = action.getTemplate();
                    JSONObject tempJson = template.getOriginalTemplate();
                    ActionContext actionContextClone = new ActionContext();
                    actionContextClone.setActionType(action.getActionType());
                    actionContextClone.setTemplateJson(constructTemplateJson(tempJson,action.getActionType(),question));
                    actionsCloned.add(actionContextClone);
                }
                Map<String,Object> clonedCondition = new HashMap<>();
                clonedCondition.put("id",-1);
                clonedCondition.put("option",condition.get("option"));
                clonedCondition.put("actions",FieldUtil.getAsMapList(actionsCloned,ActionContext.class));
                conditions.add(clonedCondition);
            }else {
                condition.put("id", null);
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
    public List<RuleCondition> deserializeConditions(QAndARuleType type, QuestionContext question, List<Map<String, Object>> conditionProps) throws Exception {
        RatingQuestionContext rq = (RatingQuestionContext) question;
        Integer ratingScale = rq.getRatingScale();
        List<RuleCondition> conditions = new ArrayList<>();
        for (Map<String, Object> prop : conditionProps) {
            Number value = (Number) prop.remove("option");
            V3Util.throwRestException((value == null || value.intValue() > ratingScale), ErrorCode.VALIDATION_ERROR, "Invalid option specified while adding Rating Rule");
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

    @Override
    public void beforeQuestionClone(QuestionContext question) throws Exception {

    }

    private void emptyConditions(int ratingScale, List<Map<String,Object>> props){
        IntStream.range(1, ratingScale + 1).forEach(i -> {
            Map<String, Object> prop = new HashMap<>();
            prop.put("option", i);
            props.add(prop);
        });
    }
}
