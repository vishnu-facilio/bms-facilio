package com.facilio.bmsconsoleV3.signup;

import com.facilio.accounts.dto.Role;
import com.facilio.accounts.dto.User;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.context.ApplicationContext;
import com.facilio.bmsconsole.context.ApplicationLayoutContext;
import com.facilio.bmsconsole.util.ApplicationApi;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.FieldUtil;
import com.facilio.util.FacilioUtil;
import org.apache.commons.chain.Context;

public class AddEnergyApp extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        ApplicationContext energyApp = ApplicationApi.getApplicationForLinkName(FacilioConstants.ApplicationLinkNames.ENERGY_APP);
        ApplicationLayoutContext energyLayout = new ApplicationLayoutContext(energyApp.getId(), ApplicationLayoutContext.AppLayoutType.DUAL,
                ApplicationLayoutContext.LayoutDeviceType.WEB, FacilioConstants.ApplicationLinkNames.ENERGY_APP);
        ApplicationApi.addApplicationLayout(energyLayout);
        ApplicationApi.addEnergyAppWebTabs(energyLayout);
        ApplicationLayoutContext energySetupLayout = new ApplicationLayoutContext(energyApp.getId(), ApplicationLayoutContext.AppLayoutType.SINGLE, ApplicationLayoutContext.LayoutDeviceType.SETUP, FacilioConstants.ApplicationLinkNames.ENERGY_APP);
        ApplicationApi.addApplicationLayout(energySetupLayout);
        ApplicationApi.addEnergySetupLayoutWebGroups(energySetupLayout);
        Role superAdmin = AccountUtil.getRoleBean().getRole(AccountUtil.getCurrentOrg().getOrgId(),
                FacilioConstants.DefaultRoleNames.SUPER_ADMIN);
        User user = AccountUtil.getCurrentUser();
        User clonedUser = FieldUtil.cloneBean(user,User.class);
        clonedUser.setApplicationId(energyApp.getId());
        clonedUser.setRole(superAdmin);
        clonedUser.setRoleId(superAdmin.getRoleId());
        clonedUser.setApplicationId(energyApp.getId());
        AccountUtil.getUserBean().addToORGUsersApps(clonedUser, false);
        ApplicationApi.addAppRoleMapping(superAdmin.getRoleId(), energyApp.getId());
        return false;
    }
}
