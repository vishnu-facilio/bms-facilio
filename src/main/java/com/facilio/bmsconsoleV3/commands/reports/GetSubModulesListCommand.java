package com.facilio.bmsconsoleV3.commands.reports;

import com.facilio.command.FacilioCommand;
import com.facilio.modules.FacilioModule;
import com.facilio.report.context.ReportFactoryFields;
import org.apache.commons.chain.Context;

import java.util.Set;

public class GetSubModulesListCommand extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception
    {
        String moduleName = (String) context.get("moduleName");
        if(moduleName != null)
        {
            Set<FacilioModule> subModulesList = ReportFactoryFields.getSubModulesList(moduleName);
            context.put("subModulesList", subModulesList);
        }
        return false;
    }
}
