package com.facilio.bmsconsoleV3.commands;

import com.facilio.bmsconsoleV3.util.GlobalScopeUtil;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import org.apache.commons.chain.Context;

public class GlobalSwitchAccessiblityCommandV3 extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {

        String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
        boolean isAccessible = GlobalScopeUtil.moduleIsSwitchAccessible(moduleName);
        context.put(FacilioConstants.ContextNames.IS_ACCESSIBLE, isAccessible);
        return false;
    }
}
