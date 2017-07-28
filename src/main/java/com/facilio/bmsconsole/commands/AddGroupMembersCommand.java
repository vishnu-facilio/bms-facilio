package com.facilio.bmsconsole.commands;

import java.sql.Connection;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.util.GroupAPI;
import com.facilio.constants.FacilioConstants;

public class AddGroupMembersCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		
		Long groupId = (Long) context.get(FacilioConstants.ContextNames.GROUP_ID);
		Long[] memberIds = (Long[]) context.get(FacilioConstants.ContextNames.GROUP_MEMBER_IDS);
		
		if (groupId != null && memberIds != null) {
			Connection conn = ((FacilioContext) context).getConnectionWithTransaction();
			
			GroupAPI.updateGroupMembers(groupId, memberIds, 1, conn);
		}
		else {
			throw new IllegalArgumentException("Group Object cannot be null");
		}
		return false;
	}

}
