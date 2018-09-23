package com.facilio.bmsconsole.commands;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.context.PMReminder;
import com.facilio.bmsconsole.context.PMReminder.ReminderType;
import com.facilio.bmsconsole.context.PMTriggerContext;
import com.facilio.bmsconsole.context.PreventiveMaintenance;
import com.facilio.bmsconsole.context.WorkOrderContext;
import com.facilio.bmsconsole.criteria.CriteriaAPI;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.bmsconsole.modules.FieldFactory;
import com.facilio.bmsconsole.modules.FieldUtil;
import com.facilio.bmsconsole.modules.ModuleFactory;
import com.facilio.bmsconsole.util.PreventiveMaintenanceAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.sql.GenericSelectRecordBuilder;

public class SchedulePMRemindersCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		List<PreventiveMaintenance> pms = (List<PreventiveMaintenance>) context.get(FacilioConstants.ContextNames.PREVENTIVE_MAINTENANCE_LIST);
		if(pms == null) {
			PreventiveMaintenance pm = (PreventiveMaintenance) context.get(FacilioConstants.ContextNames.PREVENTIVE_MAINTENANCE);
			if (pm != null) {
				pms = Collections.singletonList(pm);
			}
		}
		
		if(pms != null && !pms.isEmpty()) {
			Map<Long, List<PMTriggerContext>> pmTriggersMap = (Map<Long, List<PMTriggerContext>>) context.get(FacilioConstants.ContextNames.PM_TRIGGERS);
			Map<Long, Long> nextExecutionTimes = (Map<Long, Long>) context.get(FacilioConstants.ContextNames.NEXT_EXECUTION_TIMES);
			
			List<Long> pmIds = (List<Long>) context.get(FacilioConstants.ContextNames.RECORD_ID_LIST);
			Map<Long, WorkOrderContext> pmToWo = null;
			if(pmIds == null) {
				long pmId = (long) context.get(FacilioConstants.ContextNames.RECORD_ID);
				pmIds = Collections.singletonList(pmId);
				pmToWo = new HashMap<>();
				
				WorkOrderContext wo = (WorkOrderContext) context.get(FacilioConstants.ContextNames.WORK_ORDER);
				if (wo != null) {
					pmToWo.put(pmId, wo);
				}
			}
			else {
				pmToWo = (Map<Long, WorkOrderContext>) context.get(FacilioConstants.ContextNames.PM_TO_WO);
			}
			long currentExecutionTime = -1;
			if(context.get(FacilioConstants.ContextNames.CURRENT_EXECUTION_TIME) != null) {
				currentExecutionTime = (long) context.get(FacilioConstants.ContextNames.CURRENT_EXECUTION_TIME);
			}
			
			Boolean onlyPost = (Boolean) context.get(FacilioConstants.ContextNames.ONLY_POST_REMINDER_TYPE);
			FacilioModule module = ModuleFactory.getPMReminderModule();
			
			for(long pmId : pmIds) {
				GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
																.table(module.getTableName())
																.select(FieldFactory.getPMReminderFields())
																.andCondition(CriteriaAPI.getCurrentOrgIdCondition(module))
																.andCustomWhere("PM_ID = ?", pmId);
				
				if(onlyPost != null && onlyPost) {
					selectBuilder.andCustomWhere("REMINDER_TYPE != ?", ReminderType.BEFORE_EXECUTION.getValue());
				}
				
				List<Map<String, Object>> reminderProps = selectBuilder.get();
				if(reminderProps != null && !reminderProps.isEmpty()) {
					for(Map<String, Object> reminderProp : reminderProps) {
						PMReminder reminder = FieldUtil.getAsBeanFromMap(reminderProp, PMReminder.class);
						switch(reminder.getTypeEnum()) {
							case BEFORE_EXECUTION:
								for(PMTriggerContext trigger : pmTriggersMap.get(pmId)) {
									Long nextExecutionTime = nextExecutionTimes.get(trigger.getId());
									if(nextExecutionTime != null) {
										PreventiveMaintenanceAPI.schedulePrePMReminder(reminder, nextExecutionTime, trigger.getId());
									}
								}
								break;
							case AFTER_EXECUTION:
								WorkOrderContext wo = pmToWo.get(pmId);
								if(wo != null) {
									PreventiveMaintenanceAPI.schedulePostPMReminder(reminder, (currentExecutionTime + reminder.getDuration()), wo.getId());
								}
								break;
							case BEFORE_DUE:
								wo = pmToWo.get(pmId);
								if(wo != null && wo.getDueDate() != -1) {
									PreventiveMaintenanceAPI.schedulePostPMReminder(reminder, ((wo.getDueDate()/1000) - reminder.getDuration()), wo.getId());
								}
								break;
							case AFTER_DUE:
								wo = pmToWo.get(pmId);
								if(wo != null) {
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
