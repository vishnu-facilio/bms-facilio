package com.facilio.bmsconsoleV3.commands;

import com.facilio.bmsconsole.context.ApplicationLayoutContext;
import com.facilio.bmsconsole.util.ApplicationApi;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;

public class AddSetupLayoutTabsCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        long mainAppId = (long) context.get(FacilioConstants.ContextNames.APP_ID);
        ApplicationLayoutContext setupLayout = new ApplicationLayoutContext();
        List<ApplicationLayoutContext> setupLayoutAsList = ApplicationApi.getLayoutsForAppLayoutType(mainAppId, null, ApplicationLayoutContext.LayoutDeviceType.SETUP.getIndex());
        if (CollectionUtils.isNotEmpty(setupLayoutAsList)) {
            setupLayout = setupLayoutAsList.get(0);
        }
        ApplicationApi.addSetupLayoutWebGroups(setupLayout);
        return false;
    }
}
