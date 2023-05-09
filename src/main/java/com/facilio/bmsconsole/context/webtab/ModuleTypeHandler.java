package com.facilio.bmsconsole.context.webtab;

import com.facilio.accounts.dto.NewPermission;
import com.facilio.accounts.dto.Role;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.accounts.util.PermissionUtil;
import com.facilio.bmsconsole.context.WebTabContext;
import com.facilio.bmsconsole.util.ApplicationApi;
import com.facilio.bmsconsoleV3.util.V3PermissionUtil;
import com.facilio.constants.FacilioConstants;
import com.opensymphony.xwork2.ActionContext;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.struts2.dispatcher.Parameter;

import java.util.List;
import java.util.Map;

public class ModuleTypeHandler implements WebTabHandler {
    @Override
    public boolean hasPermission(WebTabContext webtab, Map<String,String> parameters, String action) {
        String moduleName = parameters.get(FacilioConstants.ContextNames.WebTab.MODULE_NAME);
        String parentModuleName = parameters.get(FacilioConstants.ContextNames.WebTab.PARENT_MODULE_NAME);
        Boolean isFileApi = Boolean.valueOf(parameters.get("isFileApi"));
        if(isFileApi != null && isFileApi && webtab != null) {
            try {
                List<String> moduleNames = ApplicationApi.getModulesForTab(webtab.getId());
                if(CollectionUtils.isNotEmpty(moduleNames)) {
                    moduleName = moduleNames.get(0);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if(parentModuleName != null) {
            moduleName = parentModuleName;
        }
        if(V3PermissionUtil.isWhitelistedModule(moduleName)){
            return true;
        } else {
            return V3PermissionUtil.currentUserHasPermission(webtab,moduleName,action, AccountUtil.getCurrentUser().getRole());
        }
    }

    @Override
    public boolean hasPermission(long tabId, Map<String,String> parameters, String action) throws Exception {
        WebTabContext tab = ApplicationApi.getWebTab(tabId);
        if(tab != null) {
            return hasPermission(tab,parameters,action);
        }
        return false;
    }


}