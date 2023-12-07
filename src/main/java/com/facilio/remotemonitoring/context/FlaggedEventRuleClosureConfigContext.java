package com.facilio.remotemonitoring.context;

import com.facilio.bmsconsole.workflow.rule.WorkflowRuleContext;
import com.facilio.fsm.context.ServiceOrderTicketStatusContext;
import com.facilio.fsm.signup.ServiceOrderTicketStatus;
import com.facilio.modules.FacilioStatus;
import com.facilio.modules.FacilioStringEnum;
import com.facilio.telemetry.context.TelemetryCriteriaContext;
import com.facilio.v3.context.V3Context;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class FlaggedEventRuleClosureConfigContext extends V3Context {

    private ClosureType closureType;
    private FlaggedEventRuleContext flaggedEventRule;
    private List<FlaggedEventContext.FlaggedEventStatus> flaggedEventStatuses;
    private List<FacilioStatus> workorderStatuses;
    private List<ServiceOrderTicketStatusContext> serviceOrderStatuses;
    private Long autoClosureDuration;
    private WorkflowRuleContext closeEmailRule;
    private Long closeEmailNotificationRuleId;
    private ClosureRestriction closureRestriction;
    private Boolean sendWorkorderCloseCommand;
    private List<FacilioStatus> workorderCloseCommandCriteria;
    private List<ServiceOrderTicketStatusContext> serviceOrderCloseCommandCriteria;
    private FacilioStatus workorderCloseStatus;
    private ServiceOrderTicketStatusContext serviceOrderCloseStatus;
    private Long flaggedEventTriggerCriteriaReevaluationTime;
    private String warningMessage;
    private Boolean autoCloseOnClear;
    private List<TelemetryCriteriaContext> onCloseTelemetryCriteria;
    public enum ClosureRestriction implements FacilioStringEnum {
        WARN("Warning"),
        RESTRICT("Restrict");
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
        ClosureRestriction(String displayName) {
            this.displayName = displayName;
        }
    }
    public enum ClosureType implements FacilioStringEnum {
        AUTOMATIC("Automatic"),
        MANUAL("Manual");

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
        ClosureType(String displayName) {
            this.displayName = displayName;
        }
    }
}
