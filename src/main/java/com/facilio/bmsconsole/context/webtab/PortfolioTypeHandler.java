package com.facilio.bmsconsole.context.webtab;

import com.facilio.bmsconsole.context.WebTabContext;
import com.facilio.bmsconsole.util.ApplicationApi;
import com.facilio.bmsconsole.util.WebTabUtil;
import com.facilio.bmsconsoleV3.util.V3PermissionUtil;

import java.util.Map;

public class PortfolioTypeHandler implements WebTabHandler {
    @Override
    public boolean hasPermission(WebTabContext webtab, Map<String, String> parameters, String action) {
        if(webtab != null && WebTabUtil.isNewPortfolioTab(webtab)) {
            return V3PermissionUtil.currentUserHasPermission(webtab.getId(), action);
        }
        return false;
    }

    @Override
    public boolean hasPermission(long tabId, Map<String, String> parameters, String action) throws Exception {
        WebTabContext tab = ApplicationApi.getWebTab(tabId);
        if(tab != null) {
            return hasPermission(tab,parameters,action);
        }
        return false;
    }
}
