package com.facilio.fsm.commands.timeSheet;

import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.fsm.util.ServiceTaskUtil;
import org.apache.commons.chain.Context;

public class StopTimeSheetCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        Long recordId = (Long) context.get(FacilioConstants.ContextNames.RECORD_ID);
        ServiceTaskUtil.stopTimeSheet(recordId);
        return false;
    }
}
