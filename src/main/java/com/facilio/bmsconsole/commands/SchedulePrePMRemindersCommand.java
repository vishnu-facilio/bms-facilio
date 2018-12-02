package com.facilio.bmsconsole.commands;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.context.PMReminder;
import com.facilio.bmsconsole.context.PMReminder.ReminderType;
import com.facilio.bmsconsole.context.PMTriggerContext;
import com.facilio.bmsconsole.context.PreventiveMaintenance;
import com.facilio.bmsconsole.criteria.CriteriaAPI;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.bmsconsole.modules.FieldFactory;
import com.facilio.bmsconsole.modules.FieldUtil;
import com.facilio.bmsconsole.modules.ModuleFactory;
import com.facilio.bmsconsole.util.PreventiveMaintenanceAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.sql.GenericSelectRecordBuilder;

public class SchedulePrePMRemindersCommand implements Command, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		List<PreventiveMaintenance> pms = CommonCommandUtil.getList((FacilioContext) context, FacilioConstants.ContextNames.PREVENTIVE_MAINTENANCE, FacilioConstants.ContextNames.PREVENTIVE_MAINTENANCE_LIST);
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
						for(Map<String, Object> reminderProp : reminderProps) {
							PMReminder reminder = FieldUtil.getAsBeanFromMap(reminderProp, PMReminder.class);
							switch(reminder.getTypeEnum()) {
								case BEFORE_EXECUTION:
									for(PMTriggerContext trigger : pmTriggersMap.get(pm.getId())) {
										Long nextExecutionTime = nextExecutionTimes.get(trigger.getId());
										if(nextExecutionTime != null) {
											PreventiveMaintenanceAPI.schedulePrePMReminder(reminder, nextExecutionTime, trigger.getId());
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
