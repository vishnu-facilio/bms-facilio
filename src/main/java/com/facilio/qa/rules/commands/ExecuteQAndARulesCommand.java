package com.facilio.qa.rules.commands;

import com.facilio.bmsconsole.scoringrule.ScoringRuleContext;
import com.facilio.chain.FacilioContext;
import com.facilio.command.FacilioCommand;
import com.facilio.command.PostTransactionCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.FieldUtil;
import com.facilio.qa.context.AnswerContext;
import com.facilio.qa.context.QuestionContext;
import com.facilio.qa.context.ResponseContext;
import com.facilio.qa.context.RuleHandler;
import com.facilio.qa.rules.Constants;
import com.facilio.qa.rules.pojo.QAndARule;
import com.facilio.qa.rules.pojo.QAndARuleType;
import com.facilio.qa.rules.pojo.RuleCondition;
import com.facilio.qa.rules.pojo.ScoringRule;
import com.facilio.util.FacilioUtil;
import com.facilio.util.MathUtil;
import com.facilio.workflows.util.WorkflowUtil;
import lombok.NonNull;
import lombok.extern.log4j.Log4j;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

@Log4j
public class ExecuteQAndARulesCommand extends FacilioCommand implements PostTransactionCommand {
    private QAndARuleType type;
    private FacilioContext context;

    ExecuteQAndARulesCommand (@NonNull QAndARuleType type) {
        this.type = type;
    }

    protected <T extends QAndARule> List<T> fetchRules (QAndARuleType type, Long templateId, Collection<Long> questionIds, FacilioContext context) throws Exception {
        return Constants.getRuleBean().getRulesOfQuestionsOfType(templateId, questionIds, type);
    }

    @Override
    public boolean executeCommand(Context context) throws Exception {
        if (type.isPostTransactionExecution()) {
            this.context = (FacilioContext) context;
        }
        else {
            executeRule((FacilioContext) context);
        }
        return false;
    }

    private void executeRule(FacilioContext context) throws Exception {
        Long templateId = (Long) context.get(FacilioConstants.QAndA.Command.TEMPLATE_ID);
        FacilioUtil.throwRunTimeException(templateId == null, "Template id cannot be null for Q And A rule evaluation");
        Map<Long, QuestionContext> questions = (Map<Long, QuestionContext>) context.get(FacilioConstants.QAndA.Command.QUESTION_MAP);
        FacilioUtil.throwRunTimeException(MapUtils.isEmpty(questions), "Question map cannot be null for Q And A rule evaluation");

        List<Long> questionIds = questions.values().stream().filter(QuestionContext::isRuleSupported).map(QuestionContext::getId).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(questionIds)) {
            LOGGER.debug("Rule evaluation is not done because no rule supported questions");
            return;
        }
        Collection<QAndARule> rules = fetchRules(type, templateId, questionIds, (FacilioContext) context);

        // Fetching rules even before checking there are answers, because in case of scoring we have to update full score. Besides rules can be cached and so this shouldn't be much of an issue

        Collection<AnswerContext> answers = (Collection<AnswerContext>) context.get(FacilioConstants.QAndA.Command.ANSWER_LIST);
        if (CollectionUtils.isEmpty(answers)) {
            LOGGER.debug("Rule evaluation is not done because answer list is empty");
            return;
        }

        if (CollectionUtils.isNotEmpty(rules)) {
            ResponseContext response = (ResponseContext) context.get(FacilioConstants.QAndA.RESPONSE);
            Map<Long, QAndARule> questionVsRule = rules.stream().collect(Collectors.toMap(QAndARule::getQuestionId, Function.identity()));
            for (AnswerContext answer : answers) {
                evaluateRule(answer, questions.get(answer.getQuestion().getId()), questionVsRule.get(answer.getQuestion().getId()),(ResponseContext) response);
            }
        }
        else {
            LOGGER.debug("Rule evaluation is not done because no matching rules for the given answers");
        }
    }

    protected void evaluateRule (AnswerContext answer, QuestionContext question, QAndARule rule,ResponseContext responsecontext) throws Exception {
        if (rule == null) {
            return;
        }
        FacilioUtil.throwRunTimeException(question == null, "Question cannot be null here. It's not supposed to happen");
        rule.manipulateAnswer(question, answer);
        RuleHandler handler = Objects.requireNonNull(question.getQuestionType().getRuleHandler(), "Rule handler cannot be null when rule is present for a question");
        List<RuleCondition> conditions = rule.getRuleConditions();

        if (CollectionUtils.isNotEmpty(conditions)) {
            List<Map<String, Object>> answersForEval = handler.constructAnswersForEval(type, question, answer);
            // Populate answer props further if needed
            for (Map<String, Object> answerProp : answersForEval) {
                for (RuleCondition condition : conditions) {
                    boolean result = evalAndExecuteConditions(answerProp, answer, question, condition);
                    if (result) { // May be this can be controlled type wise or some other config later if needed
                        break;
                    }
                }
            }
        }
    }

    private boolean evalAndExecuteConditions (Map<String, Object> prop, AnswerContext answer, QuestionContext question, RuleCondition condition) throws Exception {

        boolean result = condition.evaluate(question,prop);
        if (result) {
            condition.executeTrueAction(question, answer);
        }
        return result;
    }

    @Override
    public boolean postExecute() throws Exception {
        if (type.isPostTransactionExecution()) {
            executeRule(context);
        }
        return false;
    }
}
