package com.facilio.bmsconsole.commands;

import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;

public class WorkflowRuleCommand extends FacilioCommand {
	
	private String module;
	private String action;
	
	public WorkflowRuleCommand(String module, String action) {
		this.module = module;
		this.action = action;
	}
	
	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		
		context.get("ticketId");
		
		
		
		return false;
	}
}
