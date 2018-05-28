package com.facilio.bmsconsole.commands;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.context.NoteContext;
import com.facilio.bmsconsole.context.TicketContext;
import com.facilio.bmsconsole.modules.FieldUtil;
import com.facilio.bmsconsole.modules.SelectRecordsBuilder;
import com.facilio.bmsconsole.util.ActionAPI;
import com.facilio.bmsconsole.util.WorkflowRuleAPI;
import com.facilio.bmsconsole.workflow.ActionContext;
import com.facilio.bmsconsole.workflow.ActivityType;
import com.facilio.bmsconsole.workflow.WorkflowRuleContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;

public class ExecuteNoteWorkflowCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		NoteContext note = (NoteContext) context.get(FacilioConstants.ContextNames.NOTE);
		if(note != null) {
			long orgId = AccountUtil.getCurrentOrg().getOrgId();
			String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
			if(moduleName.equals(FacilioConstants.ContextNames.TICKET_NOTES)) {
				String ticketModule = (String) context.get(FacilioConstants.ContextNames.TICKET_MODULE);
				long parentId = note.getParentId();
				ActivityType eventType = (ActivityType) context.get(FacilioConstants.ContextNames.ACTIVITY_TYPE);
				if(parentId != -1 && eventType != null && eventType == ActivityType.ADD_TICKET_NOTE) {
					ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
//					long moduleId = modBean.getModule(FacilioConstants.ContextNames.TICKET).getModuleId();
					long moduleId = modBean.getModule(ticketModule).getModuleId();
					List<WorkflowRuleContext> workflowRules = WorkflowRuleAPI.getActiveWorkflowRulesFromActivityAndRuleType(moduleId, Collections.singletonList(eventType), null);
					if(workflowRules != null && workflowRules.size() > 0) {
						WorkflowRuleContext workflowRule = workflowRules.get(0);
						TicketContext ticket = getParentTicket(note, ticketModule);
						if(ticket != null && ticket.getAssignedTo() != null && ticket.getAssignedTo().getId() != note.getCreatedBy().getId()) {
							long workflowRuleId = workflowRule.getId();
							List<ActionContext> actions = ActionAPI.getActiveActionsFromWorkflowRule(orgId, workflowRuleId);
							if(actions != null) {
								Map<String, Object> placeHolders = new HashMap<>();
								CommonCommandUtil.appendModuleNameInKey(ticketModule, FacilioConstants.ContextNames.WORK_ORDER, FieldUtil.getAsProperties(ticket), placeHolders);
								CommonCommandUtil.appendModuleNameInKey(null, "org", FieldUtil.getAsProperties(AccountUtil.getCurrentOrg()), placeHolders);
								CommonCommandUtil.appendModuleNameInKey(null, "user", FieldUtil.getAsProperties(AccountUtil.getCurrentUser()), placeHolders);
								CommonCommandUtil.appendModuleNameInKey(null, "comment", FieldUtil.getAsProperties(note), placeHolders);
								for(ActionContext action : actions)
								{
									action.executeAction(placeHolders, context, workflowRule, ticket);
								}
							}
						}
					}
				}
			}
		}
		return false;
	}
	
	@SuppressWarnings("unchecked")
	private TicketContext getParentTicket (NoteContext note, String parentModuleName) throws Exception {
		long parentTicketId = note.getParentId();
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		SelectRecordsBuilder<? extends TicketContext> ticketBuilder = new SelectRecordsBuilder<TicketContext>()
																.select(modBean.getAllFields(parentModuleName))
																.moduleName(parentModuleName)
																.beanClass(FacilioConstants.ContextNames.getClassFromModuleName(parentModuleName))
																.andCustomWhere("Tickets.ID = ?", parentTicketId)
																;
		
		List<? extends TicketContext> tickets = ticketBuilder.get();
		if(tickets != null && !tickets.isEmpty()) {
			return tickets.get(0);
		}
		
		return null;
	}

}
