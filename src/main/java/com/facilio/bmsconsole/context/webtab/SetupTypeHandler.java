package com.facilio.bmsconsole.context.webtab;

import com.facilio.bmsconsole.context.WebTabContext;
import com.facilio.bmsconsole.util.ApplicationApi;
import com.facilio.bmsconsoleV3.util.V3PermissionUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class SetupTypeHandler implements WebTabHandler{
    @Override
    public boolean hasPermission(WebTabContext webtab, Map<String, String> parameters, String action) {
        boolean isSetupTab = parameters.containsKey("setupTab");
        String setupTabType = parameters.get("setupTab");
        if(isSetupTab) {
            return currentUserHasPermission(webtab,setupTabType,action);
        }
        return true;
    }

    @Override
    public boolean hasPermission(long tabId, Map<String, String> parameters, String action) throws Exception {
        WebTabContext tab = ApplicationApi.getWebTab(tabId);
        return hasPermission(tab,parameters,action);
    }


    public static boolean currentUserHasPermission(WebTabContext tab,String setupTabType, String action) {
        try {
            boolean passedTabTypeCheck = false;
            if (StringUtils.isNotEmpty(setupTabType)) {
                List<String> tabTypesSupportedForRequest = Arrays.asList(StringUtils.split(setupTabType, ","));
                if (CollectionUtils.isNotEmpty(tabTypesSupportedForRequest)) {
                    if (tabTypesSupportedForRequest.contains(tab.getTypeEnum().name())) {
                        passedTabTypeCheck = true;
                    }
                }
            }
            if (!passedTabTypeCheck){
                return false;
            }
            if(tab != null) {
                return V3PermissionUtil.currentUserHasPermission(tab.getId(), action);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
