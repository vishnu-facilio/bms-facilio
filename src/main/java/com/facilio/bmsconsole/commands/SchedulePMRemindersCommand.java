package com.facilio.bmsconsole.commands;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.context.PMReminder;
import com.facilio.bmsconsole.context.WorkOrderContext;
import com.facilio.bmsconsole.context.PMReminder.ReminderType;
import com.facilio.bmsconsole.criteria.CriteriaAPI;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.bmsconsole.modules.FieldFactory;
import com.facilio.bmsconsole.modules.FieldUtil;
import com.facilio.bmsconsole.modules.ModuleFactory;
import com.facilio.constants.FacilioConstants;
import com.facilio.leed.context.PMTriggerContext;
import com.facilio.sql.GenericSelectRecordBuilder;

public class SchedulePMRemindersCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		List<Long> pmIds = (List<Long>) context.get(FacilioConstants.ContextNames.RECORD_ID_LIST);
		Map<Long, Long> pmToWo = null;
		if(pmIds == null) {
			long pmId = (long) context.get(FacilioConstants.ContextNames.RECORD_ID);
			pmIds = Collections.singletonList(pmId);
			pmToWo = new HashMap<>();
			
			WorkOrderContext wo = (WorkOrderContext) context.get(FacilioConstants.ContextNames.WORK_ORDER);
			if (wo != null) {
				pmToWo.put(pmId, wo.getId());
			}
		}
		else {
			pmToWo = (Map<Long, Long>) context.get(FacilioConstants.ContextNames.PM_TO_WO);
		}
		long currentExecutionTime = -1;
		if(context.get(FacilioConstants.ContextNames.CURRENT_EXECUTION_TIME) != null) {
			currentExecutionTime = (long) context.get(FacilioConstants.ContextNames.CURRENT_EXECUTION_TIME);
		}
		
		PMReminder.ReminderType reminderType = (ReminderType) context.get(FacilioConstants.ContextNames.PM_REMINDER_TYPE);
		FacilioModule module = ModuleFactory.getPMReminderModule();
		
		for(long pmId : pmIds) {
			GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
															.table(module.getTableName())
															.select(FieldFactory.getPMReminderFields())
															.andCondition(CriteriaAPI.getCurrentOrgIdCondition(module))
															.andCustomWhere("PM_ID = ?", pmId);
			
			if(reminderType != null) {
				selectBuilder.andCustomWhere("REMINDER_TYPE = ?", reminderType.getIntVal());
			}
			
			List<Map<String, Object>> reminderProps = selectBuilder.get();
			if(reminderProps != null && !reminderProps.isEmpty()) {
				Map<Long, List<PMTriggerContext>> pmTriggersMap = (Map<Long, List<PMTriggerContext>>) context.get(FacilioConstants.ContextNames.PM_TRIGGERS);
				Map<Long, Long> nextExecutionTimes = (Map<Long, Long>) context.get(FacilioConstants.ContextNames.NEXT_EXECUTION_TIMES);
				for(Map<String, Object> reminderProp : reminderProps) {
					PMReminder reminder = FieldUtil.getAsBeanFromMap(reminderProp, PMReminder.class);
					switch(reminder.getTypeEnum()) {
						case BEFORE:
							for(PMTriggerContext trigger : pmTriggersMap.get(pmId)) {
								Long nextExecutionTime = nextExecutionTimes.get(trigger.getId());
								if(nextExecutionTime != null) {
									CommonCommandUtil.scheduleBeforePMReminder(reminder, nextExecutionTime, trigger.getId());
								}
							}
							break;
						case AFTER:
							Long woId = pmToWo.get(pmId);
							if(woId != null) {
								CommonCommandUtil.scheduleAfterPMReminder(reminder, currentExecutionTime, woId);
							}
							break;
					}
				}
			}
		}
		return false;
	}

}
