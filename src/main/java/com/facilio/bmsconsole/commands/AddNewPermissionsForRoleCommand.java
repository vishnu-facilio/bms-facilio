package com.facilio.bmsconsole.commands;

import java.util.List;

import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;

import com.facilio.accounts.bean.RoleBean;
import com.facilio.accounts.dto.NewPermission;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.constants.FacilioConstants;

public class AddNewPermissionsForRoleCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		Long roleId = (Long) context.get(FacilioConstants.ContextNames.ROLE_ID);
		List<NewPermission> permissions = (List<NewPermission>) context.get(FacilioConstants.ContextNames.PERMISSIONS);
		if (roleId > 0 && permissions != null && !permissions.isEmpty()) {
			for (NewPermission permission : permissions) {
				permission.setRoleId(roleId);
				RoleBean roleBean = AccountUtil.getRoleBean();
				roleBean.addNewPermission(roleId, permission);
			}
		} else {
			throw new IllegalArgumentException("Permission Object cannot be null");
		}
		return false;
	}

}
