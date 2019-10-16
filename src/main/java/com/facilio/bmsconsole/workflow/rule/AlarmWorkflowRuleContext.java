package com.facilio.bmsconsole.workflow.rule;

import com.facilio.bmsconsole.context.BaseAlarmContext;
import com.facilio.bmsconsole.context.ReadingAlarm;
import com.facilio.chain.FacilioContext;

import java.util.Map;

public class AlarmWorkflowRuleContext extends WorkflowRuleContext {

    private long ruleId = -1;
    public long getRuleId() {
        return ruleId;
    }
    public void setRuleId(long ruleId) {
        this.ruleId = ruleId;
    }

    @Override
    public boolean evaluateMisc(String moduleName, Object record, Map<String, Object> placeHolders, FacilioContext context) throws Exception {
        if (record instanceof ReadingAlarm) {
            ReadingAlarm readingAlarm = (ReadingAlarm) record;
            if (readingAlarm.getRule() != null) {
                return readingAlarm.getRule().getId() == getRuleId();
            }
        }
        return false;
    }
}
