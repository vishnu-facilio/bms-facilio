package com.facilio.bmsconsole.commands;

import com.facilio.accounts.dto.Group;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.constants.FacilioConstants;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

public class ChangeTeamStatusCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		Group group = (Group) context.get(FacilioConstants.ContextNames.GROUP);
		if (group != null) {
			AccountUtil.getGroupBean().changeGroupStatus(group.getGroupId(), group);
		}
		else {
			throw new IllegalArgumentException("User Object cannot be null");
		}
		return false;
	}
	
}