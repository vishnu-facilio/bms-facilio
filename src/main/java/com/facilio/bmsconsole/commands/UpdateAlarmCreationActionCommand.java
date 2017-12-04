package com.facilio.bmsconsole.commands;

import java.util.List;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.util.ActionAPI;
import com.facilio.bmsconsole.workflow.ActionContext;
import com.facilio.bmsconsole.workflow.UserTemplate;
import com.facilio.constants.FacilioConstants;

public class UpdateAlarmCreationActionCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		UserTemplate template = (UserTemplate) context.get(FacilioConstants.Workflow.TEMPLATE);
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
