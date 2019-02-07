package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.context.PMJobsContext;
import com.facilio.bmsconsole.context.PMReminder;
import com.facilio.bmsconsole.context.PMReminder.ReminderType;
import com.facilio.bmsconsole.context.PMResourcePlannerContext;
import com.facilio.bmsconsole.context.PMResourcePlannerReminderContext;
import com.facilio.bmsconsole.context.PMTriggerContext;
import com.facilio.bmsconsole.context.PreventiveMaintenance;
import com.facilio.bmsconsole.criteria.CriteriaAPI;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.bmsconsole.modules.FieldFactory;
import com.facilio.bmsconsole.modules.FieldUtil;
import com.facilio.bmsconsole.modules.ModuleFactory;
import com.facilio.bmsconsole.util.PreventiveMaintenanceAPI;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.serializable.SerializableCommand;
import com.facilio.sql.GenericSelectRecordBuilder;

public class SchedulePrePMRemindersCommand implements SerializableCommand {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private static final Logger LOGGER = Logger.getLogger(SchedulePrePMRemindersCommand.class.getName());

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		List<PreventiveMaintenance> pms = CommonCommandUtil.getList((FacilioContext) context, FacilioConstants.ContextNames.PREVENTIVE_MAINTENANCE, FacilioConstants.ContextNames.PREVENTIVE_MAINTENANCE_LIST);
		PMJobsContext currentJob = (PMJobsContext) context.get(FacilioConstants.ContextNames.PM_CURRENT_JOB);
		Boolean onlyPost = (Boolean) context.get(FacilioConstants.ContextNames.ONLY_POST_REMINDER_TYPE);
		if (onlyPost == null) {
			onlyPost = false;
		}
		if(!onlyPost && pms != null && !pms.isEmpty()) {
			Map<Long, List<PMTriggerContext>> pmTriggersMap = (Map<Long, List<PMTriggerContext>>) context.get(FacilioConstants.ContextNames.PM_TRIGGERS);
			Map<Long, Long> nextExecutionTimes = (Map<Long, Long>) context.get(FacilioConstants.ContextNames.NEXT_EXECUTION_TIMES);
			
			if (pmTriggersMap != null && nextExecutionTimes != null) {
				FacilioModule module = ModuleFactory.getPMReminderModule();
				for(PreventiveMaintenance pm : pms) {
					GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
																	.table(module.getTableName())
																	.select(FieldFactory.getPMReminderFields())
																	.andCondition(CriteriaAPI.getCurrentOrgIdCondition(module))
																	.andCustomWhere("PM_ID = ?", pm.getId())
																	.andCustomWhere("REMINDER_TYPE = ?", ReminderType.BEFORE_EXECUTION.getValue())
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
									PMReminder rem = pmReminderMap.get(pmResPlannerRem.getReminderId());
									if(rem != null) {													// reminder might also have type other than before execution type.
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
						for(PMReminder reminder :remindersToBeExecuted) {
							
							switch(reminder.getTypeEnum()) {
							case BEFORE_EXECUTION:
								for(PMTriggerContext trigger : pmTriggersMap.get(pm.getId())) {				// doubt why using pmTriggersMap (What is the need of iteration)? 
									if(pm.getPmCreationTypeEnum().equals(PreventiveMaintenance.PMCreationType.MULTIPLE) && currentJob.getPmTriggerId() == trigger.getId()) {	// handling separately to avoid singlePM flow breakage - merge after checking
										Long nextExecutionTime = nextExecutionTimes.get(trigger.getId());
										if(nextExecutionTime != null) {
											PreventiveMaintenanceAPI.schedulePrePMReminder(reminder, nextExecutionTime, trigger.getId(),currentJob.getResourceId());
										}
									}
									else {
										Long nextExecutionTime = nextExecutionTimes.get(trigger.getId());
										if(nextExecutionTime != null) {
											PreventiveMaintenanceAPI.schedulePrePMReminder(reminder, nextExecutionTime, trigger.getId(),-1l);
										}
									}
								}
								break;
							default:
								throw new RuntimeException("This is not supposed to happen");
							}
						}
					}
				}
			}
		}
		return false;
	}
	
}
