package com.facilio.bmsconsole.commands;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.context.NoteContext;
import com.facilio.bmsconsole.context.TicketContext;
import com.facilio.bmsconsole.util.TicketAPI;
import com.facilio.bmsconsole.util.WorkflowRuleAPI;
import com.facilio.bmsconsole.workflow.rule.EventType;
import com.facilio.bmsconsole.workflow.rule.WorkflowRuleContext;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldUtil;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ExecuteNoteWorkflowCommand extends FacilioCommand implements Serializable {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		List<NoteContext> notes = (List<NoteContext>) context.get(FacilioConstants.ContextNames.NOTE_LIST);
		if(notes != null && !notes.isEmpty()) {
			String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
			if(moduleName.equals(FacilioConstants.ContextNames.TICKET_NOTES)) {
				String ticketModule = (String) context.get(FacilioConstants.ContextNames.PARENT_MODULE_NAME);
				EventType eventType = (EventType) context.get(FacilioConstants.ContextNames.EVENT_TYPE);
				
				for (NoteContext note : notes) {
					executeWorkflow(note, ticketModule, eventType, context);
				}
			}
		}
		return false;
	}
	
	private void executeWorkflow(NoteContext note, String ticketModule, EventType eventType, Context context) throws Exception {
		long parentId = note.getParentId();
		if(parentId != -1 && eventType != null && (eventType == EventType.ADD_TICKET_NOTE || eventType == EventType.ADD_NOTE_REQUESTER)) {
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			FacilioModule module = modBean.getModule(ticketModule);
			List<WorkflowRuleContext> workflowRules = WorkflowRuleAPI.getActiveWorkflowRulesFromActivityAndRuleType(module, Collections.singletonList(eventType), null);
			if (CollectionUtils.isNotEmpty(workflowRules)) {
				for (WorkflowRuleContext workflowRule : workflowRules) {
					TicketContext ticket = TicketAPI.getParentTicket(note.getParentId(), ticketModule);
					if (ticket != null) {
						Map<String, Object> placeHolders = new HashMap<>();
						CommonCommandUtil.appendModuleNameInKey(ticketModule, FacilioConstants.ContextNames.WORK_ORDER, FieldUtil.getAsProperties(ticket), placeHolders);
						CommonCommandUtil.appendModuleNameInKey(null, "org", FieldUtil.getAsProperties(AccountUtil.getCurrentOrg()), placeHolders);
						CommonCommandUtil.appendModuleNameInKey(null, "user", FieldUtil.getAsProperties(AccountUtil.getCurrentUser()), placeHolders);
						CommonCommandUtil.appendModuleNameInKey(null, "comment", FieldUtil.getAsProperties(note), placeHolders);
						WorkflowRuleAPI.evaluateWorkflowAndExecuteActions(workflowRule, ticketModule, ticket, null, placeHolders, (FacilioContext) context, true);

					}
				}
			}
		}
	}
}
