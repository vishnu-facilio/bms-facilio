package com.facilio.bmsconsole.commands;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.FormulaFieldContext;
import com.facilio.bmsconsole.criteria.CriteriaAPI;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.bmsconsole.modules.FieldFactory;
import com.facilio.bmsconsole.modules.FieldUtil;
import com.facilio.bmsconsole.modules.ModuleFactory;
import com.facilio.bmsconsole.util.FormulaFieldAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.sql.GenericUpdateRecordBuilder;
import com.facilio.workflows.util.WorkflowUtil;

public class UpdateEnPICommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		FormulaFieldContext newEnPI = (FormulaFieldContext) context.get(FacilioConstants.ContextNames.FORMULA_FIELD);
		if (newEnPI != null) {
			FormulaFieldContext oldEnPI = FormulaFieldAPI.getFormulaField(newEnPI.getId());
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			if (newEnPI.getName() != null && !newEnPI.getName().isEmpty()) {
				FacilioField field = new FacilioField();
				field.setDisplayName(newEnPI.getName());
				field.setId(oldEnPI.getReadingFieldId());
				modBean.updateField(field);
			}
			
			if (newEnPI.getWorkflow() != null) {
				long workflowId = WorkflowUtil.addWorkflow(newEnPI.getWorkflow());
				newEnPI.setWorkflowId(workflowId);
			}
			
			newEnPI.setResourceId(-1);
			newEnPI.setAssetCategoryId(-1);
			newEnPI.setSpaceCategoryId(-1);
			newEnPI.setIncludedResources(null);
			newEnPI.setFrequency(null);
			
			FacilioModule module = ModuleFactory.getFormulaFieldModule();
			GenericUpdateRecordBuilder updateBuilder = new GenericUpdateRecordBuilder()
															.table(module.getTableName())
															.fields(FieldFactory.getFormulaFieldFields())
															.andCondition(CriteriaAPI.getCurrentOrgIdCondition(module))
															.andCondition(CriteriaAPI.getIdCondition(newEnPI.getId(), module));
			updateBuilder.update(FieldUtil.getAsProperties(newEnPI));
			
			if (newEnPI.getWorkflow() != null) {
				WorkflowUtil.deleteWorkflow(oldEnPI.getWorkflowId());
				FormulaFieldAPI.recalculateHistoricalData(newEnPI, oldEnPI.getReadingField());
			}
			context.put(FacilioConstants.ContextNames.RESULT, "success");
		}
		else {
			throw new IllegalArgumentException("EnPI cannot be null during updation");
		}
		return false;
	}

}
