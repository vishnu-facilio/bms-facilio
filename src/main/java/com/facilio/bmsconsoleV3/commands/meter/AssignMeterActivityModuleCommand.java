package com.facilio.bmsconsoleV3.commands.meter;

import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;

public class AssignMeterActivityModuleCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        Constants.setActivityContext(context, FacilioConstants.Meter.METER_ACTIVITY);
        return false;
    }
}
