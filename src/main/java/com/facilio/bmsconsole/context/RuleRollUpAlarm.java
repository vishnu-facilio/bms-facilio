package com.facilio.bmsconsole.context;

import com.facilio.bmsconsole.workflow.rule.ReadingRuleContext;

public class RuleRollUpAlarm extends BaseAlarmContext {
    private static final long serialVersionUID = 1L;

    private ReadingRuleContext rule;
    public ReadingRuleContext getRule() {
        return rule;
    }
    public void setRule(ReadingRuleContext rule) {
        this.rule = rule;
    }

}
