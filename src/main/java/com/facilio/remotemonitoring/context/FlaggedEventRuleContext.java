package com.facilio.remotemonitoring.context;

import com.facilio.bmsconsole.context.AttachmentContext;
import com.facilio.bmsconsole.workflow.rule.WorkflowRuleContext;
import com.facilio.bmsconsoleV3.context.V3ClientContext;
import com.facilio.db.criteria.Criteria;
import com.facilio.modules.FacilioStatus;
import com.facilio.modules.FacilioStringEnum;
import com.facilio.v3.context.V3Context;
import com.facilio.workflows.context.WorkflowContext;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class FlaggedEventRuleContext extends V3Context {
    private String name;
    private String description;
    private Boolean status;
    private Integer priority;
    private List<FlaggedEventRuleAlarmTypeRel> flaggedEventRuleAlarmTypeRel;
    private Long siteCriteriaId;
    private Long controllerCriteriaId;
    private Criteria siteCriteria;
    private Criteria controllerCriteria;
    private V3ClientContext client;
    private FlaggedEventExecutionType executionType;
    private Boolean createWorkorder;
    private Long workorderTemplateId;
    private Boolean sendEmailNotification;
    private WorkflowRuleContext emailRule;
    private Long emailNotificationRuleId;
    private List<FlaggedEventRuleBureauEvaluationContext> flaggedEventRuleBureauEvaluationContexts;
    private WorkflowRuleContext delayedEmailRule;
    private Long delayedEmailRuleOneId;
    private Long delayedEmailRuleTwoId;
    private Long followUpEmailDelayTimeOne;
    private Long followUpEmailDelayTimeTwo;
    private List<Long> fileIds;
    private List<AttachmentContext> files;
    private FlaggedEventRuleClosureConfigContext flaggedEventRuleClosureConfig;
    private WorkflowContext workflowContext;
    private Long workflowId;
    public boolean shouldSendEmailNotification() {
        if(sendEmailNotification == null) {
            return false;
        }
        return sendEmailNotification;
    }
    public boolean shouldCreateWorkorder() {
        if(createWorkorder == null) {
            return false;
        }
        return createWorkorder;
    }
    private List<FlaggedEventWorkorderFieldMappingContext> fieldMapping;
    public enum FlaggedEventExecutionType implements FacilioStringEnum {
        EVENT_BASED("Event Based",0),
        EVERY_30_MINUTES("Every 30 Minutes",1800),
        HOURLY("Hourly",3600),
        EVERY_2_HOUR("Every 2 Hours",7200),
        EVERY_4_HOUR("Every 4 Hours",14400),
        EVERY_8_HOUR("Every 8 Hours",28800);

        @Override
        public String getValue() {
            return this.displayName;
        }

        @Getter
        @Setter
        private String displayName;
        @Getter
        @Setter
        private int period;
        FlaggedEventExecutionType(String displayName,int period) {
            this.displayName = displayName;
            this.period = period;
        }
    }
}
