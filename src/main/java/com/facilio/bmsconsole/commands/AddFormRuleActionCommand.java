package com.facilio.bmsconsole.commands;

import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.forms.FormActionType;
import com.facilio.bmsconsole.forms.FormRuleActionContext;
import com.facilio.bmsconsole.forms.FormRuleContext;
import com.facilio.bmsconsole.util.FormRuleAPI;
import com.facilio.workflows.util.WorkflowUtil;

public class AddFormRuleActionCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		
		FormRuleContext formRule = (FormRuleContext)context.get(FormRuleAPI.FORM_RULE_CONTEXT);
		
		for(FormRuleActionContext action :formRule.getActions()) {
			
			action.setFormRuleId(formRule.getId());

			if (action.getActionTypeEnum() == FormActionType.EXECUTE_SCRIPT) {
				Long workflowId = action.getWorkflow().getId();
				if(workflowId > 0){
					WorkflowUtil.updateWorkflow(action.getWorkflow(), workflowId);
				}else{
					workflowId = WorkflowUtil.addWorkflow(action.getWorkflow());
					action.setWorkflowId(workflowId);
					FormRuleAPI.addFormRuleActionContext(action);
				}
			}else {
				FormRuleAPI.addFormRuleActionContext(action);
				FormRuleAPI.addFormRuleActionFieldsContext(action);
			}
			
		}
		return false;
	}

}
