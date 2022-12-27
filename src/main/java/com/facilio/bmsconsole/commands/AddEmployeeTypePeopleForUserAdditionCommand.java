package com.facilio.bmsconsole.commands;

import com.facilio.bmsconsoleV3.signup.util.SignupUtil;
import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;

import com.facilio.accounts.dto.Role;
import com.facilio.accounts.dto.User;
import com.facilio.accounts.util.AccountConstants;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.util.PeopleAPI;
import com.facilio.constants.FacilioConstants;

public class AddEmployeeTypePeopleForUserAdditionCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		User user = (User) context.get(FacilioConstants.ContextNames.USER);
		if(user != null) {
			Role superAdminRole = AccountUtil.getRoleBean(user.getOrgId()).getRole(user.getOrgId(),AccountConstants.DefaultRole.SUPER_ADMIN, false);
			if(SignupUtil.maintenanceAppSignup()) {
				superAdminRole = AccountUtil.getRoleBean(user.getOrgId()).getRole(user.getOrgId(), FacilioConstants.DefaultRoleNames.MAINTENANCE_SUPER_ADMIN, false);
			}
			if(user.getRoleId() == superAdminRole.getRoleId()) {
				PeopleAPI.addPeopleForUser(user, true);
			}
			else {
				PeopleAPI.addPeopleForUser(user, false);
			}
		}
		return false;
	}

}
