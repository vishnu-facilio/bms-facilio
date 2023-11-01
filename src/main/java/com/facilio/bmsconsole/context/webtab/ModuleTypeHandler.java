package com.facilio.bmsconsole.context.webtab;

import com.facilio.bmsconsole.context.WebTabContext;
import com.facilio.bmsconsole.util.ApplicationApi;
import com.facilio.bmsconsole.util.WebTabUtil;
import com.facilio.bmsconsoleV3.util.V3PermissionUtil;
import lombok.extern.log4j.Log4j;

import java.util.Map;
@Log4j
public class ModuleTypeHandler implements WebTabHandler {
    @Override
    public boolean hasPermission(WebTabContext webtab, Map<String,String> parameters, String action) {
        boolean isAttachmentAPI = Boolean.parseBoolean(parameters.get("isAttachmentApi"));
        try {
            if (isAttachmentAPI) {
                return WebTabUtil.isAttachmentAPIAccessible();
            }
        }catch (Exception ex){
            LOGGER.info("Error in AttachmentAPI permission check");
            return false;
        }

        return V3PermissionUtil.currentUserHasPermission(webtab.getId(),action);
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