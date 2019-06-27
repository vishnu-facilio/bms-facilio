package com.facilio.bmsconsole.commands;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.util.SingleRecordRuleAPI;
import com.facilio.bmsconsole.workflow.rule.EventType;
import com.facilio.bmsconsole.workflow.rule.RecordSpecificRuleContext;
import com.facilio.bmsconsole.workflow.rule.WorkflowRuleContext.RuleType;
import com.facilio.constants.FacilioConstants;
import com.facilio.tasker.FacilioTimer;

public class AddScheduledJobForRecordCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		RecordSpecificRuleContext recordRule = (RecordSpecificRuleContext) context.get(FacilioConstants.ContextNames.RECORD);
		if (recordRule != null) {
			if (recordRule.getEvent() != null) {
				if (EventType.SCHEDULED.isPresent(recordRule.getEvent().getActivityType()) && recordRule.getRuleType() == RuleType.RECORD_SPECIFIC_RULE.getIntVal()) {
					
					FacilioTimer.deleteJob(recordRule.getId(), FacilioConstants.Job.RECORD_SPECIFIC_RULE_JOB_NAME);
					SingleRecordRuleAPI.addJob(recordRule);
				}
			}
		}
		return false;
	}
	
}
