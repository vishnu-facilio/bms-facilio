package com.facilio.remotemonitoring.context;

import com.facilio.accounts.dto.Group;
import com.facilio.agentv2.controller.Controller;
import com.facilio.bmsconsoleV3.context.V3ClientContext;
import com.facilio.bmsconsoleV3.context.V3PeopleContext;
import com.facilio.bmsconsoleV3.context.V3SiteContext;
import com.facilio.bmsconsoleV3.context.V3WorkOrderContext;
import com.facilio.bmsconsoleV3.context.asset.V3AssetContext;
import com.facilio.bmsconsoleV3.context.peoplegroup.V3PeopleGroupContext;
import com.facilio.fsm.context.ServiceOrderContext;
import com.facilio.bmsconsoleV3.context.controlActions.V3ControlActionContext;
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
    private FlaggedEventRuleContext flaggedAlarmProcess;
    private Controller controller;
    private FlaggedEventStatus status;
    private V3WorkOrderContext workorder;
    private ServiceOrderContext serviceOrder;
    private V3PeopleGroupContext team;
    private V3PeopleContext assignedPeople;
    private FlaggedEventBureauActionsContext currentBureauActionDetail;
    private FlaggedEventRuleClosureConfigContext flaggedEventRuleClosureConfig;
    private Long actionRemainingTime;
    List<BureauCloseIssueReasonOptionContext> bureauCloseIssues;
    private V3SiteContext site;
    private V3AssetContext asset;
    private V3ControlActionContext controlAction;
    private FlaggedEventSourceType sourceType;

    public enum FlaggedEventStatus implements FacilioStringEnum {
        OPEN("Open"),
        WORKORDER_CREATED("Workorder Created"),
        CLEARED("Closed"),
        AUTO_CLOSED("Auto Closed"),
        SUSPENDED("Suspended"),
        TIMEOUT("Timeout");
        @Override
        public String getValue() {
            return this.displayName;
        }
        private String displayName;
        FlaggedEventStatus(String displayName) {
            this.displayName = displayName;
        }
    }


    public enum FlaggedEventSourceType implements FacilioStringEnum {
        EVENT("Event"),
        MANUAL("Manual");

        @Getter
        @Setter
        private String displayName;

        @Override
        public String getValue() {
            return displayName;
        }

        FlaggedEventSourceType(String displayName) {
            this.displayName = displayName;
        }
    }

}