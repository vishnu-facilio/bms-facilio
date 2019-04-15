package com.facilio.bmsconsole.commands;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.templates.Template;
import com.facilio.bmsconsole.util.ActionAPI;
import com.facilio.bmsconsole.workflow.rule.ActionContext;
import com.facilio.constants.FacilioConstants;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import java.util.List;

public class UpdateAlarmCreationActionCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		Template template = (Template) context.get(FacilioConstants.Workflow.TEMPLATE);
		if(template != null) {
			long orgId = AccountUtil.getCurrentOrg().getOrgId();
			List<ActionContext> alarmEmailActions = ActionAPI.getActionsFromWorkflowRuleName(orgId, (String) context.get(FacilioConstants.Workflow.WORKFLOW));
			
			if(alarmEmailActions != null && !alarmEmailActions.isEmpty()) {
				ActionContext alarmEmailAction = alarmEmailActions.get(0);
				long actionId = alarmEmailAction.getId();
				
				alarmEmailAction = new ActionContext();
				alarmEmailAction.setTemplateId(template.getId());
				
				ActionAPI.updateAction(orgId, alarmEmailAction, actionId);
				context.put(FacilioConstants.ContextNames.RESULT, true);
			}	
		}
		return false;
	}

}
