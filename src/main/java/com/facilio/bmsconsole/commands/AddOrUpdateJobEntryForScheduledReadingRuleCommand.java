package com.facilio.bmsconsole.commands;

import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.workflow.rule.AlarmRuleContext;
import com.facilio.bmsconsole.workflow.rule.EventType;
import com.facilio.bmsconsole.workflow.rule.WorkflowRuleContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.tasker.FacilioTimer;
import com.facilio.taskengine.job.JobContext;
import com.facilio.time.DateTimeUtil;

public class AddOrUpdateJobEntryForScheduledReadingRuleCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		
		AlarmRuleContext alarmRule = (AlarmRuleContext) context.get(FacilioConstants.ContextNames.ALARM_RULE);
		
		WorkflowRuleContext workflowRuleContext = null;
		if(alarmRule != null) {
			workflowRuleContext = alarmRule.getPreRequsite();;
		}
		else {
			workflowRuleContext = (WorkflowRuleContext) context.get(FacilioConstants.ContextNames.WORKFLOW_RULE);
		}
		
		if(workflowRuleContext.getActivityTypeEnum() != null && workflowRuleContext.getActivityTypeEnum().equals(EventType.SCHEDULED_READING_RULE)) {
			
			JobContext job = FacilioTimer.getJob(workflowRuleContext.getId(), FacilioConstants.Job.SCHEDULED_READING_RULE_JOB_NAME);
			
			if(job != null) {
				FacilioTimer.deleteJob(workflowRuleContext.getId(), FacilioConstants.Job.SCHEDULED_READING_RULE_JOB_NAME);
			}

			FacilioTimer.scheduleCalendarJob(workflowRuleContext.getId(), FacilioConstants.Job.SCHEDULED_READING_RULE_JOB_NAME, DateTimeUtil.getCurrenTime(), workflowRuleContext.getSchedule(), FacilioConstants.Job.EXECUTER_NAME_PRIORTIY);
		}
		
		return false;
	}

}
