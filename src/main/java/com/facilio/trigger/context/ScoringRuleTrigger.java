package com.facilio.trigger.context;

public class ScoringRuleTrigger extends BaseTriggerContext {

    @Override
    public TriggerType getTypeEnum() {
        return TriggerType.SCORING_RULE_TRIGGER;
    }

    @Override
    public Boolean getInternal() {
        return true;
    }
}
