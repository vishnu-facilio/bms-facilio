package com.facilio.remotemonitoring.commands;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsoleV3.context.V3PeopleContext;
import com.facilio.bmsconsoleV3.util.V3RecordAPI;
import com.facilio.chain.FacilioContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.remotemonitoring.RemoteMonitorConstants;
import com.facilio.remotemonitoring.compute.FlaggedEventUtil;
import com.facilio.remotemonitoring.context.FlaggedEventBureauActionsContext;
import com.facilio.remotemonitoring.context.FlaggedEventContext;
import com.facilio.remotemonitoring.context.FlaggedEventRuleBureauEvaluationContext;
import com.facilio.remotemonitoring.signup.FlaggedEventBureauActionModule;
import com.facilio.remotemonitoring.signup.FlaggedEventModule;
import com.facilio.tasker.FacilioTimer;
import com.facilio.util.FacilioUtil;
import com.facilio.v3.util.V3Util;
import org.apache.commons.chain.Context;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class FlaggedEventTakeCustodyCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        Long id = (Long) context.get(FacilioConstants.ContextNames.ID);
        Long currentPeopleId = AccountUtil.getCurrentUser().getPeopleId();
        FacilioUtil.throwIllegalArgumentException(currentPeopleId == null, "Current People Id Cannot be null");
        FacilioUtil.throwIllegalArgumentException(id == null, "Flagged Event Id Cannot be null");
        takeCustody(id,currentPeopleId);
        return false;
    }

    private static void takeCustody(Long id,Long currentPeopleId) throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FlaggedEventContext flaggedEvent = FlaggedEventUtil.getFlaggedEvent(id);
        if(flaggedEvent != null && flaggedEvent.getCurrentBureauActionDetail() != null) {
            FlaggedEventBureauActionsContext bureauAction = V3RecordAPI.getRecord(FlaggedEventBureauActionModule.MODULE_NAME, flaggedEvent.getCurrentBureauActionDetail().getId(), FlaggedEventBureauActionsContext.class);
            if(bureauAction != null) {
                if(bureauAction.getEventStatus() != null && bureauAction.getEventStatus() == FlaggedEventBureauActionsContext.FlaggedEventBureauActionStatus.UNDER_CUSTODY) {
                    FacilioUtil.throwIllegalArgumentException(true, "Already Under Custody");
                }
                if(bureauAction.getEventStatus() != null && bureauAction.getEventStatus().getState() == FlaggedEventBureauActionsContext.FlaggedEventBureauActionState.INACTIVE) {
                    FacilioUtil.throwIllegalArgumentException(true, "Restricted action. Event already closed or passed to next bureau");
                }
                FlaggedEventBureauActionsContext updateAction = new FlaggedEventBureauActionsContext();
                updateAction.setId(bureauAction.getId());
                V3PeopleContext people = new V3PeopleContext();
                people.setId(AccountUtil.getCurrentUser().getPeopleId());
                updateAction.setAssignedPeople(people);
                updateAction.setEventStatus(FlaggedEventBureauActionsContext.FlaggedEventBureauActionStatus.UNDER_CUSTODY);
                updateAction.setTakeCustodyTimestamp(System.currentTimeMillis());
                V3RecordAPI.updateRecord(updateAction,modBean.getModule(FlaggedEventBureauActionModule.MODULE_NAME), Arrays.asList(modBean.getField("eventStatus",FlaggedEventBureauActionModule.MODULE_NAME),modBean.getField("takeCustodyTimestamp",FlaggedEventBureauActionModule.MODULE_NAME),modBean.getField("assignedPeople",FlaggedEventBureauActionModule.MODULE_NAME)));
                startTakeActionJob(bureauAction);
            }
            Map<String,Object> eventUpdateProp = new HashMap<>();
            Map<String,Object> peopleProp = new HashMap<>();
            peopleProp.put("id",currentPeopleId);
            eventUpdateProp.put("assignedPeople",peopleProp);
            V3Util.updateBulkRecords(FlaggedEventModule.MODULE_NAME, eventUpdateProp,Collections.singletonList(id),false);
        }
    }

    private static void startTakeActionJob(FlaggedEventBureauActionsContext action) throws Exception {
        if(action.getTakeCustodyPeriod() != null && action.getTakeCustodyPeriod() > 0) {
            Long nextExecutionTime = (System.currentTimeMillis() + action.getTakeCustodyPeriod()) / 1000;
            FacilioTimer.scheduleOneTimeJobWithTimestampInSec(action.getId(), RemoteMonitorConstants.FLAGGED_EVENT_BUREAU_TAKE_ACTION_JOB, nextExecutionTime, "priority");
        }
    }
}