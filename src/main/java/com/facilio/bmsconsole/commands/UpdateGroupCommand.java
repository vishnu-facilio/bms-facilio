package com.facilio.bmsconsole.commands;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.context.GroupContext;
import com.facilio.bmsconsole.util.GroupAPI;

public class UpdateGroupCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		
		GroupContext groupContext = (GroupContext) context;
		boolean status = GroupAPI.updateGroup(groupContext);
		
		return true;
	}

}
