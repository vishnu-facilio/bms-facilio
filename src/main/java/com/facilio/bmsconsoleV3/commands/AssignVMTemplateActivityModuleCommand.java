package com.facilio.bmsconsoleV3.commands;

import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;

public class AssignVMTemplateActivityModuleCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        Constants.setActivityContext(context, FacilioConstants.Meter.VIRTUAL_METER_TEMPLATE_ACTIVITY);
        return false;
    }
}
