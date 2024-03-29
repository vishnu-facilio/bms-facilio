package com.facilio.bmsconsole.commands;

import com.facilio.accounts.dto.NewPermission;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.beans.WebTabBean;
import com.facilio.bmsconsole.context.*;
import com.facilio.bmsconsole.util.ApplicationApi;
import com.facilio.bmsconsole.enums.Version;
import com.facilio.bmsconsole.util.NewPermissionUtil;
import com.facilio.bmsconsoleV3.util.V3PermissionUtil;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldUtil;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class GetAllWebTabForApplicationCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        long appId = (long) context.get(FacilioConstants.ContextNames.APPLICATION_ID);
        boolean filterSetUpTab=(boolean)context.get((FacilioConstants.ContextNames.FILTER_SET_UP_TAP));
        boolean fetchSetupTabs=(boolean)context.get((FacilioConstants.ContextNames.FETCH_SETUP_TABS));
        Long roleId=(Long) context.get((FacilioConstants.ContextNames.ROLE_ID));
        boolean boolCheck = (boolean)context.getOrDefault(FacilioConstants.ContextNames.CHECK_BOOL,false);
        WebTabBean tabBean = (WebTabBean) BeanFactory.lookup("TabBean");

        ApplicationContext application = null;
        if (appId <= 0) {
            appId = AccountUtil.getCurrentUser().getApplicationId();
        }
        application = ApplicationApi.getApplicationForId(appId);
        if(application != null) {
            List<WebTabContext> webTabs = FieldUtil.getAsBeanListFromMapList(FieldUtil.getAsMapList(tabBean.getWebTabsForApplication(appId),WebTabCacheContext.class),WebTabContext.class);

            //handling this to remove webTabs based on Version
            Version currentVersion = Version.getCurrentVersion();
            if(currentVersion != null) {
                long currentVersionId = currentVersion.getVersionId();
                webTabs.removeIf(f -> f.getVersion() != null && (f.getVersion() & currentVersionId) != currentVersionId);
            }

            if (webTabs != null && !webTabs.isEmpty()) {
                ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
                if(!fetchSetupTabs) {
                    webTabs = webTabs.stream().filter(webtab -> webtab.getTypeEnum() != null && webtab.getTypeEnum().getTabType().equals(filterSetUpTab ? WebTabContext.TabType.SETUP : WebTabContext.TabType.NORMAL))
                            .collect(Collectors.toList());
                }
                for (WebTabContext webtab : webTabs) {
                    webtab.setPermissions(ApplicationApi.getPermissionsForWebTab(webtab.getId()));
                    List<TabIdAppIdMappingCacheContext> tabIdAppIdMappingContextList = tabBean.getTabIdModules(webtab.getId());
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
                    if(boolCheck){
                        webtab.setPermission(NewPermissionUtil.getTabPermissions(webtab,roleId));
                    }

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
