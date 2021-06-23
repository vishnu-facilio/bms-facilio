package com.facilio.bmsconsole.commands;

import com.facilio.command.FacilioCommand;
import com.facilio.command.PostTransactionCommand;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.workflow.rule.WorkflowRuleContext.RuleType;

public class ExecuteWorkFlowsBusinessLogicInPostTransactionCommand extends FacilioCommand implements PostTransactionCommand {
	
	private Context context;
	
	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		this.context = context;
		return false;
	}

	@Override
	public boolean postExecute() throws Exception {
		// TODO Auto-generated method stub
		new ExecuteAllWorkflowsCommand(RuleType.MODULE_RULE).execute(context);
		return false;
	}

	

}
