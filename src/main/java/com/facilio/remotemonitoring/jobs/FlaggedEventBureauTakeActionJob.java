package com.facilio.remotemonitoring.jobs;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsoleV3.util.V3RecordAPI;
import com.facilio.fw.BeanFactory;
import com.facilio.remotemonitoring.beans.AlarmRuleBean;
import com.facilio.remotemonitoring.compute.FlaggedEventUtil;
import com.facilio.remotemonitoring.context.FlaggedEventBureauActionsContext;
import com.facilio.remotemonitoring.context.FlaggedEventContext;
import com.facilio.remotemonitoring.context.FlaggedEventRuleContext;
import com.facilio.remotemonitoring.signup.FlaggedEventBureauActionModule;
import com.facilio.remotemonitoring.signup.FlaggedEventModule;
import com.facilio.taskengine.job.FacilioJob;
import com.facilio.taskengine.job.JobContext;
import lombok.extern.log4j.Log4j;

import java.util.Collections;

@Log4j
public class FlaggedEventBureauTakeActionJob extends FacilioJob {
    @Override
    public void execute(JobContext jc) throws Exception {
        long flaggedEventBureauActionId = jc.getJobId();
        if (flaggedEventBureauActionId > -1) {
            AlarmRuleBean alarmBean = (AlarmRuleBean) BeanFactory.lookup("AlarmBean");
            ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
            FlaggedEventBureauActionsContext bureauAction = V3RecordAPI.getRecord(FlaggedEventBureauActionModule.MODULE_NAME, flaggedEventBureauActionId, FlaggedEventBureauActionsContext.class);
            if (bureauAction != null && bureauAction.getFlaggedEvent() != null && bureauAction.getFlaggedEvent().getId() > -1) {
                FlaggedEventContext flaggedEvent = V3RecordAPI.getRecord(FlaggedEventModule.MODULE_NAME, bureauAction.getFlaggedEvent().getId(), FlaggedEventContext.class);
                if (flaggedEvent != null) {
                    FlaggedEventRuleContext rule = alarmBean.getFlaggedEventRule(flaggedEvent.getFlaggedAlarmProcess().getId());
                    if (rule != null && (rule.shouldCreateWorkorder() || (bureauAction.getInhibitTimeStamp() != null && bureauAction.getInhibitTimeStamp() > 0)) && flaggedEvent.getStatus() != null && flaggedEvent.getStatus() == FlaggedEventContext.FlaggedEventStatus.OPEN) {
                        if(bureauAction.getTakeActionTimestamp() != null && bureauAction.getTakeActionTimestamp() > 0) {
                            if (bureauAction.getEventStatus() != null && (bureauAction.getEventStatus() == FlaggedEventBureauActionsContext.FlaggedEventBureauActionStatus.UNDER_CUSTODY || bureauAction.getEventStatus() == FlaggedEventBureauActionsContext.FlaggedEventBureauActionStatus.OPEN || bureauAction.getEventStatus() == FlaggedEventBureauActionsContext.FlaggedEventBureauActionStatus.INHIBIT)) {
                                FlaggedEventUtil.updateBureauActionStatus(bureauAction.getId(), FlaggedEventBureauActionsContext.FlaggedEventBureauActionStatus.TIME_OUT);
                            }
                            passToNextBureau(flaggedEvent, bureauAction);
                        }
                    }
                }
            }
        }
    }

    private static void passToNextBureau(FlaggedEventContext flaggedEvent, FlaggedEventBureauActionsContext bureauAction) throws Exception {
        if (flaggedEvent != null && bureauAction != null) {
            if (bureauAction.getEventStatus() != null &&
                    (
                            bureauAction.getEventStatus() == FlaggedEventBureauActionsContext.FlaggedEventBureauActionStatus.OPEN ||
                                    bureauAction.getEventStatus() == FlaggedEventBureauActionsContext.FlaggedEventBureauActionStatus.UNDER_CUSTODY ||
                                    bureauAction.getEventStatus() == FlaggedEventBureauActionsContext.FlaggedEventBureauActionStatus.ACTION_TAKEN ||
                                    bureauAction.getEventStatus() == FlaggedEventBureauActionsContext.FlaggedEventBureauActionStatus.TIME_OUT ||
                                    bureauAction.getEventStatus() == FlaggedEventBureauActionsContext.FlaggedEventBureauActionStatus.INHIBIT
                    )) {
                boolean nextBureauExist = FlaggedEventUtil.passToNextBureau(flaggedEvent, bureauAction);
                if(!nextBureauExist) {
                    FlaggedEventUtil.checkAndCreateWorkorderForFlaggedEvent(flaggedEvent.getId());
                }
            }
        }
    }
}