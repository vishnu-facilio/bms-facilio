package com.facilio.bmsconsole.commands;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.util.ActionAPI;
import com.facilio.bmsconsole.workflow.rule.ActionContext;
import com.facilio.constants.FacilioConstants;

public class UpdateWorkFlowRuleAction implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		ActionContext action = (ActionContext) context.get(FacilioConstants.ContextNames.WORKFLOW_ACTION_LIST);
		
		if (action != null) {
			
			ActionAPI.updateAction(AccountUtil.getCurrentOrg().getId(), action, action.getId());
		}
		else {
			throw new IllegalArgumentException("Action Object cannot be null");
		}
		// TODO Auto-generated method stub
		return false;
	}

}
