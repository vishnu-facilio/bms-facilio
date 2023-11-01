package com.facilio.bmsconsole.context.webtab;

import com.facilio.bmsconsole.context.WebTabContext;
import com.facilio.bmsconsole.util.ApplicationApi;
import com.facilio.bmsconsoleV3.util.V3PermissionUtil;
import lombok.extern.log4j.Log4j;

import java.util.Map;

@Log4j
public class ReadingKpiTypeHandler implements WebTabHandler {
    @Override
    public boolean hasPermission(WebTabContext webtab, Map<String, String> parameters, String action) {
        return V3PermissionUtil.currentUserHasPermission(webtab.getId(), action);
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
