package com.facilio.bmsconsoleV3.signup.enrgyApp;

import com.facilio.bmsconsole.context.ApplicationContext;
import com.facilio.bmsconsole.context.ApplicationLayoutContext;
import com.facilio.bmsconsole.util.ApplicationApi;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import org.apache.commons.chain.Context;

public class AddEnergyAppLayout extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        ApplicationContext energyApp = ApplicationApi.getApplicationForLinkName(FacilioConstants.ApplicationLinkNames.ENERGY_APP);
        //Web Application Layout
        ApplicationLayoutContext energyLayout = new ApplicationLayoutContext(energyApp.getId(), ApplicationLayoutContext.AppLayoutType.DUAL,
                ApplicationLayoutContext.LayoutDeviceType.WEB, FacilioConstants.ApplicationLinkNames.ENERGY_APP);
        ApplicationApi.addApplicationLayout(energyLayout);
        //Energy App SetUp Layout
        ApplicationLayoutContext energySetupLayout = new ApplicationLayoutContext(energyApp.getId(), ApplicationLayoutContext.AppLayoutType.SINGLE, ApplicationLayoutContext.LayoutDeviceType.SETUP, FacilioConstants.ApplicationLinkNames.ENERGY_APP);
        ApplicationApi.addApplicationLayout(energySetupLayout);
        //Energy App Mobile Layout
        ApplicationLayoutContext energyAppMobileLayout = new ApplicationLayoutContext(energyApp.getId(),ApplicationLayoutContext.AppLayoutType.SINGLE,ApplicationLayoutContext.LayoutDeviceType.MOBILE,FacilioConstants.ApplicationLinkNames.ENERGY_APP);
        ApplicationApi.addApplicationLayout(energyAppMobileLayout);
        return false;
    }
}
