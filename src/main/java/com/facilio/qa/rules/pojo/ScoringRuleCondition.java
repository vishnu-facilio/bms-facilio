package com.facilio.qa.rules.pojo;

import com.facilio.qa.context.AnswerContext;
import com.facilio.qa.context.QuestionContext;
import com.facilio.util.MathUtil;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ScoringRuleCondition extends RuleCondition {
    private Double score;

    public double scoreWithZeroOnNull() {
        return score == null ? 0 : score;
    }

    @Override
    public boolean hasAction() {
        return score != null;
    }

    @Override
    public void executeTrueAction(QuestionContext question, AnswerContext answer) throws Exception {
        answer.setScore(answer.scoreWithZeroOnNull() + scoreWithZeroOnNull()); // Adding score in case of multi answer questions
        answer.setScorePercent(MathUtil.calculatePercentage(answer.getScore(), answer.getFullScore()));
    }
}
