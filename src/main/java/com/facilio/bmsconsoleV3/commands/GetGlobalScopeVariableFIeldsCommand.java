package com.facilio.bmsconsoleV3.commands;

import com.facilio.bmsconsoleV3.util.ScopingUtil;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.FacilioModule;
import org.apache.commons.chain.Context;

import java.util.List;

public class GetGlobalScopeVariableFIeldsCommand extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception {

        String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
        context.put(FacilioConstants.ContextNames.MODULE_LIST,ScopingUtil.getFieldsMapDetails(ScopingUtil.getModulesList(true),moduleName));
        return false;
    }
}
