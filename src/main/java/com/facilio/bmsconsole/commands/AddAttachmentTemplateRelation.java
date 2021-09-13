package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.List;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.ModuleFactory;

import org.apache.commons.chain.Context;

import com.facilio.accounts.util.EmailAttachmentAPI;
import com.facilio.bmsconsole.context.TemplateFileContext;
import com.facilio.bmsconsole.context.TemplateUrlContext;
import com.facilio.bmsconsole.util.ActionAPI;
import com.facilio.bmsconsole.util.TemplateAPI;
import com.facilio.bmsconsole.workflow.rule.ActionContext;
import com.facilio.bmsconsole.workflow.rule.ActionType;
import com.facilio.bmsconsole.workflow.rule.WorkflowRuleContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.fields.FacilioField;

public class AddAttachmentTemplateRelation extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		List<ActionContext> actions = (List<ActionContext>) context.get(FacilioConstants.ContextNames.WORKFLOW_ACTION_LIST);
		WorkflowRuleContext rule = (WorkflowRuleContext) context.get(FacilioConstants.ContextNames.WORKFLOW_RULE);
		
		if (actions == null || actions.isEmpty()) {
			actions = rule.getActions();
		}
		
		if (actions != null && !actions.isEmpty()) {
			for (ActionContext action : actions) {					
					if (action.getActionType() == ActionType.BULK_EMAIL_NOTIFICATION.getVal() && action.getTemplateId() > 0) {
						TemplateAPI.addAttachment(action.getTemplate());
					}
			}		
		}
		
		return false;
	}

	

}
