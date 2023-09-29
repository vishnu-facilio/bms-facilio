package com.facilio.remotemonitoring.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsoleV3.util.V3RecordAPI;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.remotemonitoring.RemoteMonitorConstants;
import com.facilio.remotemonitoring.compute.FlaggedEventUtil;
import com.facilio.remotemonitoring.context.BureauInhibitReasonListContext;
import com.facilio.remotemonitoring.context.FlaggedEventBureauActionsContext;
import com.facilio.remotemonitoring.context.FlaggedEventContext;
import com.facilio.remotemonitoring.signup.BureauInhibitReasonListModule;
import com.facilio.remotemonitoring.signup.FlaggedEventBureauActionModule;
import com.facilio.taskengine.job.JobContext;
import com.facilio.tasker.FacilioTimer;
import com.facilio.util.FacilioUtil;
import org.apache.commons.chain.Context;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.List;

public class InhibitFlaggedEventCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        Long id = (Long) context.get(FacilioConstants.ContextNames.ID);
        Long inhibitReasonId = (Long) context.get(RemoteMonitorConstants.INHIBIT_REASON_ID);
        if (id != null && inhibitReasonId != null) {
            FlaggedEventContext flaggedEvent = FlaggedEventUtil.getFlaggedEvent(id);
            if (flaggedEvent != null) {
                BureauInhibitReasonListContext inhibitReasonListContext = V3RecordAPI.getRecord(BureauInhibitReasonListModule.MODULE_NAME, inhibitReasonId, BureauInhibitReasonListContext.class);
                if (inhibitReasonListContext != null) {
                    FlaggedEventBureauActionsContext actionContext = getFlaggedEventBureauAction(inhibitReasonListContext.getFlaggedEventBureauEvaluation().getId());
                    if (actionContext != null) {
                        updateInhibitTimeStamp(actionContext.getId(),inhibitReasonListContext);
                        JobContext job = FacilioTimer.getJob(actionContext.getId(), RemoteMonitorConstants.FLAGGED_EVENT_BUREAU_TAKE_ACTION_JOB);
                        Long takeCustodyTimeStamp = actionContext.getTakeCustodyTimestamp();
                        Long takeActionDuration = actionContext.getTakeActionPeriod();
                        Long nextActionMaxTime = takeCustodyTimeStamp + takeActionDuration;
                        if (job != null) {
                            FacilioTimer.deleteJob(actionContext.getId(), RemoteMonitorConstants.FLAGGED_EVENT_BUREAU_TAKE_ACTION_JOB);
                        }
                        if (nextActionMaxTime > 0) {
                            nextActionMaxTime = (nextActionMaxTime + inhibitReasonListContext.getMaxInhibitTime()) / 1000;
                            FacilioTimer.scheduleOneTimeJobWithTimestampInSec(actionContext.getId(), RemoteMonitorConstants.FLAGGED_EVENT_BUREAU_TAKE_ACTION_JOB, nextActionMaxTime, "priority");
                        }
                    }
                } else {
                    FacilioUtil.throwIllegalArgumentException(true,"Invalid inhibit reason id");
                }
            } else {
                FacilioUtil.throwIllegalArgumentException(true,"Flagged Event not found");
            }
        }
        return false;
    }

    private static FlaggedEventBureauActionsContext updateInhibitTimeStamp(Long actionId, BureauInhibitReasonListContext inhibitReason) throws Exception {
        if (actionId != null) {
            ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
            FlaggedEventBureauActionsContext updateAction = new FlaggedEventBureauActionsContext();
            updateAction.setId(actionId);
            updateAction.setTakeActionTimestamp(System.currentTimeMillis());
            updateAction.setInhibitTimeStamp(System.currentTimeMillis());
            updateAction.setEventStatus(FlaggedEventBureauActionsContext.FlaggedEventBureauActionStatus.INHIBIT);
            updateAction.setInhibitReason(inhibitReason);
            List<FacilioField> fields = Arrays.asList(modBean.getField("inhibitReason", FlaggedEventBureauActionModule.MODULE_NAME), modBean.getField("eventStatus", FlaggedEventBureauActionModule.MODULE_NAME), modBean.getField("inhibitTimeStamp", FlaggedEventBureauActionModule.MODULE_NAME),modBean.getField("takeActionTimestamp", FlaggedEventBureauActionModule.MODULE_NAME));
            V3RecordAPI.updateRecord(updateAction, modBean.getModule(FlaggedEventBureauActionModule.MODULE_NAME), fields);
            return V3RecordAPI.getRecord(FlaggedEventBureauActionModule.MODULE_NAME, actionId, FlaggedEventBureauActionsContext.class);
        }
        return null;
    }

    private static FlaggedEventBureauActionsContext getFlaggedEventBureauAction(Long actionId) throws Exception {
        if (actionId != null) {
            return V3RecordAPI.getRecord(FlaggedEventBureauActionModule.MODULE_NAME, actionId, FlaggedEventBureauActionsContext.class);
        }
        return null;
    }
}