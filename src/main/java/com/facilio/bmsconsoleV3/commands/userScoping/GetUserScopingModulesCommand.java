package com.facilio.bmsconsoleV3.commands.userScoping;

import com.facilio.bmsconsoleV3.util.ScopingUtil;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import org.apache.commons.chain.Context;

public class GetUserScopingModulesCommand  extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        context.put(FacilioConstants.ContextNames.MODULE_LIST, ScopingUtil.getUserScopingModulesList());
        return false;
    }
}
