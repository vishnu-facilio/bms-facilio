package com.facilio.bmsconsoleV3.commands;

import com.facilio.bmsconsoleV3.util.ScopingUtil;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.FacilioModule;
import org.apache.commons.chain.Context;
import java.util.List;
public class GetGlobalScopeMetaCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        context.put(FacilioConstants.ContextNames.MODULE_LIST,ScopingUtil.getModulesList());
        return false;
    }
}
