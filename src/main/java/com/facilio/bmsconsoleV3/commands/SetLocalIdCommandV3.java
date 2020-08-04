package com.facilio.bmsconsoleV3.commands;

import com.facilio.bmsconsole.commands.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import org.apache.commons.chain.Context;

public class SetLocalIdCommandV3 extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        context.put(FacilioConstants.ContextNames.SET_LOCAL_MODULE_ID, true);
        return false;
    }
}
