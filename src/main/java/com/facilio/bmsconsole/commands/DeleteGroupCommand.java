package com.facilio.bmsconsole.commands;

import com.facilio.chain.FacilioContext;
import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.constants.FacilioConstants;

public class DeleteGroupCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {

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
