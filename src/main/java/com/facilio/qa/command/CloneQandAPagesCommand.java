package com.facilio.qa.command;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsoleV3.util.V3RecordAPI;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.fields.LookupField;
import com.facilio.qa.QAndAUtil;
import com.facilio.qa.context.*;
import com.facilio.qa.rules.Constants;
import com.facilio.qa.rules.commands.QAndARuleReadOnlyChainFactory;
import com.facilio.qa.rules.commands.QAndARuleTransactionChainFactory;
import com.facilio.qa.rules.pojo.QAndARule;
import com.facilio.qa.rules.pojo.QAndARuleType;
import com.facilio.qa.rules.pojo.ScoringRule;
import com.facilio.v3.exception.ErrorCode;
import com.facilio.v3.util.V3Util;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

public class CloneQandAPagesCommand extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception {

        Long templateId = (Long) context.get(FacilioConstants.QAndA.OLD_TEMPLATE_ID);
        QAndATemplateContext clonedTemplate = (QAndATemplateContext) context.get(FacilioConstants.QAndA.CLONED_Q_AND_A_TEMPLATE);
        Criteria criteria = new Criteria();
        criteria.addAndCondition(CriteriaAPI.getCondition("PARENT_ID","parentId",String.valueOf(templateId), NumberOperators.EQUALS));
        List<PageContext> pages = V3RecordAPI.getRecordsListWithSupplements(FacilioConstants.QAndA.PAGE, null,PageContext.class,criteria,null);
        QAndAUtil.populateQuestionsInPages(pages);
        if(pages!=null && !pages.isEmpty()){
            for(PageContext page:pages){
                List<QuestionContext> questions = page.getQuestions();
                PageContext clonedPage = page.toBuilder().parent(clonedTemplate).questions(null).build();
                QAndAUtil.addRecordViaV3Chain(FacilioConstants.QAndA.PAGE, Collections.singletonList(clonedPage));
                if (CollectionUtils.isNotEmpty(questions)) {
                    questions.stream().forEach(q -> {
                        try {
                            updateQuestionProps(q, clonedPage);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    });
                    QAndAUtil.addRecordViaV3Chain(FacilioConstants.QAndA.QUESTION, questions);
                }
                Map<Long, Long> oldQuestionIdVsNewMap = questions.stream()
                        .collect(Collectors.toMap(QuestionContext::getClonedQuestionId, QuestionContext::getId));

                // ScoringRules Clone
                FacilioChain fetchScoringRulesChain = QAndARuleReadOnlyChainFactory.fetchRulesChain();
                fetchScoringRulesChain.getContext().put(com.facilio.qa.rules.Constants.Command.RULE_TYPE, QAndARuleType.SCORING);
                fetchScoringRulesChain.getContext().put(FacilioConstants.QAndA.Command.PAGE_ID, page.getId());
                fetchScoringRulesChain.execute();
                List<ScoringRule> scoringRules = (List<ScoringRule>) fetchScoringRulesChain.getContext().get(Constants.Command.RULES);
                // since fetchRulesChain gives default empty map of rules if there is no rule in a page.
                if(scoringRules!=null && scoringRules.size()>0 && scoringRules.get(0)!=null && scoringRules.get(0).getId()!=null){
                    scoringRules.stream().forEach(rule -> {
                        rule.setId(null);
                        rule.setSysCreatedTime(null);
                        rule.setSysModifiedTime(null);
                        rule.setSysModifiedBy(null);
                        rule.setSysCreatedBy(AccountUtil.getCurrentUser().getId());
                        if(oldQuestionIdVsNewMap.containsKey(rule.getQuestionId())){
                            Long clonedQuestionId = oldQuestionIdVsNewMap.get(rule.getQuestionId());
                            rule.setQuestionId(clonedQuestionId);
                        }
                        QuestionContext currentQuestion = questions.stream()
                                .filter(question -> question.getId()==rule.getQuestionId())
                                .findFirst()
                                .orElse(null);
                        try {
                            if(currentQuestion!=null && rule.getConditions()!=null) {
                                rule.getQuestionType().getRuleHandler().constructConditionsForClone(QAndARuleType.SCORING, currentQuestion, rule.getConditions());
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    });
                    List<Map<String, Object>> rulesList = FieldUtil.getAsMapList(scoringRules,QAndARule.class);
                    FacilioChain addOrUpdateRulesChain = QAndARuleTransactionChainFactory.addRules();
                    FacilioContext context1 = addOrUpdateRulesChain.getContext();
                    context1.put(Constants.Command.RULE_TYPE, QAndARuleType.SCORING);
                    context1.put(FacilioConstants.QAndA.Command.PAGE_ID, clonedPage.getId());
                    context1.put(Constants.Command.RULES, rulesList);

                    addOrUpdateRulesChain.execute();
                    List<QAndARule> clonedScoringRules = (List<QAndARule>) context1.get(Constants.Command.RULES);
                }

                // Rules Clone
                FacilioChain fetchRulesChain = QAndARuleReadOnlyChainFactory.fetchRulesChain();
                fetchRulesChain.getContext().put(com.facilio.qa.rules.Constants.Command.RULE_TYPE, QAndARuleType.WORKFLOW);
                fetchRulesChain.getContext().put(FacilioConstants.QAndA.Command.PAGE_ID, page.getId());
                fetchRulesChain.execute();
                List<QAndARule> rules = (List<QAndARule>) fetchRulesChain.getContext().get(Constants.Command.RULES);
                if(rules!=null && rules.size()>0 && rules.get(0)!=null && rules.get(0).getId()!=null){
                    rules.stream().forEach(rule -> {
                        rule.setId(null);
                        rule.setSysCreatedTime(null);
                        rule.setSysModifiedTime(null);
                        rule.setSysModifiedBy(null);
                        rule.setSysCreatedBy(AccountUtil.getCurrentUser().getId());
                        if(oldQuestionIdVsNewMap.containsKey(rule.getQuestionId())){
                            Long clonedQuestionId = oldQuestionIdVsNewMap.get(rule.getQuestionId());
                            rule.setQuestionId(clonedQuestionId);
                        }
                        // getting the current question for the rule
                        QuestionContext currentQuestion = questions.stream()
                                .filter(question -> question.getId()==rule.getQuestionId())
                                .findFirst()
                                .orElse(null);
                        try {
                            if(currentQuestion!=null && rule.getConditions()!=null) {
                                rule.getQuestionType().getRuleHandler().constructConditionsForClone(QAndARuleType.WORKFLOW, currentQuestion, rule.getConditions());
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    });
                    List<Map<String, Object>> rulesList = FieldUtil.getAsMapList(rules,QAndARule.class);
                    FacilioChain addOrUpdateRulesChain = QAndARuleTransactionChainFactory.addRules();
                    FacilioContext context1 = addOrUpdateRulesChain.getContext();
                    context1.put(Constants.Command.RULE_TYPE, QAndARuleType.WORKFLOW);
                    context1.put(FacilioConstants.QAndA.Command.PAGE_ID, clonedPage.getId());
                    context1.put(Constants.Command.RULES, rulesList);

                    addOrUpdateRulesChain.execute();
                    List<QAndARule> clonedRules = (List<QAndARule>) context1.get(Constants.Command.RULES);
                }
            }
        }
        return false;
    }

    private void updateQuestionProps (QuestionContext question, PageContext clonedPage) throws Exception {
        question.setClonedQuestionId(question._getId());
        question._setId(null);
        question.setPage(clonedPage);
        question.getQuestionType().getRuleHandler().beforeQuestionClone(question);
    }
}
