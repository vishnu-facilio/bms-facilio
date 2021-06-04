package com.facilio.bmsconsole.commands;

import com.facilio.accounts.dto.RoleApp;
import com.facilio.accounts.util.AccountConstants;
import com.facilio.bmsconsole.context.BaseSpaceContext;
import com.facilio.bmsconsole.util.SpaceAPI;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.modules.ModuleFactory;
import org.apache.commons.chain.Context;

import com.facilio.accounts.dto.Role;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.constants.FacilioConstants;
import org.apache.commons.collections4.CollectionUtils;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AddRoleCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		Role role = (Role) context.get(FacilioConstants.ContextNames.ROLE);
		List<RoleApp> rolesApps = (List<RoleApp>) context.get(FacilioConstants.ContextNames.ROLES_APPS);

		if (role != null) {
			
			long roleId = AccountUtil.getRoleBean().createRole(AccountUtil.getCurrentOrg().getOrgId(), role);
			role.setRoleId(roleId);

			if(CollectionUtils.isNotEmpty(rolesApps)) {
				for(RoleApp roleApp : rolesApps) {
					roleApp.setRoleId(roleId);
				}
				AccountUtil.getRoleBean().addRolesAppsMapping(rolesApps);
			}
			else {
				throw new IllegalArgumentException("Role Object should have apps associated");
			}
			context.put(FacilioConstants.ContextNames.ROLE_ID, roleId);
		}
		else {
			throw new IllegalArgumentException("Role Object cannot be null");
		}
		return false;
	}


}
