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
import com.facilio.bmsconsole.util.ActionAPI;
import com.facilio.bmsconsole.util.TicketAPI;
import com.facilio.bmsconsole.util.WorkflowRuleAPI;
import com.facilio.bmsconsole.workflow.rule.ActionContext;
import com.facilio.bmsconsole.workflow.rule.EventType;
import com.facilio.bmsconsole.workflow.rule.WorkflowRuleContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;

public class ExecuteNoteWorkflowCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		List<NoteContext> notes = (List<NoteContext>) context.get(FacilioConstants.ContextNames.NOTE_LIST);
		if(notes != null && !notes.isEmpty()) {
			String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
			if(moduleName.equals(FacilioConstants.ContextNames.TICKET_NOTES)) {
				String ticketModule = (String) context.get(FacilioConstants.ContextNames.TICKET_MODULE);
				EventType eventType = (EventType) context.get(FacilioConstants.ContextNames.ACTIVITY_TYPE);
				
				for (NoteContext note : notes) {
					executeWorkflow(note, ticketModule, eventType, context);
				}
			}
		}
		return false;
	}
	
	private void executeWorkflow(NoteContext note, String ticketModule, EventType eventType, Context context) throws Exception {
		long parentId = note.getParentId();
		if(parentId != -1 && eventType != null && eventType == EventType.ADD_TICKET_NOTE) {
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
//			long moduleId = modBean.getModule(FacilioConstants.ContextNames.TICKET).getModuleId();
			long moduleId = modBean.getModule(ticketModule).getModuleId();
			List<WorkflowRuleContext> workflowRules = WorkflowRuleAPI.getActiveWorkflowRulesFromActivityAndRuleType(moduleId, Collections.singletonList(eventType), null);
			if(workflowRules != null && workflowRules.size() > 0) {
				WorkflowRuleContext workflowRule = workflowRules.get(0);
				TicketContext ticket = TicketAPI.getParentTicket(note.getParentId(), ticketModule);
				if(ticket != null && ticket.getAssignedTo() != null && ticket.getAssignedTo().getId() != note.getCreatedBy().getId()) {
					long workflowRuleId = workflowRule.getId();
					List<ActionContext> actions = ActionAPI.getActiveActionsFromWorkflowRule(workflowRuleId);
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
