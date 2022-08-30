package com.facilio.bmsconsoleV3.commands;

import com.facilio.bmsconsoleV3.util.ModulePermissionUtil;
import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;

public class AddSpecialTypeModulePermissionCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        ModulePermissionUtil.addInitialModulePermission();
        return false;
    }
}
