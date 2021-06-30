package com.facilio.bmsconsole.commands;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.facilio.bmsconsole.util.ScheduledRuleJobsMetaUtil;
import com.facilio.bmsconsole.workflow.rule.WorkflowRuleContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.taskengine.job.JobContext;
import com.facilio.time.DateRange;
import com.facilio.time.DateTimeUtil;

public class DisableActiveScheduledRuleMetaJobsCommand extends FacilioCommand {
	
	private static final Logger LOGGER = LogManager.getLogger(DisableActiveScheduledRuleMetaJobsCommand.class.getName());
	
	@Override
	public boolean executeCommand(Context context) throws Exception {
		
		WorkflowRuleContext rule = (WorkflowRuleContext) context.get(FacilioConstants.ContextNames.WORKFLOW_RULE);
		DateRange dateRange =  getRange(rule, (JobContext) context.get(FacilioConstants.Job.JOB_CONTEXT));

		ScheduledRuleJobsMetaUtil.disableScheduledRuleJobsMetaFromRuleId(rule.getId(), dateRange);
		
		return false;
	}
	
	private DateRange getRange(WorkflowRuleContext rule, JobContext jc) {
		long startTime = -1, endTime = -1;
		if (rule.getTime() == null) { //DATE_TIME field
			startTime = jc.getExecutionTime() * 1000;
			endTime =  (jc.getNextExecutionTime() * 1000) - 1;
		}
		else { //DATE Field
			startTime = DateTimeUtil.getDayStartTime();
			endTime = DateTimeUtil.getDayStartTime(1) - 1;
		}
		
		switch (rule.getScheduleTypeEnum()) {
			case BEFORE:
				long interval = rule.getInterval() * 1000;
				return new DateRange(startTime + interval, endTime + interval);
			case ON:
				return new DateRange(startTime, endTime);
			case AFTER:
				interval = rule.getInterval() * 1000;
				return new DateRange(startTime - interval, endTime - interval);
		}
		return null;
	}

}
