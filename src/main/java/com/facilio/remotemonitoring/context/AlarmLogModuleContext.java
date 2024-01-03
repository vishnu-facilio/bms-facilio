package com.facilio.remotemonitoring.context;

import com.facilio.bmsconsoleV3.context.V3PeopleContext;
import com.facilio.bmsconsoleV3.context.peoplegroup.V3PeopleGroupContext;
import com.facilio.v3.context.V3Context;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AlarmLogModuleContext extends V3Context {
    private FlaggedEventContext.FlaggedEventStatus eventStatus;
    private String eventStatusDisplayName;
    private FlaggedEventBureauActionsContext.FlaggedEventBureauActionStatus assessmentStatus;
    private String assessmentStatusDisplayName;
    private Long startTime;
    private Long endTime;
    private FlaggedEventContext parentTicket;
    private V3PeopleContext performedBy;
    private V3PeopleGroupContext performedTeam;
    private Long duration;

    public void setEventStatus(FlaggedEventContext.FlaggedEventStatus eventStatus){
        this.eventStatus = eventStatus;
        this.eventStatusDisplayName = eventStatus.getValue();
    }

    public void setAssessmentStatus(FlaggedEventBureauActionsContext.FlaggedEventBureauActionStatus assessmentStatus){
        this.assessmentStatus = assessmentStatus;
        this.assessmentStatusDisplayName = assessmentStatus.getValue();
    }
}
