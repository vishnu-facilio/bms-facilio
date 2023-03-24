package com.facilio.bmsconsoleV3.commands.shift;

import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;

public class AssignShiftActivityModuleCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        Constants.setActivityContext(context, FacilioConstants.Shift.SHIFT_ACTIVITY);
        return false;
    }
}
