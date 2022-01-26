package com.facilio.bmsconsole.commands;

import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.constants.FacilioConstants;

import java.util.Collections;

public class DeleteRoleCommand extends FacilioCommand {
	
	@Override
	public boolean executeCommand(Context context) throws Exception {

		Long roleId = (Long) context.get(FacilioConstants.ContextNames.ROLE_ID);
		
		if (roleId != null) {
			Long usersForRole = AccountUtil.getUserBean().getOrgUsersCountForRole(Collections.singleton(roleId));
			if(usersForRole == null || usersForRole <= 0) {
				AccountUtil.getRoleBean().deleteRolePermission(roleId);
				AccountUtil.getRoleBean().deleteRole(roleId);
			}
			else {
				throw new IllegalArgumentException("Cannot delete role(s) as users are associated");
			}
		}
		else {
			throw new IllegalArgumentException("ROLE ID cannot be null");
		}
		return false;
	}


}

