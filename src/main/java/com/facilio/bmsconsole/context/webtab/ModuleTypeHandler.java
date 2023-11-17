package com.facilio.bmsconsole.context.webtab;

import com.facilio.bmsconsole.context.WebTabContext;
import com.facilio.bmsconsole.util.ApplicationApi;
import com.facilio.bmsconsole.util.WebTabUtil;
import com.facilio.bmsconsoleV3.util.V3PermissionUtil;
import lombok.extern.log4j.Log4j;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;
@Log4j
public class ModuleTypeHandler implements WebTabHandler {
    @Override
    public boolean hasPermission(WebTabContext webtab, Map<String,String> parameters, String action) throws Exception {
        boolean isAttachmentAPI = Boolean.parseBoolean(parameters.get("isAttachmentApi"));
        String moduleName = StringUtils.isNotEmpty(parameters.get("parentModuleName"))?  parameters.get("parentModuleName") : parameters.get("moduleName");;

        if(StringUtils.isNotEmpty(moduleName) && !V3PermissionUtil.isModuleAccessible(moduleName, webtab.getId())){
            LOGGER.info(moduleName+" is not accessible in this Tab");
            return false;
        }
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