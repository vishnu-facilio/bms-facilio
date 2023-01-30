package com.facilio.bmsconsole.util;

import com.facilio.accounts.dto.Role;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.accounts.util.PermissionUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.WebTabContext;
import com.facilio.bmsconsole.context.webtab.WebTabHandler;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import lombok.extern.log4j.Log4j;
import org.apache.struts2.dispatcher.HttpParameters;
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
        if(moduleName.equals("site") || moduleName.equals("building") || moduleName.equals("floor")) {
            moduleName = "space";
        }
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
}