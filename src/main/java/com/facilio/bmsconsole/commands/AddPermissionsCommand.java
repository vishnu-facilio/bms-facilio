package com.facilio.bmsconsole.commands;

import java.util.List;

import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;

import com.facilio.accounts.bean.RoleBean;
import com.facilio.accounts.dto.Permissions;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.accounts.util.PermissionUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;

public class AddPermissionsCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		
		boolean isNewHandling = false;
		
		Long roleId = (Long) context.get(FacilioConstants.ContextNames.ROLE_ID);
		List<Permissions> permissions = (List<Permissions>) context.get(FacilioConstants.ContextNames.PERMISSIONS);
		
		if (roleId != null && permissions != null) {
			
			for (Permissions perm : permissions) {
				perm.setRoleId(roleId);
				
				if(isNewHandling) {
					if(perm.getModuleId() <=0 &&  perm.getModuleName() != null && !PermissionUtil.isSpecialModule(perm.getModuleName())) {
						
						ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
						FacilioModule module = modBean.getModule(perm.getModuleName());
						perm.setModuleId(module.getModuleId());
					}
				}
				
				RoleBean roleBean = AccountUtil.getRoleBean();
				roleBean.addPermission(roleId, perm);
			}
		}
		else {
//			throw new IllegalArgumentException("Permission Object cannot be null");
		}
		return false;
	}

}
