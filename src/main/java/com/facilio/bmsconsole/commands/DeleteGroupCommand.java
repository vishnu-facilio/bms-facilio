package com.facilio.bmsconsole.commands;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.constants.FacilioConstants;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

public class DeleteGroupCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {

		Long groupId = (Long) context.get(FacilioConstants.ContextNames.GROUP_ID);
		
		if (groupId != null) {
			
			AccountUtil.getGroupBean().deleteGroup(groupId);
		}
		else {
			throw new IllegalArgumentException("Group ID cannot be null");
		}
		return false;
	}

}
