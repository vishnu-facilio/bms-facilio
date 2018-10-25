package com.facilio.bmsconsole.commands;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

public class WorkflowRuleCommand implements Command {
	
	private String module;
	private String action;
	
	public WorkflowRuleCommand(String module, String action) {
		this.module = module;
		this.action = action;
	}
	
	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		
		context.get("ticketId");
		
		
		
		return false;
	}
}
