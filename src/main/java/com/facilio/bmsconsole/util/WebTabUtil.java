package com.facilio.bmsconsole.util;

import com.facilio.accounts.dto.NewPermission;
import com.facilio.accounts.dto.Role;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.accounts.util.PermissionUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.ApplicationContext;
import com.facilio.bmsconsole.context.WebTabContext;
import com.facilio.bmsconsole.context.webtab.WebTabHandler;
import com.facilio.bmsconsoleV3.util.V3PermissionUtil;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.db.criteria.operators.StringOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import lombok.extern.log4j.Log4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.struts2.dispatcher.HttpParameters;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Log4j
public class WebTabUtil {
    public static WebTabHandler getWebTabHandler(WebTabContext tab) {
        return tab.getTypeEnum().getHandler();
    }
    public static boolean checkPermission(HttpParameters parameters,String action,long tabId) throws Exception {
        WebTabContext tab = ApplicationApi.getWebTab(tabId);
        if(tab == null) {
            return false;
        }
        WebTabHandler handler = getWebTabHandler(tab);
        Map<String,String> params = new HashMap<>();
        List<String> keys = FacilioConstants.HTTPParameter.KEYS;
        if(parameters != null) {
            for(String key : keys) {
                if(parameters.containsKey(key)) {
                    params.put(key, parameters.get(key).getValue());
                }
            }
        }
        if(handler != null) {
            return handler.hasPermission(tab, params, action);
        }
        return true;
    }

    public static boolean checkModulePermission(String action,String moduleName,boolean isV3Permission) throws Exception {
        Role role = AccountUtil.getCurrentUser().getRole();
        //portfolio is handled for space module - check for facilio main app
        moduleName = getSpecialModule(moduleName);
        boolean hasPerm = PermissionUtil.currentUserHasPermission(moduleName, action, role);
        if(isV3Permission) {
            if (PermissionUtil.permCheckSysModules().contains(moduleName)) {
                return hasPerm;
            } else {
                ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
                FacilioModule module = modBean.getModule(moduleName);
                if (module != null) {
                    if (module.isCustom()) {
                        return hasPerm;
                    }
                }
            }
        }
        return true;
    }


    public static String getSpecialModule(String moduleName) {
        if(moduleName.equals("site") || moduleName.equals("building") || moduleName.equals("floor")) {
            return "space";
        }
        if(moduleName.equals(FacilioConstants.ContextNames.TICKET)) {
            return FacilioConstants.ContextNames.WORK_ORDER;
        }
        return moduleName;
    }

//    Temp Solution
    public static boolean checkModulePermissionForTab(String action,String moduleName) throws Exception {
        Role role = AccountUtil.getCurrentUser().getRole();
        if(role != null && role.isPrevileged()) {
            return true;
        }
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        //portfolio is handled for portfolio tab
        moduleName = getSpecialModule(moduleName);
        FacilioModule module = modBean.getModule(moduleName);
        ApplicationContext currentApp = AccountUtil.getCurrentApp();


        if(module != null && currentApp != null) {
            List<Long> tabIds;
            if (moduleName.equals("site") || moduleName.equals("building") || moduleName.equals("floor") || moduleName.equals("space")) {
                tabIds = Collections.singletonList(getPortfolioTabId(currentApp));
            } else {
                tabIds = ApplicationApi.getTabForModules(currentApp.getId(), module.getModuleId());
            }
            if(CollectionUtils.isNotEmpty(tabIds)) {
                boolean hasPerm = currentUserHasPermission(tabIds.get(0),action,role);
                return hasPerm;
            }
        }
        return false;
    }

    private static Long getPortfolioTabId(ApplicationContext currentApp) throws Exception {
        GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
                .table(ModuleFactory.getWebTabModule().getTableName()).select(FieldFactory.getWebTabFields())
                .andCondition(CriteriaAPI.getCondition("APPLICATION_ID","applicationId",String.valueOf(currentApp.getId()), NumberOperators.EQUALS))
                .andCondition(CriteriaAPI.getCondition("CONFIG","config","portfolio", StringOperators.CONTAINS));
        Map<String,Object> prop = builder.fetchFirst();
        if(MapUtils.isNotEmpty(prop)) {
            WebTabContext tab = FieldUtil.getAsBeanFromMap(prop,WebTabContext.class);
            if(tab != null) {
                return tab.getId();
            }
        }
        return null;
    }
    public static boolean currentUserHasPermission(long tabId, String action, Role role) {

        try {
            if (V3PermissionUtil.isFeatureEnabled()) {
                NewPermission permission = ApplicationApi.getRolesPermissionForTab(tabId, role.getRoleId());
                boolean hasPerm = PermissionUtil.hasPermission(permission, action, tabId);
                return hasPerm;
            } else {
                long rolePermissionVal = ApplicationApi.getRolesPermissionValForTab(tabId, role.getRoleId());
                boolean hasPerm = PermissionUtil.hasPermission(rolePermissionVal, action, tabId);
                return hasPerm;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}