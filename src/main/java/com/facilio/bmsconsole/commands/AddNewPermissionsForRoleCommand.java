package com.facilio.bmsconsole.commands;

import com.facilio.accounts.bean.RoleBean;
import com.facilio.accounts.dto.NewPermission;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.WebTabBean;
import com.facilio.bmsconsole.context.Permission;
import com.facilio.bmsconsole.context.PermissionGroup;
import com.facilio.bmsconsole.context.WebTabContext;
import com.facilio.bmsconsole.util.NewPermissionUtil;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

public class AddNewPermissionsForRoleCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		Long roleId = (Long) context.get(FacilioConstants.ContextNames.ROLE_ID);
		boolean checkBool = (boolean)context.getOrDefault(FacilioConstants.ContextNames.CHECK_BOOL,false);
		List<NewPermission> permissions = (List<NewPermission>) context.get(FacilioConstants.ContextNames.PERMISSIONS);
		List<WebTabContext> newPermissionWithBool = (List<WebTabContext>) context.get(FacilioConstants.ContextNames.BOOL_PERMISSIONS);

		if(checkBool && newPermissionWithBool != null){
			permissions = setPermission(newPermissionWithBool,roleId);
		}

		if (roleId > 0 && permissions != null && !permissions.isEmpty()) {
			for (NewPermission permission : permissions) {
				permission.setRoleId(roleId);
				RoleBean roleBean = AccountUtil.getRoleBean();
				roleBean.addNewPermission(roleId, permission);
			}
		} else {
//			throw new IllegalArgumentException("Permission Object cannot be null");
		}
		return false;
	}

	private List<NewPermission> setPermission(List<WebTabContext> newPermission, Long roleId) throws Exception {

		List<NewPermission> webTabPermission = new ArrayList<>();

		for (WebTabContext tab : newPermission) {
			Long tabId = tab.getId();
			WebTabBean tabBean = (WebTabBean) BeanFactory.lookup("TabBean");
			WebTabContext webTab = tabBean.getWebTab(tabId);
			long permissionVal = 0;

			if (webTab != null ) {
				List<Permission> permissionVsValues = NewPermissionUtil.getPermissions(webTab);
				List<Permission> permissions = tab.getPermission();

				if(CollectionUtils.isNotEmpty(permissionVsValues) && CollectionUtils.isNotEmpty(permissions)) {
					for (Permission permission : permissions) {
						for (Permission permissionVsValue : permissionVsValues) {
							if (permissionVsValue instanceof PermissionGroup && permission.isEnabled()) {
								for (Permission permissionGroupVsValue : ((PermissionGroup) permissionVsValue).getPermissions()) {
									String actionName = permissionGroupVsValue.getActionName();
									if (permission.getActionName().equals(actionName)) {
										permissionVal += permissionGroupVsValue.getValue();
										break;
									}
								}
							} else {
								String actionName = permissionVsValue.getActionName();
								if (permission.getActionName().equals(actionName) && permission.isEnabled()) {
									permissionVal += permissionVsValue.getValue();
									break;
								}
							}
						}
					}
				}
			}
			webTabPermission.add(new NewPermission(tabId,permissionVal));
		}
		return webTabPermission;
	}
}
