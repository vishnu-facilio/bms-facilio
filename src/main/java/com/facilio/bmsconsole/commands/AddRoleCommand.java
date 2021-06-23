package com.facilio.bmsconsole.commands;

import com.facilio.accounts.dto.RoleApp;
import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;

import com.facilio.accounts.dto.Role;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.constants.FacilioConstants;
import org.apache.commons.collections4.CollectionUtils;

import java.util.*;

public class AddRoleCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		Role role = (Role) context.get(FacilioConstants.ContextNames.ROLE);
		List<RoleApp> rolesApps = (List<RoleApp>) context.get(FacilioConstants.ContextNames.ROLES_APPS);

		if (role != null) {
			
			long roleId = AccountUtil.getRoleBean().createRole(AccountUtil.getCurrentOrg().getOrgId(), role);
			role.setRoleId(roleId);

			if(CollectionUtils.isEmpty(rolesApps)) {
				throw new IllegalArgumentException("Role Object should have apps associated");
			}
			for(RoleApp roleApp : rolesApps) {
				roleApp.setRoleId(roleId);
			}
			AccountUtil.getRoleBean().addRolesAppsMapping(rolesApps);


			context.put(FacilioConstants.ContextNames.ROLE_ID, roleId);
		}
		else {
			throw new IllegalArgumentException("Role Object cannot be null");
		}
		return false;
	}


}
