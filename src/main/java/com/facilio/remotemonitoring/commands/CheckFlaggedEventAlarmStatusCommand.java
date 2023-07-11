package com.facilio.remotemonitoring.commands;

import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.remotemonitoring.compute.FlaggedEventUtil;
import org.apache.commons.chain.Context;

public class CheckFlaggedEventAlarmStatusCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        Long id = (Long) context.get(FacilioConstants.ContextNames.ID);
        if (id != null) {
            FlaggedEventUtil.closeFlaggedEvent(id, true);
        }
        return false;
    }
}
