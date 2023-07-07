package com.facilio.bmsconsole.commands;

import java.util.List;
import java.util.Map;

import com.facilio.command.FacilioCommand;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
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

		GenericSelectRecordBuilder selectRecordBuilder = new GenericSelectRecordBuilder()
				.table(ModuleFactory.getFormRuleModule().getTableName())
				.select(FieldFactory.getFormRuleFields())
				.andCondition(CriteriaAPI.getCondition("ID","id",String.valueOf(formRule.getId()), NumberOperators.EQUALS));


		FormRuleContext formRuleContext = FieldUtil.getAsBeanFromMap(selectRecordBuilder.fetchFirst(),FormRuleContext.class);

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
