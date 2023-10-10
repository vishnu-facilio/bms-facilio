package com.facilio.bmsconsoleV3.signup.enrgyApp;

import com.facilio.accounts.dto.Role;
import com.facilio.accounts.dto.User;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.context.ApplicationContext;
import com.facilio.bmsconsole.util.ApplicationApi;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.FieldUtil;
import org.apache.commons.chain.Context;

public class AddEnergyAppDefaultUserAndRoles extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        ApplicationContext energyApp = ApplicationApi.getApplicationForLinkName(FacilioConstants.ApplicationLinkNames.ENERGY_APP);

        Role energySuperAdmin = new Role();
        energySuperAdmin.setIsPrevileged(true);
        energySuperAdmin.setName(FacilioConstants.DefaultRoleNames.ENERGY_SUPER_ADMIN);
        AccountUtil.getRoleBean().createRole(AccountUtil.getCurrentOrg().getOrgId(),energySuperAdmin);

        Role energySuperAdminRole = AccountUtil.getRoleBean().getRole(AccountUtil.getCurrentOrg().getOrgId(),
                FacilioConstants.DefaultRoleNames.ENERGY_SUPER_ADMIN);
        User user = AccountUtil.getCurrentUser();
        User clonedUser = FieldUtil.cloneBean(user,User.class);
        clonedUser.setApplicationId(energyApp.getId());
        clonedUser.setRole(energySuperAdminRole);
        clonedUser.setRoleId(energySuperAdminRole.getRoleId());
        clonedUser.setApplicationId(energyApp.getId());
        AccountUtil.getUserBean().addToORGUsersApps(clonedUser, false);

        Role energyManager = new Role();
        energyManager.setIsPrevileged(true);
        energyManager.setName(FacilioConstants.DefaultRoleNames.ENERGY_MANAGER);
        AccountUtil.getRoleBean().createRole(AccountUtil.getCurrentOrg().getOrgId(),energyManager);

        energyManager = AccountUtil.getRoleBean().getRole(AccountUtil.getCurrentOrg().getOrgId(),
                FacilioConstants.DefaultRoleNames.ENERGY_MANAGER);

        ApplicationApi.addAppRoleMapping(energySuperAdminRole.getRoleId(), energyApp.getId());
        ApplicationApi.addAppRoleMapping(energyManager.getRoleId(), energyApp.getId());
        return false;
    }
}
