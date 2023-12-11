package com.facilio.bmsconsole.context.webtab;

import com.facilio.bmsconsole.context.WebTabContext;
import com.facilio.bmsconsole.util.ApplicationApi;
import com.facilio.bmsconsoleV3.util.V3PermissionUtil;
import lombok.extern.log4j.Log4j;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;

@Log4j
public class FormulaBuilderTypeHandler implements WebTabHandler {
    @Override
    public boolean hasPermission(WebTabContext webTab, Map<String, String> parameters, String action) throws Exception {
        String moduleName = parameters.get("permissionModuleName");

        if (StringUtils.isEmpty(moduleName)) {
            LOGGER.info("Can't get moduleName for this Tab");
            return false;
        } else if (!(V3PermissionUtil.isSpecialModule(moduleName) && V3PermissionUtil.isModuleAccessible(moduleName, webTab.getId()))) {
            LOGGER.info(moduleName + " is not accessible in this Tab");
            return false;
        }

        return V3PermissionUtil.currentUserHasPermission(webTab.getId(), action);
    }

    @Override
    public boolean hasPermission(long tabId, Map<String, String> parameters, String action) throws Exception {
        WebTabContext tab = ApplicationApi.getWebTab(tabId);
        if (tab != null) {
            return hasPermission(tab, parameters, action);
        }
        return false;
    }
}
