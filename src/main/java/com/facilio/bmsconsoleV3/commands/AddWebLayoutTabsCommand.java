package com.facilio.bmsconsoleV3.commands;

import com.facilio.bmsconsole.context.ApplicationLayoutContext;
import com.facilio.bmsconsole.util.ApplicationApi;
import com.facilio.bmsconsoleV3.util.OldAppMigrationUtil;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;

public class AddWebLayoutTabsCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        long mainAppId = (long) context.get(FacilioConstants.ContextNames.APP_ID);
        List<ApplicationLayoutContext> webLayoutAsList = ApplicationApi.getLayoutsForAppLayoutType(mainAppId, null, ApplicationLayoutContext.LayoutDeviceType.WEB.getIndex());
        ApplicationLayoutContext webLayout = new ApplicationLayoutContext();
        if (CollectionUtils.isNotEmpty(webLayoutAsList)) {
            webLayout = webLayoutAsList.get(0);
        }
        ApplicationApi.addMaintenancePortalWebGroupsForWebLayout(webLayout);
        OldAppMigrationUtil.addTabsForCustomModule(webLayout);
        return false;
    }
}
