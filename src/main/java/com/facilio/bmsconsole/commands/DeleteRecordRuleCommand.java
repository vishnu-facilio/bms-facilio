package com.facilio.bmsconsole.commands;

import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.util.WorkflowRuleAPI;
import com.facilio.constants.FacilioConstants;

public class DeleteRecordRuleCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		Long recordId = (Long) context.get(FacilioConstants.ContextNames.RECORD_ID);
		
		if (recordId != null) {
			WorkflowRuleAPI.deleteWorkflowRule(recordId);
		}

		return false;
	}

}
