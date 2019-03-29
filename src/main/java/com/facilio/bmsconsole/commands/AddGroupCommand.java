package com.facilio.bmsconsole.commands;


import com.facilio.accounts.dto.Group;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.constants.FacilioConstants;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

public class AddGroupCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		
		Group group = (Group) context.get(FacilioConstants.ContextNames.GROUP);
		
		if (group != null) {
		//	Connection conn = ((FacilioContext) context).getConnectionWithTransaction();
			
			long groupId = AccountUtil.getGroupBean().createGroup(AccountUtil.getCurrentOrg().getOrgId(), group);
			group.setGroupId(groupId);
			
			context.put(FacilioConstants.ContextNames.GROUP_ID, groupId);
		}
		else {
			throw new IllegalArgumentException("Group Object cannot be null");
		}
		return false;
	}

}
