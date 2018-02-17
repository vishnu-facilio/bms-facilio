package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.context.PMReminder;
import com.facilio.bmsconsole.context.PreventiveMaintenance;
import com.facilio.bmsconsole.util.ActionAPI;
import com.facilio.bmsconsole.util.PreventiveMaintenanceAPI;
import com.facilio.constants.FacilioConstants;

public class DeletePMRemindersCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		
		PreventiveMaintenance pm = (PreventiveMaintenance) context.get(FacilioConstants.ContextNames.PREVENTIVE_MAINTENANCE);
		List<PMReminder> newReminders = (List<PMReminder>) context.get(FacilioConstants.ContextNames.PM_REMINDERS);
		if(pm != null && pm.getId() != -1 && newReminders != null) {
			
			List<PMReminder> reminders = PreventiveMaintenanceAPI.getPMReminders(pm.getId());
			
			List<Long> actionIds = new ArrayList<>();
			actionIds.addAll(reminders.stream().map(PMReminder::getActionId).collect(Collectors.toList()));
			ActionAPI.deleteActions(actionIds);
			
			/*FacilioModule module = ModuleFactory.getPMReminderModule();
			GenericDeleteRecordBuilder deleteBuilder = new GenericDeleteRecordBuilder()
					.table(module.getTableName())
					.andCondition(CriteriaAPI.getIdCondition(pm.getId(), module));
			deleteBuilder.delete();*/

		}
		
		return false;
	}
	
	

}
