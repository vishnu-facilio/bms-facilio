package com.facilio.bmsconsole.context.webtab;

import com.facilio.bmsconsole.context.WebTabContext;
import com.facilio.bmsconsole.util.ApplicationApi;
import com.facilio.bmsconsoleV3.util.V3PermissionUtil;

import java.util.Map;

public class MyAttendanceHandler implements WebTabHandler{
    @Override
    public boolean hasPermission(WebTabContext webtab, Map<String, String> parameters, String action) {
        try {
            if(!(webtab.getTypeEnum() == WebTabContext.Type.MY_ATTENDANCE))
                return false;
            if(webtab != null && webtab.getId() > 0) {
                return V3PermissionUtil.currentUserHasPermission(webtab.getId(), action);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean hasPermission(long tabId, Map<String, String> parameters, String action) throws Exception {
        WebTabContext tab = ApplicationApi.getWebTab(tabId);
        return hasPermission(tab,parameters,action);
    }
}
