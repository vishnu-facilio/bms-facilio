package com.facilio.bmsconsole.commands;

import com.facilio.command.FacilioCommand;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.fields.FacilioField;
import org.apache.commons.chain.Context;

import com.facilio.beans.ModuleBean;
import com.facilio.beans.ModuleCRUDBean;
import com.facilio.bmsconsole.context.FormulaFieldContext;
import com.facilio.bmsconsole.util.FormulaFieldAPI;
import com.facilio.bmsconsole.util.WorkflowRuleAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericDeleteRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.ModuleFactory;
import com.facilio.workflows.util.WorkflowUtil;

import java.util.List;
import java.util.Map;

public class DeleteFormulaCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		long id = (long) context.get(FacilioConstants.ContextNames.RECORD_ID);
		
		if ( id != -1) {
			FormulaFieldContext oldFormula = FormulaFieldAPI.getFormulaField(id);
			
			ModuleCRUDBean crudBean = (ModuleCRUDBean) BeanFactory.lookup("ModuleCRUD");
			crudBean.deleteAllData(oldFormula.getReadingField().getModule().getName());
			FacilioModule module = ModuleFactory.getFormulaFieldModule();
			GenericDeleteRecordBuilder deleteBuilder = new GenericDeleteRecordBuilder()
															.table(module.getTableName())
//															.andCondition(CriteriaAPI.getCurrentOrgIdCondition(module))
															.andCondition(CriteriaAPI.getIdCondition(id, module));
			
			deleteBuilder.delete();
			List<FacilioField> fieldList= FieldFactory.getReadingDataMetaFields();
			Map<String,FacilioField> fieldMap= FieldFactory.getAsMap(fieldList);
			deleteBuilder = new GenericDeleteRecordBuilder().table(ModuleFactory.getReadingDataMetaModule().getTableName())
					.andCondition(CriteriaAPI.getCondition(fieldMap.get("fieldId"),""+oldFormula.getReadingFieldId(),NumberOperators.EQUALS));
			deleteBuilder.delete();

			WorkflowUtil.deleteWorkflow(oldFormula.getWorkflowId());
			if (oldFormula.getViolationRuleId() > 0) {
				WorkflowRuleAPI.deleteWorkflowRule(oldFormula.getViolationRuleId());
			}
			
			Boolean isFormulaFieldOppFromMandV = (Boolean) context.get(FacilioConstants.ContextNames.IS_FORMULA_FIELD_OPERATION_FROM_M_AND_V);
			
			if(isFormulaFieldOppFromMandV == null || !isFormulaFieldOppFromMandV) {
				ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
				modBean.deleteModule(oldFormula.getReadingField().getModule().getName());
			}
			
			context.put(FacilioConstants.ContextNames.RESULT, "success");
		}
		else {
			throw new IllegalArgumentException("Formula ID cannot be null during deletion");
		}
		
		return false;
	}

}
