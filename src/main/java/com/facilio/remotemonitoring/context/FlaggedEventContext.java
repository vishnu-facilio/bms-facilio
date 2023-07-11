package com.facilio.remotemonitoring.context;

import com.facilio.accounts.dto.Group;
import com.facilio.agentv2.controller.Controller;
import com.facilio.bmsconsoleV3.context.V3ClientContext;
import com.facilio.bmsconsoleV3.context.V3PeopleContext;
import com.facilio.bmsconsoleV3.context.V3WorkOrderContext;
import com.facilio.modules.FacilioStringEnum;
import com.facilio.v3.context.V3Context;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class FlaggedEventContext extends V3Context {
    private String name;
    private V3ClientContext client;
    private FlaggedEventRuleContext flaggedEventRule;
    private Controller controller;
    private FlaggedEventStatus status;
    private V3WorkOrderContext workorder;
    private Group team;
    private V3PeopleContext assignedPeople;
    private FlaggedEventBureauActionsContext currentBureauActionDetail;
    private FlaggedEventRuleClosureConfigContext flaggedEventRuleClosureConfig;
    private Long actionRemainingTime;
    List<BureauCloseIssueReasonOptionContext> bureauCloseIssues;

    public enum FlaggedEventStatus implements FacilioStringEnum {
        OPEN("Open"),
        WORKORDER_CREATED("Workorder Created"),
        CLEARED("Closed"),
        AUTO_CLOSED("Auto Closed");
        @Override
        public String getValue() {
            return this.displayName;
        }
        private String displayName;
        FlaggedEventStatus(String displayName) {
            this.displayName = displayName;
        }
    }
}