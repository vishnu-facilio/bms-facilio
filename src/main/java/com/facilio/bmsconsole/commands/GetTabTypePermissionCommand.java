package com.facilio.bmsconsole.commands;

import java.util.List;

import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.context.Permission;
import com.facilio.bmsconsole.util.NewPermissionUtil;
import com.facilio.constants.FacilioConstants;

public class GetTabTypePermissionCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		int tabType = (int) context.get(FacilioConstants.ContextNames.WEB_TAB_TYPE);
		if (tabType > 0) {
			String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
			List<Permission> permissions = NewPermissionUtil.getPermissions(tabType, moduleName);
			context.put(FacilioConstants.ContextNames.PERMISSIONS, permissions);
		}
		return false;
	}

}
