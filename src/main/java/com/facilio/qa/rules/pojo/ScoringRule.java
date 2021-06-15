package com.facilio.qa.rules.pojo;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ScoringRule extends QAndARule<ScoringRuleCondition> {
    private Long fullScore;

}
