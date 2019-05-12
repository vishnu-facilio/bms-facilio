package com.facilio.bmsconsole.commands;

import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.context.*;
import com.facilio.bmsconsole.context.PMReminder.ReminderType;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import com.facilio.bmsconsole.util.PreventiveMaintenanceAPI;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SchedulePostPMRemindersCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		List<PreventiveMaintenance> pms = CommonCommandUtil.getList((FacilioContext) context, FacilioConstants.ContextNames.PREVENTIVE_MAINTENANCE, FacilioConstants.ContextNames.PREVENTIVE_MAINTENANCE_LIST);
		PMJobsContext currentJob = (PMJobsContext) context.get(FacilioConstants.ContextNames.PM_CURRENT_JOB);
		
		if(pms != null && !pms.isEmpty()) {
			Map<Long, WorkOrderContext> pmToWo = (Map<Long, WorkOrderContext>) context.get(FacilioConstants.ContextNames.PM_TO_WO);
			
			long currentExecutionTime = -1;
			if(context.get(FacilioConstants.ContextNames.CURRENT_EXECUTION_TIME) != null) {
				currentExecutionTime = (long) context.get(FacilioConstants.ContextNames.CURRENT_EXECUTION_TIME);
			}
			
			FacilioModule module = ModuleFactory.getPMReminderModule();
			for(PreventiveMaintenance pm : pms) {
				GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
																.table(module.getTableName())
																.select(FieldFactory.getPMReminderFields())
																.andCondition(CriteriaAPI.getCurrentOrgIdCondition(module))
																.andCustomWhere("PM_ID = ?", pm.getId())
																.andCustomWhere("REMINDER_TYPE != ?", ReminderType.BEFORE_EXECUTION.getValue())
																;
				
				List<Map<String, Object>> reminderProps = selectBuilder.get();
				if(reminderProps != null && !reminderProps.isEmpty()) {
					
					List<PMReminder> reminders = FieldUtil.getAsBeanListFromMapList(reminderProps, PMReminder.class);
					
					List<PMReminder> remindersToBeExecuted = new ArrayList<>();
					if(pm.getPmCreationTypeEnum().equals(PreventiveMaintenance.PMCreationType.MULTIPLE)) {
						
						Map<Long, PMReminder> pmReminderMap = PreventiveMaintenanceAPI.getReminderMap(reminders);
						
						PMResourcePlannerContext planner = PreventiveMaintenanceAPI.getPMResourcePlanner(pm.getId(), currentJob.getResourceId());
						if(planner != null && planner.getPmResourcePlannerReminderContexts() != null && !planner.getPmResourcePlannerReminderContexts().isEmpty()) {
							for(PMResourcePlannerReminderContext pmResPlannerRem :planner.getPmResourcePlannerReminderContexts()) {
								PMReminder rem = pmReminderMap.get(pmResPlannerRem.getReminderId());		// reminder might also have before execution type.
								if(rem != null) {
									remindersToBeExecuted.add(rem);
								}
							}
						}
						else {
							remindersToBeExecuted.add(reminders.get(0));
						}
					}
					else {
						
						remindersToBeExecuted.addAll(reminders);
					}
					
					WorkOrderContext wo = pmToWo.get(pm.getId());
					for(PMReminder reminder : remindersToBeExecuted) {
						switch(reminder.getTypeEnum()) {
							case BEFORE_EXECUTION:
								throw new RuntimeException("This is not supposed to happen");
							case AFTER_EXECUTION:
								if(wo != null) {
									PreventiveMaintenanceAPI.schedulePostPMReminder(reminder, (currentExecutionTime + reminder.getDuration()), wo.getId());
								}
								break;
							case BEFORE_DUE:
								if(wo != null && wo.getDueDate() != -1) {
									PreventiveMaintenanceAPI.schedulePostPMReminder(reminder, ((wo.getDueDate()/1000) - reminder.getDuration()), wo.getId());
								}
								break;
							case AFTER_DUE:
								if(wo != null && wo.getDueDate() != -1) {
									PreventiveMaintenanceAPI.schedulePostPMReminder(reminder, ((wo.getDueDate()/1000) + reminder.getDuration()), wo.getId());
								}
								break;
						}
					}
				}
			}
		}
		return false;
	}
}
