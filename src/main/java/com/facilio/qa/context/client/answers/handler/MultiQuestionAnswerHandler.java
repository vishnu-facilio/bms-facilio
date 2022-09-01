package com.facilio.qa.context.client.answers.handler;

import com.facilio.qa.context.client.answers.MatrixAnswerContext;
import com.facilio.qa.rules.pojo.ScoringRule;
import com.facilio.qa.rules.pojo.ScoringRuleCondition;
import org.apache.commons.collections4.CollectionUtils;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class MultiQuestionAnswerHandler extends MatrixAnswerHandler{

    public MultiQuestionAnswerHandler(Class<MatrixAnswerContext> answerClass) {
        super(answerClass);
    }

    @Override
    public double computeFullScore(List<ScoringRuleCondition> conditions) {

        double fullScore = 0.0;
        if (CollectionUtils.isNotEmpty(conditions)) {

            Set<Long> columIds = new HashSet<>();
            for (ScoringRuleCondition condition : conditions) {
                columIds.add(condition.getColumnId());
            }

            Map<Long,List<ScoringRuleCondition>> columnVsConditions = conditions.stream().collect(Collectors.groupingBy(ScoringRuleCondition::getColumnId));
            for (Long columnId : columIds) {
                fullScore += ScoringRule.computeMaxScore(columnVsConditions.get(columnId));;
            }
        }
        return fullScore;
    }
}
