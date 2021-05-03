package com.facilio.bmsconsole.commands;

import java.util.List;

import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.forms.FormActionType;
import com.facilio.bmsconsole.forms.FormRuleActionContext;
import com.facilio.bmsconsole.forms.FormRuleActionFieldsContext;
import com.facilio.bmsconsole.forms.FormRuleContext;
import com.facilio.bmsconsole.util.FormRuleAPI;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.workflows.util.WorkflowUtil;

public class DeleteFormRuleActionCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		
		FormRuleContext formRule = (FormRuleContext)context.get(FormRuleAPI.FORM_RULE_CONTEXT);
		
		List<FormRuleActionContext> oldactions = FormRuleAPI.getFormRuleActionContext(formRule.getId());
		
		for(FormRuleActionContext action :oldactions) {
			if(action.getActionTypeEnum() == FormActionType.EXECUTE_SCRIPT) {
				long oldWorkflowId = action.getWorkflowId();
				WorkflowUtil.deleteWorkflow(oldWorkflowId);
			}
			else {
				for(FormRuleActionFieldsContext actionField : action.getFormRuleActionFieldsContext()) {
					
					if(actionField.getCriteriaId() > 0) {
						CriteriaAPI.deleteCriteria(actionField.getCriteriaId());
					}
				}
			}
			
			FormRuleAPI.deleteFormRuleActionContext(action);
		}
		return false;
	}

}
