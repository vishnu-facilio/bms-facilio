package com.facilio.bmsconsole.context.webtab;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.context.WebTabContext;
import com.facilio.bmsconsole.util.ApplicationApi;
import com.facilio.bmsconsole.util.WebTabUtil;
import com.facilio.bmsconsoleV3.util.V3PermissionUtil;
import com.facilio.constants.FacilioConstants;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;
import java.util.Map;

public class CustomTypehandler implements WebTabHandler {
    @Override
    public boolean hasPermission(WebTabContext webtab, Map<String,String> parameters, String action) {
        Boolean isFileApi = Boolean.valueOf(parameters.get("isFileApi"));
        if(webtab != null && WebTabUtil.isPortfolioTab(webtab)) {
            if (isFileApi != null && isFileApi && webtab != null) {
                return V3PermissionUtil.currentUserHasPermission(webtab, action, AccountUtil.getCurrentUser().getRole());
            }
        }
        return true;
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
