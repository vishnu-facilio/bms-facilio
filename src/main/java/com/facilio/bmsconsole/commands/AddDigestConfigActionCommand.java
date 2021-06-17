package com.facilio.bmsconsole.commands;

import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.context.DigestConfigContext;
import com.facilio.bmsconsole.context.ScheduledActionContext;
import com.facilio.bmsconsole.workflow.rule.ActionContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.modules.DigestTemplateFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import com.facilio.tasker.FacilioTimer;
import com.facilio.taskengine.ScheduleInfo;

public class AddDigestConfigActionCommand extends FacilioCommand{

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
      	ScheduledActionContext scheduledAction = new ScheduledActionContext();
		List<ActionContext> actions = (List<ActionContext>) context.get(FacilioConstants.ContextNames.ACTIONS_LIST);
		DigestConfigContext config = (DigestConfigContext)context.get(FacilioConstants.ContextNames.CONFIG);
		if(CollectionUtils.isNotEmpty(actions)) {
			ScheduleInfo scheduleInfo = (ScheduleInfo)context.get(FacilioConstants.ContextNames.SCHEDULE_INFO);
			if(scheduleInfo == null) {
				throw new IllegalArgumentException("Schedule Info has to associated for a digest configuration");
			}
			scheduledAction.setActionId(actions.get(0).getId());
			scheduledAction.setOrgId(AccountUtil.getCurrentOrg().getId());
			scheduledAction.setFrequency(scheduleInfo.getFrequency());
			scheduledAction.setTime(scheduleInfo.getTimes().get(0));
			scheduledAction.setName(DigestTemplateFactory.getDigestTemplate(config.getDefaultTemplateId()).getTemplateName());
			
			FacilioModule module = ModuleFactory.getScheduledActionModule();
			
			Map<String, Object> prop = FieldUtil.getAsProperties(scheduledAction);
			GenericInsertRecordBuilder insertBuilder = new GenericInsertRecordBuilder()
					.table(module.getTableName())
					.fields(FieldFactory.getScheduledActionFields())
					.addRecord(prop);;
			insertBuilder.save();
			
			scheduledAction.setId((long) prop.get("id"));
			
			FacilioTimer.scheduleCalendarJob(scheduledAction.getId(), FacilioConstants.Job.DIGEST_JOB_NAME, System.currentTimeMillis(), scheduleInfo, "facilio");
			context.put(FacilioConstants.ContextNames.SCHEDULED_ACTION, scheduledAction);
		}
		return false;
	}

}
