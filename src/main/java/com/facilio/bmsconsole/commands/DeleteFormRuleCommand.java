package com.facilio.bmsconsole.commands;

import com.facilio.bmsconsole.forms.FormActionType;
import com.facilio.bmsconsole.forms.FormRuleActionContext;
import com.facilio.command.FacilioCommand;
import com.facilio.workflows.util.WorkflowUtil;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.forms.FormRuleContext;
import com.facilio.bmsconsole.util.FormRuleAPI;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;

public class DeleteFormRuleCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		
		FormRuleContext formRule = (FormRuleContext)context.get(FormRuleAPI.FORM_RULE_CONTEXT);
		List<FormRuleActionContext> actions = FormRuleAPI.getFormRuleActionContext(formRule.getId());

		if(CollectionUtils.isNotEmpty(actions)){
			for(FormRuleActionContext action :actions) {
				if(action.getActionTypeEnum() == FormActionType.EXECUTE_SCRIPT) {
					long oldWorkflowId = action.getWorkflowId();
					WorkflowUtil.deleteWorkflow(oldWorkflowId);
				}
			}
		}

		    if(formRule.getIsDefault()){
			throw new IllegalArgumentException("Default form rule cannot be deleted");
		}

		FormRuleAPI.deleteFormRuleContext(formRule);
		return false;
	}

}
