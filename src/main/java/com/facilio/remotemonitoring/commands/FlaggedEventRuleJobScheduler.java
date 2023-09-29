package com.facilio.remotemonitoring.commands;

import com.facilio.command.FacilioCommand;
import com.facilio.remotemonitoring.RemoteMonitorConstants;
import com.facilio.remotemonitoring.context.AlarmFilterCriteriaType;
import com.facilio.remotemonitoring.context.AlarmFilterRuleContext;
import com.facilio.remotemonitoring.context.FilterRuleCriteriaContext;
import com.facilio.remotemonitoring.context.FlaggedEventRuleContext;
import com.facilio.tasker.FacilioTimer;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;
import java.util.Map;

public class FlaggedEventRuleJobScheduler extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception {
        String moduleName = Constants.getModuleName(context);
        Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);
        List<FlaggedEventRuleContext> flaggedEventRules = (List<FlaggedEventRuleContext>) recordMap.get(moduleName);
        if (CollectionUtils.isNotEmpty(flaggedEventRules)) {
            for (FlaggedEventRuleContext flaggedEventRule : flaggedEventRules) {
                if(flaggedEventRule.getExecutionType() != null && flaggedEventRule.getExecutionType().getPeriod() > 0) {
                    int period = flaggedEventRule.getExecutionType().getPeriod();
                    FacilioTimer.schedulePeriodicJob(flaggedEventRule.getId(), RemoteMonitorConstants.FLAGGED_EVENT_SCHEDULED_JOB, period, period, "priority");
                }
            }
        }
        return false;
    }
}