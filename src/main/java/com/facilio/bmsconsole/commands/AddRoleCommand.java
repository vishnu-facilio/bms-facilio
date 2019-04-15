package com.facilio.bmsconsole.commands;

import com.facilio.accounts.dto.Role;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.constants.FacilioConstants;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

public class AddRoleCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
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
