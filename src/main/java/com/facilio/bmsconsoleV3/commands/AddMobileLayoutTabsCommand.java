package com.facilio.bmsconsoleV3.commands;

import com.facilio.bmsconsole.context.ApplicationLayoutContext;
import com.facilio.bmsconsole.util.ApplicationApi;
import com.facilio.bmsconsoleV3.util.OldAppMigrationUtil;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;

public class AddMobileLayoutTabsCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        long mainAppId = (long) context.get(FacilioConstants.ContextNames.APP_ID);
        List<ApplicationLayoutContext> mobileLayoutAsList = ApplicationApi.getLayoutsForAppLayoutType(mainAppId, null, ApplicationLayoutContext.LayoutDeviceType.MOBILE.getIndex());
        ApplicationLayoutContext mobileLayout = new ApplicationLayoutContext();
        if (CollectionUtils.isNotEmpty(mobileLayoutAsList)) {
            mobileLayout = mobileLayoutAsList.get(0);
        }
        ApplicationApi.addMaintenancePortalWebGroupsForMobileLayout(mobileLayout);
        OldAppMigrationUtil.addTabsForCustomModule(mobileLayout);
        return false;
    }
}
