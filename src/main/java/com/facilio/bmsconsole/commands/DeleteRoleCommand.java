package com.facilio.bmsconsole.commands;

import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.constants.FacilioConstants;

public class DeleteRoleCommand extends FacilioCommand {
	
	@Override
	public boolean executeCommand(Context context) throws Exception {

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

