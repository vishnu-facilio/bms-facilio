package com.facilio.bmsconsole.commands;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.constants.FacilioConstants;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

public class DeleteRoleCommand implements Command {
	
	@Override
	public boolean execute(Context context) throws Exception {

		Long roleId = (Long) context.get(FacilioConstants.ContextNames.ROLE_ID);
		
		if (roleId != null) {
		
			AccountUtil.getRoleBean().deleteRolePermission(roleId);
			AccountUtil.getRoleBean().deleteRole(roleId);
		}
		else {
			throw new IllegalArgumentException("ROLE ID cannot be null");
		}
		return false;
	}


}

