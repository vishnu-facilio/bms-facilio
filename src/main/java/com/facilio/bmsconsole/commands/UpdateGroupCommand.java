package com.facilio.bmsconsole.commands;

import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;

import com.facilio.accounts.dto.Group;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.constants.FacilioConstants;

public class UpdateGroupCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {

		Group group = (Group) context.get(FacilioConstants.ContextNames.GROUP);
		
		if (group != null) {
			
			AccountUtil.getGroupBean().updateGroup(group.getId(), group);
			
			context.put(FacilioConstants.ContextNames.GROUP_ID, group.getId());
		}
		else {
			throw new IllegalArgumentException("Group Object cannot be null");
		}
		return false;
	}
}