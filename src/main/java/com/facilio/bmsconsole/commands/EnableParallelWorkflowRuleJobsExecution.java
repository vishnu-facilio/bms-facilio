package com.facilio.bmsconsole.commands;

import org.apache.commons.chain.Context;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.facilio.constants.FacilioConstants;

public class EnableParallelWorkflowRuleJobsExecution extends FacilioCommand {

	private static final Logger LOGGER = LogManager.getLogger(EnableParallelWorkflowRuleJobsExecution.class.getName());
	@Override
	public boolean executeCommand(Context context) throws Exception {
		
		context.put(FacilioConstants.ContextNames.IS_PARALLEL_RULE_EXECUTION, Boolean.TRUE);
		return false;
	}

}
