package com.facilio.qa.command;

import com.facilio.beans.ModuleBean;
import com.facilio.command.FacilioCommand;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.PickListOperators;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.qa.QAndAUtil;
import com.facilio.qa.context.ClientAnswerContext;
import com.facilio.qa.context.PageContext;
import com.facilio.qa.context.QuestionContext;
import com.facilio.qa.rules.pojo.QAndARuleType;
import com.facilio.qa.rules.pojo.ScoringRule;
import com.facilio.util.FacilioUtil;
import com.facilio.util.MathUtil;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class FetchAnswersForQuestionsCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        List<PageContext> pages = Constants.getRecordList((FacilioContext) context);
        Long responseId = getResponseId((FacilioContext) context);
        if (CollectionUtils.isNotEmpty(pages) && responseId != null) {
            Map<Long, QuestionContext> questions = pages.stream().flatMap(QAndAUtil::getQuestionStream).collect(Collectors.toMap(QuestionContext::getId, Function.identity()));
            QAndAUtil.populateAnswersForQuestions(questions, getResponseCriteria(pages.get(0).getParent().getId(), responseId), true); // Assuming we will always fetch pages of single template
            if(context.get("responseCreatedTime")!=null){
                for (PageContext page : pages) {
                    page.getQuestions().removeIf(question -> question.getAnswer()==null && question.getAnswers()==null && ((Long)context.get("responseCreatedTime")) < question.getSysCreatedTime());
                }
            }
            populateScores(pages.get(0).getParent().getId(), pages, questions);
        }
        return false;
    }

    private Long getResponseId (FacilioContext context) {
        Object responseId = Constants.getQueryParam(context, "response");
        return responseId == null ? null : FacilioUtil.parseLong(responseId);
    }

    private Criteria getResponseCriteria (Long parentId, Long responseId) throws Exception {
        ModuleBean modBean = Constants.getModBean();
        Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(modBean.getAllFields(FacilioConstants.QAndA.ANSWER));
        Criteria criteria = new Criteria();
        criteria.addAndCondition(CriteriaAPI.getCondition(fieldMap.get("parent"), parentId.toString(), PickListOperators.IS));
        criteria.addAndCondition(CriteriaAPI.getCondition(fieldMap.get("response"), responseId.toString(), PickListOperators.IS));
        return criteria;
    }

    private void populateScores (long templateId,List<PageContext> pages, Map<Long, QuestionContext> questions) throws Exception {
        if (MapUtils.isNotEmpty(questions)) {
            List<ScoringRule> rules = com.facilio.qa.rules.Constants.getRuleBean().getRulesOfQuestionsOfType(templateId, questions.keySet(), QAndARuleType.SCORING);
            Map<Long, ScoringRule> questionVsRule = rules == null ? Collections.EMPTY_MAP : rules.stream().collect(Collectors.toMap(ScoringRule::getQuestionId, Function.identity()));

            for (PageContext page : pages) {
                Double fullScore = null, totalScore = null;
                for (QuestionContext question : page.getQuestions()) {
                    ClientAnswerContext answer = question.getAnswer();
                    if (answer == null) {
                        ScoringRule rule = questionVsRule.get(question.getId());
                        if (rule != null) {
                            question.setScore(0d);
                            question.setScorePercent(0f);
                            question.setFullScore(rule.getFullScore());
                        }
                    } else {
                        question.setFullScore(answer.getFullScore());
                        question.setScore(answer.getScore());
                        question.setScorePercent(answer.getScorePercent());

                        //Removing from answer since it's question for consistency
                        answer.setFullScore(null);
                        answer.setScore(null);
                        answer.setScorePercent(null);
                    }

                    if (question.getFullScore() != null) {
                        //Initialising here to send scores only if rules are present or score is present
                        fullScore = fullScore == null ? 0 : fullScore;
                        totalScore = totalScore == null ? 0 : totalScore;

                        fullScore += question.getFullScore() == null ? 0 : question.getFullScore();
                        totalScore += question.getScore() == null ? 0 : question.getScore();
                    }
                }
                if (fullScore != null) {
                    float scorePercent = totalScore == 0 ? 0 : MathUtil.calculatePercentage(totalScore, fullScore);
                    page.setFullScore(fullScore);
                    page.setTotalScore(totalScore);
                    page.setScorePercent(scorePercent);
                }
            }
        }
    }
}
