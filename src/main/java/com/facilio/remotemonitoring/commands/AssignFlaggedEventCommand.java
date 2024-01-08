package com.facilio.remotemonitoring.commands;

import com.facilio.bmsconsoleV3.context.V3PeopleContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.remotemonitoring.compute.FlaggedEventUtil;
import com.facilio.remotemonitoring.context.FlaggedEventContext;
import com.facilio.remotemonitoring.signup.FlaggedEventBureauActionModule;
import com.facilio.remotemonitoring.signup.FlaggedEventModule;
import com.facilio.remotemonitoring.utils.RemoteMonitorUtils;
import com.facilio.util.FacilioUtil;
import com.facilio.v3.util.V3Util;
import org.apache.commons.chain.Context;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class AssignFlaggedEventCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {

        V3PeopleContext assignToPeople = (V3PeopleContext) context.get(FacilioConstants.ContextNames.ASSIGNED_TO_ID);
        if(assignToPeople == null) {
            FacilioUtil.throwIllegalArgumentException(true, "Assigning people cannot be null");
        }
        Long flaggedEventId = (Long) context.get(FacilioConstants.ContextNames.ID);
        FlaggedEventContext flaggedEventContext = FlaggedEventUtil.getFlaggedEvent(flaggedEventId);
        if(flaggedEventContext == null || flaggedEventContext.getCurrentBureauActionDetail() == null) {
            FacilioUtil.throwIllegalArgumentException(true, "Flagged event not found");
        }
        if(flaggedEventContext.getStatus() == null || flaggedEventContext.getStatus() != FlaggedEventContext.FlaggedEventStatus.OPEN) {
            FacilioUtil.throwIllegalArgumentException(true, "Cannot assign flagged event");
        }
//        Map<String,Object> eventUpdateProp = new HashMap<>();
//        Map<String,Object> peopleProp = new HashMap<>();
//        peopleProp.put("id",assignToPeople.getId());
//        eventUpdateProp.put("assignedPeople",peopleProp);
//        V3Util.updateBulkRecords(FlaggedEventModule.MODULE_NAME, eventUpdateProp, Collections.singletonList(flaggedEventId),false);
//        Map<String,Object> bureauEventProp = new HashMap<>();
//        bureauEventProp.put("id",flaggedEventContext.getCurrentBureauActionDetail().getId());
//        bureauEventProp.put("assignedPeople",peopleProp);
//        V3Util.updateBulkRecords(FlaggedEventBureauActionModule.MODULE_NAME, bureauEventProp, Collections.singletonList(flaggedEventContext.getCurrentBureauActionDetail().getId()),false);
        FlaggedEventUtil.takeCustody(flaggedEventId,assignToPeople.getId(),true);
        return false;
    }
}
