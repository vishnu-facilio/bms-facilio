package com.facilio.bmsconsole.commands;

import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.util.WorkflowRuleAPI;
import com.facilio.bmsconsole.workflow.rule.WorkflowRuleContext;
import com.facilio.constants.FacilioConstants;

public class GetRecordRuleCommand extends FacilioCommand {

		@Override
		public boolean executeCommand(Context context) throws Exception {
			Long recordRuleId = (Long) context.get(FacilioConstants.ContextNames.RECORD_ID);
			if (recordRuleId != null && recordRuleId > 0) {
				WorkflowRuleContext ruleContext = WorkflowRuleAPI.getWorkflowRule(recordRuleId);
				if (ruleContext == null) {
					throw new IllegalArgumentException("Invalid id");
				}
				context.put(FacilioConstants.ContextNames.RECORD, ruleContext);
			}
			else {
				throw new IllegalArgumentException("Invalid id");
			}
			return false;
		}
	
}
