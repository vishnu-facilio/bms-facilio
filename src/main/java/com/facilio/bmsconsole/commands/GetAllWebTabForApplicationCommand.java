package com.facilio.bmsconsole.commands;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.ApplicationContext;
import com.facilio.bmsconsole.context.TabIdAppIdMappingContext;
import com.facilio.bmsconsole.context.WebTabContext;
import com.facilio.bmsconsole.util.ApplicationApi;
import com.facilio.bmsconsole.util.NewPermissionUtil;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

public class GetAllWebTabForApplicationCommand extends FacilioCommand{
    @Override
    public boolean executeCommand(Context context) throws Exception {
        long appId = (long) context.get(FacilioConstants.ContextNames.APPLICATION_ID);

        ApplicationContext application = null;
        if (appId <= 0) {
            appId = AccountUtil.getCurrentUser().getApplicationId();
        }
        application = ApplicationApi.getApplicationForId(appId);
        if(application != null) {

            List<WebTabContext> webTabs = ApplicationApi.getWebTabsForApplication(appId);
            if (webTabs != null && !webTabs.isEmpty()) {
                ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
                for (WebTabContext webtab : webTabs) {
                    webtab.setPermissions(ApplicationApi.getPermissionsForWebTab(webtab.getId()));
                    List<TabIdAppIdMappingContext> tabIdAppIdMappingContextList = ApplicationApi.getTabIdModules(webtab.getId());
                    List<Long> moduleIds = new ArrayList<>();
                    List<String> specialTypes = new ArrayList<>();
                    if (tabIdAppIdMappingContextList != null && !tabIdAppIdMappingContextList.isEmpty()) {
                        for (TabIdAppIdMappingContext tabIdAppIdMappingContext : tabIdAppIdMappingContextList) {
                            if (tabIdAppIdMappingContext.getModuleId() > 0) {
                                moduleIds.add(tabIdAppIdMappingContext.getModuleId());
                            }
                            if (tabIdAppIdMappingContext.getSpecialType() != null && !tabIdAppIdMappingContext.getSpecialType().equalsIgnoreCase("null") && !tabIdAppIdMappingContext.getSpecialType().equalsIgnoreCase("")) {
                                specialTypes.add(tabIdAppIdMappingContext.getSpecialType());
                            }
                        }
                    }
                    webtab.setModuleIds(moduleIds);
                    webtab.setSpecialTypeModules(specialTypes);
                    String moduleName = "*";
                    if (webtab.getTypeEnum() == WebTabContext.Type.MODULE) {
                        if (webtab.getModuleIds() != null && !webtab.getModuleIds().isEmpty()) {
                            moduleName = modBean.getModule(webtab.getModuleIds().get(0)).getName();
                        } else if (webtab.getConfigJSON() != null) {
                            moduleName = (String) webtab.getConfigJSON().get("type");
                        }
                    }
                    webtab.setPermission(NewPermissionUtil.getPermissions(webtab.getType(), moduleName));
                    if (webtab.getTypeEnum() == WebTabContext.Type.SETTINGS) {
                        webtab.setPermission(NewPermissionUtil.getPermissionFromConfig(webtab.getType(), webtab.getConfigJSON()));
                    }
                    if (AccountUtil.getCurrentUser() != null) {
                        webtab.setPermissionVal(ApplicationApi.getRolesPermissionValForTab(webtab.getId(),
                                AccountUtil.getCurrentUser().getRoleId()));
                    }
                    List<FacilioModule> modules = new ArrayList<>();
                    if (CollectionUtils.isNotEmpty(webtab.getModuleIds())) {
                        for (Long moduleId : webtab.getModuleIds()) {
                            modules.add(modBean.getModule(moduleId));
                        }
                    }
                    if (CollectionUtils.isNotEmpty(webtab.getSpecialTypeModules())) {
                        for (String specialType : webtab.getSpecialTypeModules()) {
                            modules.add(modBean.getModule(specialType));
                        }
                    }
                    if (CollectionUtils.isNotEmpty(modules)) {
                        webtab.setModules(modules);
                    }
                }
            }
            context.put(FacilioConstants.ContextNames.WEB_TABS, webTabs);
        }
        return false;
    }
}
