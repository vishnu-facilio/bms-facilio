package com.facilio.bmsconsole.commands;

import com.facilio.accounts.dto.User;
import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.context.ViewGroups;
import com.facilio.bmsconsole.util.ViewAPI;
import com.facilio.constants.FacilioConstants;

public class AddViewGroupCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		ViewGroups viewGroup = (ViewGroups) context.get(FacilioConstants.ContextNames.VIEW_GROUP);
		String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
		if (viewGroup != null) {
			viewGroup.setName(viewGroup.getDisplayName().toLowerCase().replaceAll("[^a-zA-Z0-9]+",""));
			User currentUser = AccountUtil.getCurrentUser();
			viewGroup.setSysCreatedTime(System.currentTimeMillis());
			viewGroup.setSysModifiedTime(viewGroup.getSysCreatedTime());
			viewGroup.setSysCreatedBy(currentUser.getId());
			viewGroup.setSysModifiedBy(currentUser.getId());
			
			long groupId = ViewAPI.addViewGroup(viewGroup, AccountUtil.getCurrentOrg().getOrgId(), moduleName);
			
			
			viewGroup.setId(groupId);
			context.put(FacilioConstants.ContextNames.VIEW_GROUP, viewGroup);

		}
		return false;
	}

}
