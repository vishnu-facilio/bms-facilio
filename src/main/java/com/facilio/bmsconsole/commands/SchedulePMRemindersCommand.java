package com.facilio.bmsconsole.commands;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.context.PMReminder;
import com.facilio.bmsconsole.context.PMReminder.ReminderType;
import com.facilio.bmsconsole.criteria.CriteriaAPI;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.bmsconsole.modules.FieldFactory;
import com.facilio.bmsconsole.modules.FieldUtil;
import com.facilio.bmsconsole.modules.ModuleFactory;
import com.facilio.constants.FacilioConstants;
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
			pmToWo.put(pmId, (Long) context.get(FacilioConstants.ContextNames.ID));
		}
		else {
			pmToWo = (Map<Long, Long>) context.get(FacilioConstants.ContextNames.PM_TO_WO);
		}
		long currentExecutionTime = -1;
		if(context.get(FacilioConstants.ContextNames.CURRENT_EXECUTION_TIME) != null) {
			currentExecutionTime = (long) context.get(FacilioConstants.ContextNames.CURRENT_EXECUTION_TIME);
		}
		
		long nextExecutionTime = -1;
		if(context.get(FacilioConstants.ContextNames.NEXT_EXECUTION_TIME) != null) {
			nextExecutionTime = (long) context.get(FacilioConstants.ContextNames.NEXT_EXECUTION_TIME);
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
				for(Map<String, Object> reminderProp : reminderProps) {
					PMReminder reminder = FieldUtil.getAsBeanFromMap(reminderProp, PMReminder.class);
					CommonCommandUtil.schedulePMRemainder(reminder, currentExecutionTime, nextExecutionTime, pmToWo.get(pmId));
				}
			}
		}
		return false;
	}

}
