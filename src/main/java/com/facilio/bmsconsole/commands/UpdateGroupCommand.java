package com.facilio.bmsconsole.commands;

import java.sql.Connection;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.context.GroupContext;
import com.facilio.bmsconsole.util.GroupAPI;
import com.facilio.constants.FacilioConstants;

public class UpdateGroupCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {

		GroupContext group = (GroupContext) context.get(FacilioConstants.ContextNames.GROUP);
		
		if (group != null) {
			Connection conn = ((FacilioContext) context).getConnectionWithTransaction();
			
			GroupAPI.updateGroup(group, conn);
			
			context.put(FacilioConstants.ContextNames.GROUP_ID, group.getGroupId());
		}
		else {
			throw new IllegalArgumentException("Group Object cannot be null");
		}
		return false;
	}

}
