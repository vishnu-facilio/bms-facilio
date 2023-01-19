package com.facilio.bmsconsole.commands;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.StringOperators;
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

        Criteria criteria = new Criteria();
        criteria.addAndCondition(CriteriaAPI.getCondition("NAME", "name", StringUtils.join(systemModuleNames, ","), StringOperators.IS));
        if (StringUtils.isNotEmpty(searchString)) {
            criteria.addAndCondition(CriteriaAPI.getCondition("DISPLAY_NAME", "displayName", searchString, StringOperators.CONTAINS));
        }

        systemModules = modBean.getModuleList(criteria);

        if (pagination != null) {
            int page = (int) pagination.get("page");
            int perPage = (int) pagination.get("perPage");
            int fromIndex = ((page-1) * perPage);
            int toIndex =  fromIndex + perPage;
            int listSize = systemModules.size();
            if (fromIndex < 0 || fromIndex > listSize) {
                fromIndex = 0;
            }
            if (toIndex == 0 || toIndex > listSize) {
                toIndex = listSize;
            }
            systemModules = systemModules.subList(fromIndex, toIndex);
        }

        context.put(FacilioConstants.ContextNames.MODULE_LIST, systemModules);
        return false;
    }
}
