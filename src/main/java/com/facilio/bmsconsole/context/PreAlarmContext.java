package com.facilio.bmsconsole.context;

import com.facilio.bmsconsole.workflow.rule.ReadingRuleContext;

import java.util.ArrayList;
import java.util.List;

public class PreAlarmContext extends BaseAlarmContext {
    private static final long serialVersionUID = 1L;

    private ReadingAlarmCategoryContext readingAlarmCategory;
    public ReadingAlarmCategoryContext getReadingAlarmCategory() {
        return readingAlarmCategory;
    }
    public void setReadingAlarmCategory(ReadingAlarmCategoryContext readingAlarmCategory) {
        this.readingAlarmCategory = readingAlarmCategory;
    }

    private ReadingRuleContext rule;
    public ReadingRuleContext getRule() {
        return rule;
    }
    public void setRule(ReadingRuleContext rule) {
        this.rule = rule;
    }

    private ReadingRuleContext subRule;
    public ReadingRuleContext getSubRule() {
        return subRule;
    }
    public void setSubRule(ReadingRuleContext subRule) {
        this.subRule = subRule;
    }

    private long readingFieldId;
    public long getReadingFieldId() {
        return readingFieldId;
    }
    public void setReadingFieldId(long readingFieldId) {
        this.readingFieldId = readingFieldId;
    }
}

