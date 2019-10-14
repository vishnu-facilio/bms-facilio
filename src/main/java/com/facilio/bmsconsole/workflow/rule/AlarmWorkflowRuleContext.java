package com.facilio.bmsconsole.workflow.rule;

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
    public RuleType getRuleTypeEnum() {
        return RuleType.ALARM_WORKFLOW_RULE;
    }

    @Override
    public boolean evaluateMisc(String moduleName, Object record, Map<String, Object> placeHolders, FacilioContext context) throws Exception {
        return super.evaluateMisc(moduleName, record, placeHolders, context);
    }
}
