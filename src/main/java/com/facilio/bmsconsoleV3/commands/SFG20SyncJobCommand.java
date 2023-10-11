package com.facilio.bmsconsoleV3.commands;

import com.facilio.bmsconsoleV3.context.SFG20JobPlan.SFG20SettingsContext;
import com.facilio.bmsconsoleV3.context.SFG20JobPlan.SFG20SyncHistoryContext;
import com.facilio.bmsconsoleV3.util.SFG20JobPlanAPI;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.taskengine.job.FacilioJob;
import com.facilio.tasker.FacilioTimer;
import org.apache.commons.chain.Context;

public class SFG20SyncJobCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        SFG20SyncHistoryContext historyContext = (SFG20SyncHistoryContext) context.get(FacilioConstants.ContextNames.SFG20.SYNC_HISTORY);
        FacilioTimer.scheduleOneTimeJobWithTimestampInSec(historyContext.getId(), "SFGJobplanSyncJob", 0, "priority");
        return false;
    }
}