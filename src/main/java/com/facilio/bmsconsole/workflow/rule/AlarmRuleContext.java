package com.facilio.bmsconsole.workflow.rule;

import com.facilio.readingrule.context.NewReadingRuleContext;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.struts2.json.annotations.JSON;

import java.util.ArrayList;
import java.util.List;

public class AlarmRuleContext {

    private ReadingRuleContext preRequsite;
    private WorkflowRuleContext reportDowntimeRule;
    private List<ReadingRuleContext> alarmTriggerRuleVersionHistory;
    private ReadingRuleInterface alarmTriggerRule;
    private List<Long> alarmRCARules;

    private ReadingRuleContext alarmClearRule;
    private ReadingRuleContext alarmClearRuleDuplicate;

    public AlarmRuleContext() {
    }

    public List<ReadingRuleContext> getAlarmTriggerRuleVersionHistory() {
        return alarmTriggerRuleVersionHistory;
    }

    public void addAlarmTriggerRuleVersionHistory(ReadingRuleContext alarmTriggerRule) {
        this.alarmTriggerRuleVersionHistory = this.alarmTriggerRuleVersionHistory == null ? new ArrayList<>() : this.alarmTriggerRuleVersionHistory;
        this.alarmTriggerRuleVersionHistory.add(alarmTriggerRule);
    }

    public AlarmRuleContext(List<ReadingRuleContext> rules) {
        for (ReadingRuleContext rule : rules) {

            if (rule.getRuleTypeEnum().equals(WorkflowRuleContext.RuleType.READING_RULE)) {
                preRequsite = rule;
            } else if (rule.getRuleTypeEnum().equals(WorkflowRuleContext.RuleType.ALARM_CLEAR_RULE) && !rule.isOnSuccess()) {
                if (alarmClearRule == null) {
                    alarmClearRule = rule;
                } else {
                    alarmClearRuleDuplicate = rule;
                }
            } else if (rule.getRuleTypeEnum().equals(WorkflowRuleContext.RuleType.ALARM_TRIGGER_RULE)) {
                if (rule.isActive()) {
                    alarmTriggerRule = rule;
                } else {
                    // addAlarmTriggerRuleVersionHistory(rule);
                }
            } else if (rule.getRuleTypeEnum().equals(WorkflowRuleContext.RuleType.ALARM_RCA_RULES)) {

                if (rule.isActive()) {
                    addAlarmRCARules(rule.getId());
                } else {
//					addAlarmRCARulesVersionHistory(rule.getVersionGroupId(),rule);
                }
            }
        }
    }

    public AlarmRuleContext(List<ReadingRuleContext> rules, List<ReadingAlarmRuleContext> readingAlarmRuleContexts) {
        this(rules);
        this.readingAlarmRuleContexts = readingAlarmRuleContexts;
    }

    public AlarmRuleContext(NewReadingRuleContext rule) {
        this.alarmTriggerRule = rule;
    }

    public ReadingRuleInterface getAlarmTriggerRule() {
        return alarmTriggerRule;
    }

    public void setAlarmTriggerRule(ReadingRuleContext alarmTriggerRule) {
        this.alarmTriggerRule = alarmTriggerRule;
    }

    public List<Long> getAlarmRCARules() {
        return alarmRCARules;
    }

    public void setAlarmRCARules(List<Long> alarmRCARules) {
        this.alarmRCARules = alarmRCARules;
    }

    public void addAlarmRCARules(List<Long> alarmRCARules) {
        if (this.alarmRCARules == null) {
            this.alarmRCARules = new ArrayList<>();
        }
        this.alarmRCARules.addAll(alarmRCARules);
    }

    public void setAlarmRCARulesInt(List<Integer> rules) {
        if (CollectionUtils.isNotEmpty(rules)) {
            for (int rule : rules) {
                alarmRCARules.add((long) rule);
            }
        }
    }

    public void addAlarmRCARules(Long alarmRuleId) {
        if (this.alarmRCARules == null) {
            this.alarmRCARules = new ArrayList<>();
        }
        this.alarmRCARules.add(alarmRuleId);
    }

    public ReadingRuleContext getAlarmClearRule() {
        return alarmClearRule;
    }

    public ReadingRuleContext getPreRequsite() {
        return preRequsite;
    }

    public void setPreRequsite(ReadingRuleContext preRequsite) {
        this.preRequsite = preRequsite;
    }

    public void setAlarmClearRule(ReadingRuleContext alarmClearRule) throws Exception {        // do not use this method anywhere in server
        this.alarmClearRule = alarmClearRule;
        if (alarmClearRule != null) {
            this.alarmClearRuleDuplicate = (ReadingRuleContext) alarmClearRule.clone();
        }
    }

    @JSON(serialize = false)
    public ReadingRuleContext getAlarmClearRuleDuplicate() {
        return alarmClearRuleDuplicate;
    }

    public void setAlarmClearRuleDuplicate(ReadingRuleContext alarmClearRuleDuplicate) {
        this.alarmClearRuleDuplicate = alarmClearRuleDuplicate;
    }

    List<ReadingAlarmRuleContext> readingAlarmRuleContexts;

    public List<ReadingAlarmRuleContext> getReadingAlarmRuleContexts() {
        return readingAlarmRuleContexts;
    }

    public void setReadingAlarmRuleContexts(List<ReadingAlarmRuleContext> readingAlarmRuleContexts) {
        this.readingAlarmRuleContexts = readingAlarmRuleContexts;
    }

    private boolean reportBreakdown;

    public boolean isReportBreakdown() {
        return reportBreakdown;
    }

    public void setReportBreakdown(boolean reportBreakdown) {
        this.reportBreakdown = reportBreakdown;
    }

    private boolean isAutoClear;

    public boolean isAutoClear() {
        return isAutoClear;
    }

    public void setAutoClear(boolean isAutoClear) {
        this.isAutoClear = isAutoClear;
    }

    public void setIsAutoClear(boolean isAutoClear) {
        this.isAutoClear = isAutoClear;
    }

    public WorkflowRuleContext getReportDowntimeRule() {
        return reportDowntimeRule;
    }

    public void setReportDowntimeRule(WorkflowRuleContext reportDowntimeRule) {
        this.reportDowntimeRule = reportDowntimeRule;
    }

    public Long getGroupId() {
        if (preRequsite != null) {
            return preRequsite.getId();
        }
        return alarmTriggerRule.getId();
    }

    private List<AlarmWorkflowRuleContext> workflowRulesForAlarms;

    public List<AlarmWorkflowRuleContext> getWorkflowRulesForAlarms() {
        return workflowRulesForAlarms;
    }

    public void setWorkflowRulesForAlarms(List<AlarmWorkflowRuleContext> workflowRulesForAlarms) {
        this.workflowRulesForAlarms = workflowRulesForAlarms;
    }

    public void setNullForResponse() {
        if (this.alarmTriggerRule instanceof NewReadingRuleContext) {
            ((NewReadingRuleContext) this.alarmTriggerRule).setNullForResponse();
        }
    }
}
