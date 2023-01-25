package com.facilio.qa.rules.commands;

import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.FieldUtil;
import com.facilio.qa.context.AnswerContext;
import com.facilio.qa.context.QuestionContext;
import com.facilio.qa.context.ResponseContext;
import com.facilio.qa.context.RuleHandler;
import com.facilio.qa.rules.pojo.QAndARule;
import com.facilio.qa.rules.pojo.QAndARuleType;
import com.facilio.qa.rules.pojo.RuleCondition;
import com.facilio.qa.rules.pojo.ScoringRule;
import com.facilio.util.FacilioUtil;
import com.facilio.util.MathUtil;
import com.facilio.workflows.util.WorkflowUtil;
import org.apache.commons.collections4.CollectionUtils;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class ExecuteQAndAScoringRules extends ExecuteQAndARulesCommand {
    public ExecuteQAndAScoringRules() {
        super(QAndARuleType.SCORING);
    }

    @Override
    protected <T extends QAndARule> List<T> fetchRules(QAndARuleType type, Long templateId, Collection<Long> questionIds, FacilioContext context) throws Exception {
        List<ScoringRule> rules = super.fetchRules(type, templateId, questionIds, context);
        if (CollectionUtils.isNotEmpty(rules)) {
            double fullScore = rules.stream().collect(Collectors.summingDouble(ScoringRule::fullScoreWithZeroOnNull));
            ResponseContext response = (ResponseContext) context.get(FacilioConstants.QAndA.RESPONSE);
            response.setFullScore(fullScore);
        }
        return (List<T>) rules;
    }

    @Override
    protected void evaluateRule (AnswerContext answer, QuestionContext question, QAndARule rule,ResponseContext responseContext) throws Exception {
        if (rule == null) {
            return;
        }
        FacilioUtil.throwRunTimeException(question == null, "Question cannot be null here. It's not supposed to happen");
        rule.manipulateAnswer(question, answer);
        RuleHandler handler = Objects.requireNonNull(question.getQuestionType().getRuleHandler(), "Rule handler cannot be null when rule is present for a question");
        List<RuleCondition> conditions = rule.getRuleConditions();
        if(CollectionUtils.isEmpty(conditions)) {
            ScoringRule scoringRule = (ScoringRule) rule;
            Map<String, Object> answerMap = FieldUtil.getAsProperties(answer);
            Map<String, Object> workflowResult = (Map<String, Object>) WorkflowUtil.getResult(scoringRule.getWorkflowId(),answerMap);
            answer.setFullScore(FacilioUtil.parseDouble(workflowResult.get("fullScore")));
            answer.setScore(FacilioUtil.parseDouble(workflowResult.get("totalScore")));
            answer.setScorePercent(MathUtil.calculatePercentage(answer.getScore(), answer.getFullScore()));

            if(responseContext.getFullScore()!=null){
                responseContext.setFullScore(responseContext.getFullScore()+answer.getFullScore());
            }else{
                responseContext.setFullScore(answer.getFullScore());
            }
        }
        else{
            super.evaluateRule(answer,question,rule,responseContext);
        }
    }
}
