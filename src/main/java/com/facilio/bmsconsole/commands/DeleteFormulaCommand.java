package com.facilio.bmsconsole.commands;

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
			
			WorkflowUtil.deleteWorkflow(oldFormula.getWorkflowId());
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			modBean.deleteModule(oldFormula.getReadingField().getModule().getName());
			if (oldFormula.getViolationRuleId() > 0) {
				WorkflowRuleAPI.deleteWorkflowRule(oldFormula.getViolationRuleId());
			}
			
			context.put(FacilioConstants.ContextNames.RESULT, "success");
		}
		else {
			throw new IllegalArgumentException("Formula ID cannot be null during deletion");
		}
		
		return false;
	}

}
