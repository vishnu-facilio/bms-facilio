package com.facilio.bmsconsole.commands;

import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;

import com.facilio.accounts.dto.Group;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.constants.FacilioConstants;

public class ChangeTeamStatusCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		Group group = (Group) context.get(FacilioConstants.ContextNames.GROUP);
		if (group != null) {
			AccountUtil.getGroupBean().changeGroupStatus(group.getId(), group);
		}
		else {
			throw new IllegalArgumentException("User Object cannot be null");
		}
		return false;
	}
	
}