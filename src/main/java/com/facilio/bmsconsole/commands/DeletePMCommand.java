package com.facilio.bmsconsole.commands;
import com.facilio.constants.FacilioConstants;
import com.facilio.tasker.FacilioTimer;
import org.apache.commons.chain.Context;

import java.util.List;

public class DeletePMCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        List<Long> recordIds = (List<Long>) context.get(FacilioConstants.ContextNames.RECORD_ID_LIST);
        for (long recordId: recordIds) {
            FacilioTimer.deleteJob(recordId, "SchedulePMBackgroundJob");
            FacilioTimer.deleteJob(recordId, "DeletePMJob");
            FacilioTimer.scheduleOneTimeJobWithDelay(recordId, "DeletePMJob", 1, "priority");
        }
        return false;
    }
}
