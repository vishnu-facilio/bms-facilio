package com.facilio.qa.rules.pojo;

import com.facilio.qa.context.QuestionContext;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;

@Getter
@Setter
public class ScoringRule extends QAndARule<ScoringRuleCondition> {
    private Integer fullScore;

    @Override
    public void beforeSaveHook(QuestionContext question) {
        if (CollectionUtils.isNotEmpty(getRuleConditions())) {
            fullScore = question.getQuestionType().getAnswerHandler().computeFullScore(this.getRuleConditions()); //Answer handler cannot be null when rule is enabled
        }
    }

    public static int computeMaxScore (List<ScoringRuleCondition> conditions) {
        if (CollectionUtils.isNotEmpty(conditions)) {
            return conditions.stream().mapToInt(ScoringRuleCondition::scoreWithZeroOnNull).max().getAsInt();
        }
        return 0;
    }

    public static int computeSumScore (List<ScoringRuleCondition> conditions) {
        if (CollectionUtils.isNotEmpty(conditions)) {
            return conditions.stream().mapToInt(ScoringRuleCondition::scoreWithZeroOnNull).sum();
        }
        return 0;
    }

}
