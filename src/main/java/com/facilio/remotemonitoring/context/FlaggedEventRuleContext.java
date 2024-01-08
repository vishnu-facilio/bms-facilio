package com.facilio.remotemonitoring.context;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.AttachmentContext;
import com.facilio.bmsconsole.workflow.rule.WorkflowRuleContext;
import com.facilio.bmsconsoleV3.context.V3ClientContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.bmsconsoleV3.context.controlActions.V3ControlActionContext;
import com.facilio.bmsconsoleV3.context.controlActions.V3ControlActionTemplateContext;
import com.facilio.db.criteria.Criteria;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioStringEnum;
import com.facilio.remotemonitoring.handlers.ticketmodulecreate.ServiceOrderRecordCreationHandler;
import com.facilio.remotemonitoring.handlers.ticketmodulecreate.TicketModuleRecordCreationHandler;
import com.facilio.remotemonitoring.handlers.ticketmodulecreate.WorkOrderRecordCreationHandler;
import com.facilio.remotemonitoring.utils.RemoteMonitorUtils;
import com.facilio.telemetry.context.TelemetryCriteriaContext;
import com.facilio.v3.context.V3Context;
import com.facilio.workflows.context.WorkflowContext;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Map;

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
    private Boolean autoCreateWorkOrder;
    private Long ticketModuleId;
    private String ticketModuleName;
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
    private List<V3ControlActionTemplateContext> controlActionTemplate;
    private List<TelemetryCriteriaContext> onCreateTelemetryCriteria;
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

    public TicketModuleRecordCreationHandler getTicketModuleRecordCreationHandler() throws Exception {
        String ticketModuleName = RemoteMonitorUtils.getTicketModuleName(this);
        if (StringUtils.isNotEmpty(ticketModuleName)) {
            switch (ticketModuleName) {
                case FacilioConstants.ContextNames.WORK_ORDER:
                    return new WorkOrderRecordCreationHandler();
                case FacilioConstants.ContextNames.SERVICE_ORDER:
                    return new ServiceOrderRecordCreationHandler();
            }
        }
        return null;
    }
}
