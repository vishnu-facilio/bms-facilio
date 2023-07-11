package com.facilio.remotemonitoring.context;

import com.facilio.bmsconsoleV3.context.V3PeopleContext;
import com.facilio.modules.FacilioStringEnum;
import com.facilio.remotemonitoring.handlers.timer.*;
import com.facilio.v3.context.V3Context;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter @Setter
public class FlaggedEventBureauActionsContext extends FlaggedEventRuleBureauEvaluationContext {
    private FlaggedEventContext flaggedEvent;
    private Long takeActionTimestamp;
    private Long takeCustodyTimestamp;
    private Long inhibitTimeStamp;
    private Long evaluationOpenTimestamp;
    private List<BureauInhibitReasonListContext> configuredinhibitReasonList;
    private BureauInhibitReasonListContext inhibitReason;
    private List<BureauCloseIssueReasonOptionContext> closeIssueReasonOptionList;
    private V3PeopleContext assignedPeople;
    private FlaggedEventBureauActionStatus eventStatus;
    private String troubleShootingTips;
    public enum FlaggedEventBureauActionState implements FacilioStringEnum {
        ACTIVE("Active"),
        INACTIVE("Inactive");
        private String displayName;
        FlaggedEventBureauActionState(String displayName) {
            this.displayName = displayName;
        }

        public List<FlaggedEventBureauActionStatus> getStatuses() {
            List<FlaggedEventBureauActionStatus> values = new ArrayList<>();
            for(FlaggedEventBureauActionStatus enumVal : FlaggedEventBureauActionStatus.values()) {
                if(enumVal.getState() == this) {
                    values.add(enumVal);
                }
            }
            return values;
        }
    }
    public enum FlaggedEventBureauActionStatus implements FacilioStringEnum {
        NOT_STARTED("Open",FlaggedEventBureauActionState.ACTIVE,new DefaultActionHandler()),

        OPEN("Open",FlaggedEventBureauActionState.ACTIVE,new OpenActionHandler()),
        UNDER_CUSTODY("Under Custody",FlaggedEventBureauActionState.ACTIVE,new UnderCustodyActionHandler()),
        ACTION_TAKEN("Action Taken",FlaggedEventBureauActionState.ACTIVE,new TakeActionHandler()),
        INHIBIT("Inhibit",FlaggedEventBureauActionState.ACTIVE,new InhibitActionHandler()),
        PASSED_TO_NEXT_BUREAU("Passed",FlaggedEventBureauActionState.INACTIVE,new DefaultActionHandler()),
        TIME_OUT("Time Out",FlaggedEventBureauActionState.INACTIVE,new DefaultActionHandler()),
        INACTIVE("Inactive",FlaggedEventBureauActionState.INACTIVE,new DefaultActionHandler());
        @Getter @Setter
        private String displayName;
        @Getter @Setter
        private FlaggedEventBureauActionState state;
        @Getter @Setter
        private TeamActionHandler teamActionHandler;
        @Override
        public String getValue() {
            return displayName;
        }
        FlaggedEventBureauActionStatus(String displayName,FlaggedEventBureauActionState state,TeamActionHandler teamActionHandler) {
            this.displayName = displayName;
            this.state = state;
            this.teamActionHandler = teamActionHandler;
        }
    }
}