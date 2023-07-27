package com.facilio.bmsconsoleV3.signup.fsmApp;

import com.facilio.bmsconsole.context.ApplicationContext;
import com.facilio.bmsconsole.context.ApplicationLayoutContext;
import com.facilio.bmsconsole.util.ApplicationApi;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import org.apache.commons.chain.Context;

public class AddFsmApplicationLayout extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception {
        //web layout for FSM App
        ApplicationContext fsm = ApplicationApi.getApplicationForLinkName(FacilioConstants.ApplicationLinkNames.FSM_APP);
        ApplicationLayoutContext webLayout = new ApplicationLayoutContext(fsm.getId(), ApplicationLayoutContext.AppLayoutType.SINGLE, ApplicationLayoutContext.LayoutDeviceType.WEB, FacilioConstants.ApplicationLinkNames.FSM_APP);
        webLayout.setId(ApplicationApi.getAddApplicationLayout(webLayout));
        //need to change as per FSM App
        ApplicationApi.addFsmPortalWebGroupsForWebLayout(webLayout);
        return false;
    }
}
