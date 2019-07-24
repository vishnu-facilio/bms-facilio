package com.facilio.bmsconsole.commands;

import org.apache.commons.chain.Context;

import com.facilio.accounts.dto.Role;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.constants.FacilioConstants;

public class AddRoleCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		Role role = (Role) context.get(FacilioConstants.ContextNames.ROLE);
		
		if (role != null) {	
			
			long roleId = AccountUtil.getRoleBean().createRole(AccountUtil.getCurrentOrg().getOrgId(), role);
			role.setRoleId(roleId);
			
			context.put(FacilioConstants.ContextNames.ROLE_ID, roleId);
		}
		else {
			throw new IllegalArgumentException("Role Object cannot be null");
		}
		return false;
	}

}
