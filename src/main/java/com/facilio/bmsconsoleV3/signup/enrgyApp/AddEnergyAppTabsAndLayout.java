package com.facilio.bmsconsoleV3.signup.enrgyApp;

import com.facilio.accounts.dto.Role;
import com.facilio.bmsconsole.context.ApplicationContext;
import com.facilio.bmsconsole.context.ApplicationLayoutContext;
import com.facilio.bmsconsole.util.ApplicationApi;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;

public class AddEnergyAppTabsAndLayout extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        ApplicationContext rmApp = ApplicationApi.getApplicationForLinkName(FacilioConstants.ApplicationLinkNames.ENERGY_APP);
        List<ApplicationLayoutContext> layouts = ApplicationApi.getLayoutsForAppId(rmApp.getId());
        if(CollectionUtils.isNotEmpty(layouts)){
            for(ApplicationLayoutContext layoutContext : layouts){
                if(layoutContext.getLayoutDeviceTypeEnum() == ApplicationLayoutContext.LayoutDeviceType.WEB){
                    ApplicationApi.addEnergyAppWebTabs(layoutContext);
                }
                if(layoutContext.getLayoutDeviceTypeEnum() == ApplicationLayoutContext.LayoutDeviceType.SETUP){
                    ApplicationApi.addEnergySetupLayoutWebGroups(layoutContext);
                }
            }
        }
        return false;
    }
}
