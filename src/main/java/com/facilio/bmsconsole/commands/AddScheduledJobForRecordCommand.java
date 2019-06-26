package com.facilio.bmsconsole.commands;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.util.SingleRecordRuleAPI;
import com.facilio.bmsconsole.workflow.rule.EventType;
import com.facilio.bmsconsole.workflow.rule.RecordSpecificRuleContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.tasker.FacilioTimer;
import com.facilio.tasker.ScheduleInfo;

public class AddScheduledJobForRecordCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		RecordSpecificRuleContext recordRule = (RecordSpecificRuleContext) context.get(FacilioConstants.ContextNames.RECORD);
		if (recordRule != null) {
			if (recordRule.getEvent() != null) {
				if (EventType.SCHEDULED_RECORD_RULE.isPresent(recordRule.getEvent().getActivityType())) {
					FacilioTimer.deleteJob(recordRule.getId(), FacilioConstants.Job.RECORD_SPECIFIC_RULE_JOB_NAME);
					//addJob(recordRule);
					SingleRecordRuleAPI.addJob(recordRule);
				}
			}
		}
		return false;
	}
	
	private void addJob(RecordSpecificRuleContext rule) throws Exception {
		ScheduleInfo info = rule.getSchedule();
		//if(config.getScheduleMode() == Mode.PERIODIC.getValue()) {
			FacilioTimer.scheduleCalendarJob(rule.getId(), FacilioConstants.Job.RECORD_SPECIFIC_RULE_JOB_NAME, System.currentTimeMillis(), info, FacilioConstants.Job.EXECUTER_NAME_FACILIO);
//		}
//		else {
//			long nextExecutionTime = info.nextExecutionTime(getStartTimeInSecond(System.currentTimeMillis()));
//			FacilioTimer.scheduleOneTimeJob(config.getId(), FacilioConstants.Job.NOTIFICATION_TRIGGER_JOB_NAME, nextExecutionTime, FacilioConstants.Job.EXECUTER_NAME_FACILIO);
//		}
	}

}
