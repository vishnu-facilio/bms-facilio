package com.facilio.bmsconsole.commands;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.context.PMReminder;
import com.facilio.bmsconsole.context.PMReminderAction;
import com.facilio.bmsconsole.context.PreventiveMaintenance;
import com.facilio.bmsconsole.context.TicketStatusContext;
import com.facilio.bmsconsole.context.WorkOrderContext;
import com.facilio.bmsconsole.modules.FieldUtil;
import com.facilio.bmsconsole.util.ActionAPI;
import com.facilio.bmsconsole.util.TicketAPI;
import com.facilio.bmsconsole.workflow.rule.ActionContext;
import com.facilio.constants.FacilioConstants;

public class ExecutePMReminderCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		WorkOrderContext wo = (WorkOrderContext) context.get(FacilioConstants.ContextNames.WORK_ORDER);
		PMReminder pmReminder = (PMReminder) context.get(FacilioConstants.ContextNames.PM_REMINDER);
		PreventiveMaintenance pm = (PreventiveMaintenance) context.get(FacilioConstants.ContextNames.PREVENTIVE_MAINTENANCE);
		
		if(wo != null && (pmReminder.getTypeEnum() == PMReminder.ReminderType.BEFORE_EXECUTION || !isClosed(wo))) {
			for(PMReminderAction reminderAction : pmReminder.getReminderActions())  {
				ActionContext action = ActionAPI.getAction(reminderAction.getActionId());
				if(action != null) {
					Map<String, Object> placeHolders = new HashMap<>();
					CommonCommandUtil.appendModuleNameInKey(FacilioConstants.ContextNames.WORK_ORDER, FacilioConstants.ContextNames.WORK_ORDER, FieldUtil.getAsProperties(wo), placeHolders);
					CommonCommandUtil.appendModuleNameInKey(null, "pm", FieldUtil.getAsProperties(pm), placeHolders);
					CommonCommandUtil.appendModuleNameInKey(null, "org", FieldUtil.getAsProperties(AccountUtil.getCurrentOrg()), placeHolders);
					CommonCommandUtil.appendModuleNameInKey(null, "user", FieldUtil.getAsProperties(AccountUtil.getCurrentUser()), placeHolders);
					action.executeAction(placeHolders, null, null, null);
				}
			}
		}
		
		return false;
	}
	
	private boolean isClosed(WorkOrderContext wo) throws Exception {
		TicketStatusContext status = TicketAPI.getStatus("Closed");
		if(wo.getStatus().getId() == status.getId()) {
			return true;
		}
		return false;
	}
}
