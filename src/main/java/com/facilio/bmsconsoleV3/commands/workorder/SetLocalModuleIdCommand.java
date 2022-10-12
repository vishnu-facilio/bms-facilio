package com.facilio.bmsconsoleV3.commands.workorder;

import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import org.apache.commons.chain.Context;

public class SetLocalModuleIdCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        // Set SET_LOCAL_MODULE_ID to true to add local IDs
        context.put(FacilioConstants.ContextNames.SET_LOCAL_MODULE_ID, true);
        return false;
    }
}
