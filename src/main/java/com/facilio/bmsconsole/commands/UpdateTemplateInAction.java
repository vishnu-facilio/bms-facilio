package com.facilio.bmsconsole.commands;

import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;
import org.json.simple.JSONObject;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.templates.Template;
import com.facilio.bmsconsole.util.ActionAPI;
import com.facilio.bmsconsole.workflow.rule.ActionContext;
import com.facilio.constants.FacilioConstants;

public class UpdateTemplateInAction extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		JSONObject templateContent = (JSONObject) context.get(FacilioConstants.Workflow.ACTION_TEMPLATE);
		Template template = (Template) context.get(FacilioConstants.Workflow.TEMPLATE);
		if(templateContent != null && !templateContent.isEmpty() && template != null) {

			ActionContext actionContext = new ActionContext();
			actionContext.setTemplateId(template.getId());
			ActionAPI.updateAction(AccountUtil.getCurrentOrg().getId(), actionContext, (long) templateContent.get("actionId"));
		}
			// TODO Auto-generated method stub
			return false;
	}

}
