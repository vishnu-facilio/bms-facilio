package com.facilio.bmsconsole.commands;

import com.facilio.accounts.dto.RoleApp;
import com.facilio.accounts.util.AccountConstants;
import com.facilio.command.FacilioCommand;
import com.facilio.db.builder.GenericDeleteRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import org.apache.commons.chain.Context;

import com.facilio.accounts.dto.Role;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.constants.FacilioConstants;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;

public class UpdateRoleCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		Role role = (Role) context.get(FacilioConstants.ContextNames.ROLE);
		List<RoleApp> rolesApps = (List<RoleApp>) context.get(FacilioConstants.ContextNames.ROLES_APPS);
		Boolean isWebTabPermission = (Boolean) context.get(FacilioConstants.ContextNames.IS_WEBTAB_PERMISSION);


		if (role != null) {
			
			AccountUtil.getRoleBean().updateRole(role.getRoleId(), role, isWebTabPermission);
			if(CollectionUtils.isNotEmpty(rolesApps)) {
				for(RoleApp roleApp : rolesApps) {
					roleApp.setRoleId(role.getRoleId());
				}
				deleteRolesAppMapping(role.getRoleId());
				AccountUtil.getRoleBean().addRolesAppsMapping(rolesApps);
			}
			else {
				throw new IllegalArgumentException("Role Object should have apps associated");
			}
			
			context.put(FacilioConstants.ContextNames.ROLE_ID, role.getRoleId());
		}
		else {
			throw new IllegalArgumentException("Role Object cannot be null");
		}
		return false;
	}

	private void deleteRolesAppMapping(long roleId) throws Exception {
		GenericDeleteRecordBuilder builder = new GenericDeleteRecordBuilder()
				.table(AccountConstants.getRolesAppsModule().getTableName())
				;
		builder.andCondition(CriteriaAPI.getCondition("ROLE_ID", "roleId", String.valueOf(roleId), NumberOperators.EQUALS));

		builder.delete();
	}


}
