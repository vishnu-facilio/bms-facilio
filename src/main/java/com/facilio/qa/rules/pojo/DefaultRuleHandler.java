package com.facilio.qa.rules.pojo;

import com.facilio.bmsconsole.templates.Template;
import com.facilio.bmsconsole.util.ActionAPI;
import com.facilio.bmsconsole.workflow.rule.ActionContext;
import com.facilio.bmsconsole.workflow.rule.ActionType;
import com.facilio.modules.FieldUtil;
import com.facilio.qa.QAndAUtil;
import com.facilio.qa.context.AnswerContext;
import com.facilio.qa.context.QuestionContext;
import com.facilio.qa.context.RuleHandler;
import lombok.AllArgsConstructor;
import org.json.simple.JSONObject;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

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
                clonedCondition.put("operator",condition.get("operator"));
                clonedCondition.put("value", condition.get("value"));
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
    public List<Map<String, Object>> constructAnswersForEval(QAndARuleType type, QuestionContext question, AnswerContext answer) throws Exception {
        return RuleHandler.constructSingletonAnswerProp(question, getAnswer.apply(answer));
    }

    @Override
    public void beforeQuestionClone(QuestionContext question) throws Exception {

    }
}
