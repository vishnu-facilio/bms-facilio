package com.facilio.qa.rules.pojo;

import com.facilio.qa.context.AnswerContext;
import com.facilio.qa.context.QuestionContext;
import com.facilio.util.FacilioUtil;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
public class ScoringRule extends QAndARule<ScoringRuleCondition> {
    private Double fullScore;
    private Long workflowId;

    public double fullScoreWithZeroOnNull() {
        return fullScore == null ? 0 : fullScore;
    }

    @Override
    public void beforeSaveHook(QuestionContext question) {
        if (CollectionUtils.isNotEmpty(getRuleConditions())) {
            fullScore = question.getQuestionType().getAnswerHandler().computeFullScore(this.getRuleConditions()); //Answer handler cannot be null when rule is enabled
        }
    }

    public static double computeMaxScore (List<ScoringRuleCondition> conditions) {
        if (CollectionUtils.isNotEmpty(conditions)) {
            return conditions.stream().mapToDouble(ScoringRuleCondition::scoreWithZeroOnNull).max().getAsDouble();
        }
        return 0;
    }

    public static double computeSumScore (List<ScoringRuleCondition> conditions) {
        if (CollectionUtils.isNotEmpty(conditions)) {
            return conditions.stream().collect(Collectors.summingDouble(ScoringRuleCondition::scoreWithZeroOnNull));
        }
        return 0;
    }

    @Override
    public void manipulateAnswer(QuestionContext question, AnswerContext answer) {
        answer.setFullScore(fullScore);
        answer.setScore(0d); // Resetting existing score
    }
}
