package com.facilio.bmsconsole.commands;

import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.workflow.rule.AlarmRuleContext;
import com.facilio.bmsconsole.workflow.rule.EventType;
import com.facilio.bmsconsole.workflow.rule.ReadingRuleContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.tasker.FacilioTimer;
import com.facilio.time.DateTimeUtil;

public class AddJobEntryForScheduledReadingRuleCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		
		AlarmRuleContext alarmRule = (AlarmRuleContext) context.get(FacilioConstants.ContextNames.ALARM_RULE);
		
		ReadingRuleContext preRequsiteRule = alarmRule.getPreRequsite();
		
		if(preRequsiteRule.getEvent() != null && preRequsiteRule.getEvent().getActivityTypeEnum().equals(EventType.SCHEDULED_READING_RULE)) {

			FacilioTimer.scheduleCalendarJob(preRequsiteRule.getId(), FacilioConstants.Job.SCHEDULED_READING_RULE_JOB_NAME, DateTimeUtil.getCurrenTime(), preRequsiteRule.getSchedule(), FacilioConstants.Job.EXECUTER_NAME_FACILIO);
		}
		
		return false;
	}

}
