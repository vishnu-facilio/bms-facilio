package com.facilio.bmsconsole.commands;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.beans.ModuleBean;
import com.facilio.beans.ModuleCRUDBean;
import com.facilio.bmsconsole.context.FormulaFieldContext;
import com.facilio.bmsconsole.criteria.CriteriaAPI;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.bmsconsole.modules.ModuleFactory;
import com.facilio.bmsconsole.util.FormulaFieldAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.sql.GenericDeleteRecordBuilder;
import com.facilio.workflows.util.WorkflowUtil;

public class DeleteEnPICommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		long id = (long) context.get(FacilioConstants.ContextNames.RECORD_ID);
		
		if ( id != -1) {
			FormulaFieldContext oldEnPI = FormulaFieldAPI.getENPI(id);
			
			ModuleCRUDBean crudBean = (ModuleCRUDBean) BeanFactory.lookup("ModuleCRUD");
			crudBean.deleteAllData(oldEnPI.getReadingField().getModule().getName());
			FacilioModule module = ModuleFactory.getFormulaFieldModule();
			GenericDeleteRecordBuilder deleteBuilder = new GenericDeleteRecordBuilder()
															.table(module.getTableName())
															.andCondition(CriteriaAPI.getCurrentOrgIdCondition(module))
															.andCondition(CriteriaAPI.getIdCondition(id, module));
			
			deleteBuilder.delete();
			
			WorkflowUtil.deleteWorkflow(oldEnPI.getWorkflowId());
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			modBean.deleteModule(oldEnPI.getReadingField().getModule().getName());
			
			context.put(FacilioConstants.ContextNames.RESULT, "success");
		}
		else {
			throw new IllegalArgumentException("EnPI ID cannot be null");
		}
		
		return false;
	}

}
