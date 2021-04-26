package com.facilio.bmsconsole.commands;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.context.NoteContext;
import com.facilio.bmsconsole.util.RecordAPI;
import com.facilio.bmsconsole.util.WorkflowRuleAPI;
import com.facilio.bmsconsole.workflow.rule.EventType;
import com.facilio.bmsconsole.workflow.rule.WorkflowRuleContext;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleBaseWithCustomFields;

public class ExecuteNoteWorkflowCommand extends FacilioCommand implements Serializable {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		List<NoteContext> notes = (List<NoteContext>) context.get(FacilioConstants.ContextNames.NOTE_LIST);
		if(notes != null && !notes.isEmpty()) {
			String parentModule = (String) context.get(FacilioConstants.ContextNames.PARENT_MODULE_NAME);
			if(parentModule != null) {
				EventType eventType = (EventType) context.get(FacilioConstants.ContextNames.EVENT_TYPE);
				
				for (NoteContext note : notes) {
					executeWorkflow(note, parentModule, eventType, context);
				}
			}
		}
		return false;
	}
	
	private void executeWorkflow(NoteContext note, String parentModule, EventType eventType, Context context) throws Exception {
		long parentId = note.getParentId();
		if(parentId != -1 && eventType != null && (eventType == EventType.ADD_TICKET_NOTE || eventType == EventType.ADD_NOTE_REQUESTER)) {
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			FacilioModule module = modBean.getModule(parentModule);
			List<WorkflowRuleContext> workflowRules = WorkflowRuleAPI.getActiveWorkflowRulesFromActivityAndRuleType(module, Collections.singletonList(eventType), null);
			if (CollectionUtils.isNotEmpty(workflowRules)) {
				ModuleBaseWithCustomFields record = RecordAPI.getRecord(parentModule, note.getParentId());
				if (record != null) {
					for (WorkflowRuleContext workflowRule : workflowRules) {
						Map<String, Object> placeHolders = new HashMap<>();
						CommonCommandUtil.appendModuleNameInKey(parentModule, parentModule, FieldUtil.getAsProperties(record), placeHolders);
						CommonCommandUtil.appendModuleNameInKey(null, "org", FieldUtil.getAsProperties(AccountUtil.getCurrentOrg()), placeHolders);
						CommonCommandUtil.appendModuleNameInKey(null, "user", FieldUtil.getAsProperties(AccountUtil.getCurrentUser()), placeHolders);
						CommonCommandUtil.appendModuleNameInKey(null, "comment", FieldUtil.getAsProperties(note), placeHolders);
						WorkflowRuleAPI.evaluateWorkflowAndExecuteActions(workflowRule, parentModule, record, null, placeHolders, (FacilioContext) context, true);

					}
				}
			}
		}
	}
}
