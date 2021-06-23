package com.facilio.bmsconsole.commands;

import java.util.Collections;
import java.util.Map;

import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.context.ScheduledActionContext;
import com.facilio.bmsconsole.util.ActionAPI;
import com.facilio.bmsconsole.util.ScheduledActionAPI;
import com.facilio.bmsconsole.workflow.rule.ActionContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.constants.FacilioConstants.Job;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import com.facilio.tasker.FacilioTimer;

public class EditScheduledActionCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		
		ScheduledActionContext scheduledAction = (ScheduledActionContext) context.get(FacilioConstants.ContextNames.SCHEDULE_INFO);
		ScheduledActionContext oldScheduledAction = ScheduledActionAPI.getScheduledAction(scheduledAction.getId());
		long oldActionId = oldScheduledAction.getActionId();
		
		ActionContext action = scheduledAction.getAction();
		if (action != null) {
			ActionAPI.addActions(Collections.singletonList(action), null);
			scheduledAction.setActionId(action.getId());
		}
		
		if (scheduledAction.getScheduleInfo() != null) {
			if (scheduledAction.getScheduleInfo().getFrequencyType() != -1) {
				scheduledAction.setFrequency(scheduledAction.getScheduleInfo().getFrequencyType());
			}
			if (scheduledAction.getScheduleInfo().getTimeObjects() != null) {
				scheduledAction.setTime(scheduledAction.getScheduleInfo().getTimeObjects().get(0));
			}
		}
		
		FacilioModule module = ModuleFactory.getScheduledActionModule();
		
		Map<String, Object> prop = FieldUtil.getAsProperties(scheduledAction);
		GenericUpdateRecordBuilder builder = new GenericUpdateRecordBuilder()
				.table(module.getTableName())
				.fields(FieldFactory.getScheduledActionFields())
				.andCondition(CriteriaAPI.getIdCondition(scheduledAction.getId(), module))
				;
		builder.update(prop);
		
		if (scheduledAction.getScheduleInfo() != null) {
			FacilioTimer.deleteJob(scheduledAction.getId(), Job.DIGEST_JOB_NAME);
			FacilioTimer.scheduleCalendarJob(scheduledAction.getId(), Job.DIGEST_JOB_NAME, System.currentTimeMillis(), scheduledAction.getScheduleInfo(), "facilio");
		}
		if (action != null) {
			ActionAPI.deleteActions(Collections.singletonList(oldActionId));
		}
		
		return false;
	}

}
