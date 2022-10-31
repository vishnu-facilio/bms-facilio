package com.facilio.bmsconsole.commands;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import org.apache.commons.chain.Context;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class GetSystemModulesCountCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        String searchString = (String) context.get(FacilioConstants.ContextNames.SEARCH);

        List<String> systemModuleNames = new ArrayList<>();
        for(String moduleName : GetModulesListCommand.MODULES) {
            if (AccountUtil.isModuleLicenseEnabled(moduleName)) {
                systemModuleNames.add(moduleName);
            }
        }

        if (StringUtils.isNotEmpty(searchString)) {
            systemModuleNames.removeIf(modName -> !modName.contains(searchString));
        }

        long count = systemModuleNames.size();

        context.put(FacilioConstants.ContextNames.COUNT, count);
        return false;
    }
}
