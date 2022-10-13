package com.facilio.bmsconsoleV3.commands;

import com.facilio.bmsconsoleV3.util.ScopingUtil;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import org.apache.commons.chain.Context;

public class GetValueGeneratorsForGlobalScopeCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
        context.put(FacilioConstants.ContextNames.VALUE_GENERATORS,ScopingUtil.getApplicableValueGenerators(moduleName));
        return false;
    }
}
