package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.context.PMReminder;
import com.facilio.bmsconsole.context.PreventiveMaintenance;
import com.facilio.bmsconsole.criteria.CriteriaAPI;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.bmsconsole.modules.ModuleFactory;
import com.facilio.bmsconsole.util.PreventiveMaintenanceAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.sql.GenericDeleteRecordBuilder;

public class DeletePMRemindersCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		
		PreventiveMaintenance pm = (PreventiveMaintenance) context.get(FacilioConstants.ContextNames.PREVENTIVE_MAINTENANCE);
		if(pm != null && pm.getId() != -1) {
			
			List<PMReminder> reminders = PreventiveMaintenanceAPI.getPMReminders(pm.getId());
			
			List<Long> actionIds = new ArrayList<>();
			actionIds.addAll(reminders.stream().map(PMReminder::getActionId).collect(Collectors.toList()));
			
			FacilioModule module = ModuleFactory.getPMReminderModule();
			GenericDeleteRecordBuilder deleteBuilder = new GenericDeleteRecordBuilder()
					.table(module.getTableName())
					.andCondition(CriteriaAPI.getIdCondition(pm.getId(), module));
			deleteBuilder.delete();

		}
		
		return false;
	}
	
	

}
