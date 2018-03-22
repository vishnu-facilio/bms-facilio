package com.facilio.bmsconsole.commands;

import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.context.PMReminder;
import com.facilio.bmsconsole.context.PreventiveMaintenance;
import com.facilio.bmsconsole.criteria.CriteriaAPI;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.bmsconsole.modules.FieldFactory;
import com.facilio.bmsconsole.modules.FieldUtil;
import com.facilio.bmsconsole.modules.ModuleFactory;
import com.facilio.bmsconsole.view.ViewFactory;
import com.facilio.constants.FacilioConstants;
import com.facilio.sql.GenericSelectRecordBuilder;

public class GetPMAndPMReminderCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		long recordId = (long) context.get(FacilioConstants.ContextNames.ID);
		boolean onlyPost = (boolean) context.get(FacilioConstants.ContextNames.ONLY_POST_REMINDER_TYPE);
		List<FacilioField> fields = FieldFactory.getPMReminderFields();
		fields.addAll(FieldFactory.getPreventiveMaintenanceFields());
		FacilioModule module = ModuleFactory.getPMReminderModule();
		List<Map<String, Object>> reminderProps = null;
		if (onlyPost) {
			FacilioModule afterPMReminderWORelModule = ModuleFactory.getAfterPMRemindersWORelModule();
			fields.addAll(FieldFactory.getAfterPMReminderWORelFields());
			GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
								.select(fields)
								.table(module.getTableName())
								.innerJoin("Preventive_Maintenance")
								.on("PM_Reminders.PM_ID = Preventive_Maintenance.ID")
								.innerJoin("After_PM_Reminder_WO_Rel")
								.on("PM_Reminders.ID = After_PM_Reminder_WO_Rel.PM_REMINDER_ID")
								.andCondition(CriteriaAPI.getCurrentOrgIdCondition(module))
								.andCondition(CriteriaAPI.getIdCondition(recordId, afterPMReminderWORelModule));
			reminderProps = selectBuilder.get();
		}
		else {
			FacilioModule beforeReminderTriggerRelModule = ModuleFactory.getBeforePMRemindersTriggerRelModule();
			GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
															.select(fields)
															.table(module.getTableName())
															.innerJoin("Preventive_Maintenance")
															.on("PM_Reminders.PM_ID = Preventive_Maintenance.ID")
															.innerJoin("Before_PM_Reminder_Trigger_Rel")
															.on("PM_Reminders.ID = Before_PM_Reminder_Trigger_Rel.PM_REMINDER_ID")
															.andCondition(CriteriaAPI.getCurrentOrgIdCondition(module))
															.andCondition(CriteriaAPI.getIdCondition(recordId, beforeReminderTriggerRelModule))
															.andCondition(ViewFactory.getPreventiveStatusCondition(true));
			reminderProps = selectBuilder.get();
		}
		
		if(reminderProps != null && !reminderProps.isEmpty()) {
			Map<String, Object> prop = reminderProps.get(0);
			PMReminder reminder = FieldUtil.getAsBeanFromMap(prop, PMReminder.class);
			PreventiveMaintenance pm = FieldUtil.getAsBeanFromMap(prop, PreventiveMaintenance.class);
			pm.setId(reminder.getPmId());
			
			context.put(FacilioConstants.ContextNames.PM_REMINDER, reminder);
			context.put(FacilioConstants.ContextNames.PREVENTIVE_MAINTENANCE, pm);
			
			if(prop.get("woId") != null) {
				context.put(FacilioConstants.ContextNames.RECORD_ID, prop.get("woId"));
			}
		}
		
		return false;
	}

}
