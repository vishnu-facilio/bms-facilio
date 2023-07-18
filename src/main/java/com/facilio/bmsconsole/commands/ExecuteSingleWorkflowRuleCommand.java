package com.facilio.bmsconsole.commands;


import com.facilio.bmsconsole.workflow.rule.WorkflowRuleContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import org.apache.commons.chain.Context;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import com.facilio.bmsconsole.util.WorkflowRuleAPI;


public class ExecuteSingleWorkflowRuleCommand extends FacilioCommand {

	private static final Logger LOGGER = LogManager.getLogger(ExecuteSingleWorkflowRuleCommand.class.getName());
	
	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		Boolean executeWorkFlow = (Boolean) context.get("executeWorkFlow");
		try {
			if(executeWorkFlow != null && !executeWorkFlow){
				return false;
			}

			WorkflowRuleAPI.executeSingleWorkflowRuleCommand(context);
		}catch (Exception e) {
			LOGGER.fatal("Error occurred during scheduled rule execution: "+ e);
		}
		return false;
	}
}
