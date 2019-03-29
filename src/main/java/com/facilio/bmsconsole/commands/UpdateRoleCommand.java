package com.facilio.bmsconsole.commands;

import com.facilio.accounts.dto.Role;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.constants.FacilioConstants;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

public class UpdateRoleCommand implements Command{

	@Override
	public boolean execute(Context context) throws Exception {
		Role role = (Role) context.get(FacilioConstants.ContextNames.ROLE);
		
		if (role != null) {
			
			AccountUtil.getRoleBean().updateRole(role.getRoleId(), role);
			
			context.put(FacilioConstants.ContextNames.ROLE_ID, role.getRoleId());
		}
		else {
			throw new IllegalArgumentException("Group Object cannot be null");
		}
		return false;
	}

}
