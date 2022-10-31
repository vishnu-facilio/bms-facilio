package com.facilio.bmsconsole.commands;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import org.apache.commons.chain.Context;
import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class GetSystemModulesListCommand extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception {
        JSONObject pagination = (JSONObject) context.get(FacilioConstants.ContextNames.PAGINATION);
        String searchString = (String) context.get(FacilioConstants.ContextNames.SEARCH);

        List<String> systemModuleNames = new ArrayList<>();
        List<FacilioModule> systemModules = new ArrayList<>();

        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");

        for(String moduleName : GetModulesListCommand.MODULES) {
            if (AccountUtil.isModuleLicenseEnabled(moduleName)) {
                systemModuleNames.add(moduleName);
            }
        }

        if (StringUtils.isNotEmpty(searchString)) {
            systemModuleNames.removeIf(modName -> !modName.contains(searchString));
        }

        if (pagination != null) {
            int page = (int) pagination.get("page");
            int perPage = (int) pagination.get("perPage");
            int fromIndex = ((page-1) * perPage);
            int toIndex =  fromIndex + perPage;
            int listSize = systemModuleNames.size();
            if (fromIndex < 0 || fromIndex > listSize) {
                fromIndex = 0;
            }
            if (toIndex == 0 || toIndex > listSize) {
                toIndex = listSize;
            }
            systemModuleNames = systemModuleNames.subList(fromIndex, toIndex);
        }

        systemModules = modBean.getModuleList(systemModuleNames);

        context.put(FacilioConstants.ContextNames.MODULE_LIST, systemModules);
        return false;
    }
}
