package com.facilio.bmsconsole.commands;

import java.sql.Connection;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.util.GroupAPI;
import com.facilio.constants.FacilioConstants;

public class DeleteGroupCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {

		Long groupId = (Long) context.get(FacilioConstants.ContextNames.GROUP_ID);
		
		if (groupId != null) {
			Connection conn = ((FacilioContext) context).getConnectionWithTransaction();
			
			GroupAPI.deleteGroup(groupId, conn);
		}
		else {
			throw new IllegalArgumentException("Group ID cannot be null");
		}
		return false;
	}

}
