package com.facilio.fsm.commands.timeSheet;

import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.fsm.util.ServiceTaskUtil;
import org.apache.commons.chain.Context;

import java.util.List;

public class StopTimeSheetCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        List<Long> recordIds = (List<Long>) context.get(FacilioConstants.ContextNames.RECORD_ID_LIST);
        ServiceTaskUtil.stopTimeSheet(recordIds);
        return false;
    }
}
