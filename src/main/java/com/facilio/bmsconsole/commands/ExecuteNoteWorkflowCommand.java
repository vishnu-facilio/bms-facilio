package com.facilio.bmsconsole.commands;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.json.simple.JSONObject;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.context.NoteContext;
import com.facilio.bmsconsole.context.TicketContext;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FieldType;
import com.facilio.bmsconsole.modules.FieldUtil;
import com.facilio.bmsconsole.modules.LookupField;
import com.facilio.bmsconsole.modules.ModuleFactory;
import com.facilio.bmsconsole.modules.SelectRecordsBuilder;
import com.facilio.bmsconsole.util.ActionAPI;
import com.facilio.bmsconsole.util.WorkflowAPI;
import com.facilio.bmsconsole.workflow.ActionContext;
import com.facilio.bmsconsole.workflow.ActionTemplate;
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
				long parentId = note.getParentId();
				ActivityType eventType = (ActivityType) context.get(FacilioConstants.ContextNames.ACTIVITY_TYPE);
				if(parentId != -1 && eventType != null && eventType == ActivityType.ADD_TICKET_NOTE) {
					ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
					long moduleId = modBean.getModule(FacilioConstants.ContextNames.TICKET).getModuleId();
					List<WorkflowRuleContext> workflowRules = WorkflowAPI.getActiveWorkflowRulesFromActivityAndRuleType(moduleId, Collections.singletonList(eventType));
					if(workflowRules != null && workflowRules.size() > 0) {
						WorkflowRuleContext workflowRule = workflowRules.get(0);
						TicketContext ticket = getParentTicket(note);
						if(ticket != null && ticket.getAssignedTo() != null && ticket.getAssignedTo().getId() != note.getCreatedBy().getId()) {
							long workflowRuleId = workflowRule.getId();
							List<ActionContext> actions = ActionAPI.getActiveActionsFromWorkflowRule(orgId, workflowRuleId);
							if(actions != null) {
								Map<String, Object> placeHolders = new HashMap<>();
								CommonCommandUtil.appendModuleNameInKey(FacilioConstants.ContextNames.TICKET, FacilioConstants.ContextNames.TICKET, FieldUtil.getAsProperties(ticket), placeHolders);
								CommonCommandUtil.appendModuleNameInKey(null, "org", FieldUtil.getAsProperties(AccountUtil.getCurrentOrg()), placeHolders);
								CommonCommandUtil.appendModuleNameInKey(null, "user", FieldUtil.getAsProperties(AccountUtil.getCurrentUser()), placeHolders);
								for(ActionContext action : actions)
								{
									ActionTemplate template = action.getTemplate();
									if(template != null) {
										JSONObject actionObj = template.getTemplate(placeHolders);
										action.getActionType().performAction(actionObj, context);
									}
								}
							}
						}
					}
				}
			}
		}
		return false;
	}
	
	private TicketContext getParentTicket (NoteContext note) throws Exception {
		long parentTicketId = note.getParentId();
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		
		LookupField userField = new LookupField();
		userField.setName("assignedTo");
		userField.setColumnName("ASSIGNED_TO_ID");
		userField.setDataType(FieldType.LOOKUP);
		userField.setModule(ModuleFactory.getTicketsModule());
		userField.setSpecialType(FacilioConstants.ContextNames.USERS);
		
		List<FacilioField> fields = new ArrayList<>();
		fields.add(userField);
		
		SelectRecordsBuilder<TicketContext> ticketBuilder = new SelectRecordsBuilder<TicketContext>()
																.select(fields)
																.table("Tickets")
																.moduleName(FacilioConstants.ContextNames.TICKET)
																.beanClass(TicketContext.class)
																.andCustomWhere("ID = ?", parentTicketId)
																;
		
		List<TicketContext> tickets = ticketBuilder.get();
		if(tickets != null && !tickets.isEmpty()) {
			return tickets.get(0);
		}
		
		return null;
	}

}
