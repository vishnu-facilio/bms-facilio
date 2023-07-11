package com.facilio.remotemonitoring.commands;

import com.facilio.accounts.dto.Role;
import com.facilio.accounts.dto.User;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.context.ApplicationContext;
import com.facilio.bmsconsole.util.ApplicationApi;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.FieldUtil;
import org.apache.commons.chain.Context;

public class AddRMRolesAndUsersCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {

        ApplicationContext rmApp = ApplicationApi.getApplicationForLinkName(FacilioConstants.ApplicationLinkNames.REMOTE_MONITORING);
        Role rmAdminRole = new Role();
        rmAdminRole.setIsPrevileged(true);
        rmAdminRole.setName(FacilioConstants.DefaultRoleNames.REMOTE_MONITORING_ADMIN);
        AccountUtil.getRoleBean().createRole(AccountUtil.getCurrentOrg().getOrgId(),rmAdminRole);
        Role admin = AccountUtil.getRoleBean().getRole(AccountUtil.getCurrentOrg().getOrgId(),
                FacilioConstants.DefaultRoleNames.REMOTE_MONITORING_ADMIN);
        User user = AccountUtil.getCurrentUser();
        User clonedUser = FieldUtil.cloneBean(user,User.class);
        clonedUser.setRole(admin);
        clonedUser.setRoleId(admin.getRoleId());
        clonedUser.setApplicationId(rmApp.getId());
        AccountUtil.getUserBean().addToORGUsersApps(clonedUser, false);
        ApplicationApi.addAppRoleMapping(admin.getRoleId(), rmApp.getId());
        return false;
    }
}