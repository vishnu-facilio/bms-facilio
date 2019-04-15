package com.facilio.bmsconsole.commands;

import com.facilio.accounts.bean.RoleBean;
import com.facilio.accounts.dto.Permissions;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.constants.FacilioConstants;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import java.util.List;

public class AddPermissionsCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		
		Long roleId = (Long) context.get(FacilioConstants.ContextNames.ROLE_ID);
		List<Permissions> permissions = (List<Permissions>) context.get(FacilioConstants.ContextNames.PERMISSIONS);
		
		if (roleId != null && permissions != null) {
			
			Permissions permissionsObj = new Permissions();
//			List<Long> members = new ArrayList<>();
			for (Permissions perm : permissions) {
				permissionsObj.setRoleId(roleId);
				permissionsObj.setModuleName(perm.getModuleName());
				permissionsObj.setPermission(perm.getPermission());
				RoleBean roleBean = AccountUtil.getRoleBean();
				roleBean.addPermission(roleId, permissionsObj);
			}
		}
		else {
			throw new IllegalArgumentException("Permission Object cannot be null");
		}
		return false;
	}

}
