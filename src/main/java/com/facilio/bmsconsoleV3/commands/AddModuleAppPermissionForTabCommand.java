package com.facilio.bmsconsoleV3.commands;

import com.facilio.bmsconsole.context.Permission;
import com.facilio.bmsconsole.context.WebTabContext;
import com.facilio.bmsconsole.util.ApplicationApi;
import com.facilio.bmsconsoleV3.util.AppModulePermissionUtil;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;

public class AddModuleAppPermissionForTabCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        //Until table migration is done -- for backward compatability
//        WebTabContext webtab = (WebTabContext) context.get(FacilioConstants.ContextNames.WEB_TAB);
//        long tabId = (long) context.get(FacilioConstants.ContextNames.WEB_TAB_ID);
//        if(webtab == null){
//            webtab = ApplicationApi.getWebTab(tabId,true);
//        }
//        if(webtab != null) {
//            List<Permission> permissions = AppModulePermissionUtil.getPermissionValue(webtab);
//            if (CollectionUtils.isEmpty(permissions)) {
//                AppModulePermissionUtil.addModuleAppPermission(webtab);
//            }
//        }
        return false;
    }
}
