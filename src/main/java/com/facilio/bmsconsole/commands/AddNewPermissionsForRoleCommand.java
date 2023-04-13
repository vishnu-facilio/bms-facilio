package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.List;

import com.facilio.accounts.dto.NewPermission;
import com.facilio.bmsconsole.context.WebTabContext;
import com.facilio.bmsconsoleV3.util.V3PermissionUtil;
import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;

import com.facilio.accounts.bean.RoleBean;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.constants.FacilioConstants;
import org.apache.commons.collections4.CollectionUtils;

public class AddNewPermissionsForRoleCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		Long roleId = (Long) context.get(FacilioConstants.ContextNames.ROLE_ID);
		List<NewPermission> permissions = (List<NewPermission>) context.get(FacilioConstants.ContextNames.PERMISSIONS);
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

}
