package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.accounts.dto.Permissions;
import com.facilio.accounts.dto.Role;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.accounts.util.PermissionInterface;
import com.facilio.accounts.util.PermissionUtil;
import com.facilio.accounts.util.RoleFactory;
import com.facilio.beans.ModuleBean;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;

public class AddDefaultRoleAndPermissionCommand extends FacilioCommand {


	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		
		Role mgrRole = getRole(RoleFactory.Role.MANAGER.getName());
		
		Map<String, List<PermissionInterface>> mgrPermissionMap = RoleFactory.Role.MANAGER.getPermissionMap();
		
		addRole(mgrRole, mgrPermissionMap, context);
		
		Role techRole = getRole(RoleFactory.Role.TECHNICIAN.getName());
		
		Map<String, List<PermissionInterface>> techPermissionMap = RoleFactory.Role.TECHNICIAN.getPermissionMap();
		
		addRole(techRole, techPermissionMap, context);
		
		return false;
	}
	
	private void addRole(Role role,Map<String, List<PermissionInterface>> permissionMap,Context context) throws Exception {
		
		List<Permissions> permissions = new ArrayList<>();
		for(String moduleName : permissionMap.keySet()) {
			
			List<PermissionInterface> availablePermissions = permissionMap.get(moduleName);
			if(PermissionUtil.isSpecialModule(moduleName)) {
				Permissions permission = getPermissionContext(availablePermissions,moduleName,-1,role);
				permissions.add(permission);
			}
			else {
				ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
				FacilioModule module = modBean.getModule(moduleName);
				Permissions permission = getPermissionContext(availablePermissions,null,module.getModuleId(),role);
				permissions.add(permission);
			}
		}
		
		context.put(FacilioConstants.ContextNames.ROLE, role);
		context.put(FacilioConstants.ContextNames.PERMISSIONS, permissions);
		
		Command addRole = FacilioChainFactory.getAddRoleCommand();
		addRole.execute(context);
	}
	
	private Role getRole(String roleName) {
		
		Role role = new Role();
		role.setCreatedTime(System.currentTimeMillis());
		role.setName(roleName);
		role.setDescription(roleName);
		role.setOrgId(AccountUtil.getCurrentOrg().getId());
		
		return role;
	}
	
	private Permissions getPermissionContext(List<PermissionInterface> availablePermissions, String moduleName,long moduleId,Role role) {
		
		long permissionValue = 0;
		for(PermissionInterface availablePermission : availablePermissions) {
			permissionValue += availablePermission.getPermission();
		}
		Permissions permission = new Permissions();
		permission.setModuleId(moduleId);
		permission.setModuleName(moduleName);
		permission.setPermission(permissionValue);
		permission.setRoleId(role.getId());
		return permission;
	}

}
