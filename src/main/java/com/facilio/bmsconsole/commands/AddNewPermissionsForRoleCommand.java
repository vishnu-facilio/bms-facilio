package com.facilio.bmsconsole.commands;

import com.facilio.accounts.bean.RoleBean;
import com.facilio.accounts.dto.NewPermission;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.WebTabBean;
import com.facilio.bmsconsole.context.Permission;
import com.facilio.bmsconsole.context.WebTabContext;
import com.facilio.bmsconsole.util.NewPermissionUtil;
import com.facilio.bmsconsoleV3.util.V3PermissionUtil;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FieldUtil;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class AddNewPermissionsForRoleCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		Long roleId = (Long) context.get(FacilioConstants.ContextNames.ROLE_ID);
		boolean checkBool = (boolean)context.getOrDefault(FacilioConstants.ContextNames.CHECK_BOOL,false);
		List<NewPermission> permissions = (List<NewPermission>) context.get(FacilioConstants.ContextNames.PERMISSIONS);
		List<Map<String,Object>> newPermissionWithBool = (List<Map<String, Object>>) context.get(FacilioConstants.ContextNames.BOOL_PERMISSIONS);

		if(checkBool && newPermissionWithBool != null){
			permissions = setPermission(newPermissionWithBool,roleId);
		}

		if(V3PermissionUtil.isFeatureEnabled()){
			permissions = new ArrayList<>();
			List<WebTabContext> webTabs = (List<WebTabContext>) context.get(FacilioConstants.ContextNames.WEB_TABS);
			if(CollectionUtils.isNotEmpty(webTabs)){
				for(WebTabContext webTab : webTabs) {
					NewPermission newPermission = V3PermissionUtil.getPermissionValueForTab(webTab);
					if(newPermission.getPermission() > 0 || newPermission.getPermission2() > 0) {
						permissions.add(newPermission);
					}
				}
			}
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

	private List<NewPermission> setPermission(List<Map<String,Object>> newPermission, Long roleId) throws Exception {

		List<WebTabContext> webTabs = FieldUtil.getAsBeanListFromMapList(newPermission,WebTabContext.class);
		List<NewPermission> webTabPermission = new ArrayList<>();

		for (WebTabContext tab : webTabs) {
			Long tabId = tab.getId();
			WebTabBean tabBean = (WebTabBean) BeanFactory.lookup("TabBean");
			WebTabContext webTab = tabBean.getWebTab(tabId);
			long permissionVal = 0;
			List<Permission> permissions = tab.getPermission();
			if (webTab != null ) {
				Map<String, Long> permissionVsValue = NewPermissionUtil.getPermissions(webTab.getType());
				Set<String> permissionValuekeys = permissionVsValue.keySet();
				for (Permission permission : permissions) {
					String actionName = permission.getActionName();
					if (StringUtils.isNotEmpty(actionName) && permissionValuekeys.contains(actionName)) {
						if(permission.isEnabled()){
							permissionVal += permissionVsValue.get(actionName);
						}
					}
				}
			}
			webTabPermission.add(new NewPermission(tabId,permissionVal));
		}
		return webTabPermission;
	}
}
