package com.facilio.bmsconsole.commands;

import com.facilio.bmsconsole.util.SystemButtonApi;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.FacilioModule;
import org.apache.commons.chain.Context;

public class AddSystemButtonForModuleCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {

        FacilioModule module = (FacilioModule) context.get(FacilioConstants.ContextNames.MODULE);
        String moduleName = module.getName();
        SystemButtonApi.addCreateButtonWithModuleDisplayName(moduleName);
        SystemButtonApi.addListEditButton(moduleName);
        SystemButtonApi.addSummaryEditButton(moduleName);
        SystemButtonApi.addListDeleteButton(moduleName);
        SystemButtonApi.addBulkDeleteButton(moduleName);
        SystemButtonApi.addExportAsCSV(moduleName);
        SystemButtonApi.addExportAsExcel(moduleName);
        return false;
    }

}
