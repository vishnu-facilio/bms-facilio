package com.facilio.bmsconsoleV3.commands.workorder;

import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import org.apache.commons.chain.Context;

public class SkipModuleCriteriaForSummaryCommand extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception {
        // ModuleState criteria is applicable only for listing;
        context.put(FacilioConstants.ContextNames.SKIP_MODULE_CRITERIA, true);
        return false;
    }
}
