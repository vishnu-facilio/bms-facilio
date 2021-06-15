package com.facilio.qa.rules.pojo;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ScoringRuleCondition extends RuleCondition {
    private Integer score;

    public int scoreWithZeroOnNull() {
        return score == null ? 0 : score;
    }

    @Override
    public boolean hasAction() {
        return score != null;
    }
}
