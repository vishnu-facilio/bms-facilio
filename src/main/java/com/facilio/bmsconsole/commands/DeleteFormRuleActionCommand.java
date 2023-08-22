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
import org.apache.commons.collections4.CollectionUtils;

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
		List<FormRuleActionContext> newActions=formRule.getActions();
		boolean isScriptAvailable = true;
		if(CollectionUtils.isNotEmpty(newActions)){
			isScriptAvailable = newActions.stream().anyMatch(formRuleActionContext -> formRuleActionContext.getActionTypeEnum()==FormActionType.EXECUTE_SCRIPT);
		}

		for(FormRuleActionContext action :oldactions) {
			if(action.getActionTypeEnum() != FormActionType.EXECUTE_SCRIPT) {
				for(FormRuleActionFieldsContext actionField : action.getFormRuleActionFieldsContext()) {
					if(actionField.getCriteriaId() > 0) {
						CriteriaAPI.deleteCriteria(actionField.getCriteriaId());
					}
				}
				FormRuleAPI.deleteFormRuleActionContext(action);
			}
			else if(!isScriptAvailable){
				long workflowId = action.getWorkflowId();
				WorkflowUtil.deleteWorkflow(workflowId);
				FormRuleAPI.deleteFormRuleActionContext(action);
			}
		}
		return false;
	}

}
