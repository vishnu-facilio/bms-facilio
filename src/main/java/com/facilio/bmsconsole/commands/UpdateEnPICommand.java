package com.facilio.bmsconsole.commands;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.beans.ModuleBean;
import com.facilio.beans.ModuleCRUDBean;
import com.facilio.bmsconsole.context.EnergyPerformanceIndicatorContext;
import com.facilio.bmsconsole.criteria.CriteriaAPI;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.bmsconsole.modules.FieldFactory;
import com.facilio.bmsconsole.modules.FieldUtil;
import com.facilio.bmsconsole.modules.ModuleFactory;
import com.facilio.bmsconsole.util.EnergyPerformanceIndicatiorAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.sql.GenericUpdateRecordBuilder;
import com.facilio.tasker.FacilioTimer;
import com.facilio.workflows.util.WorkflowUtil;

public class UpdateEnPICommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		EnergyPerformanceIndicatorContext newEnPI = (EnergyPerformanceIndicatorContext) context.get(FacilioConstants.ContextNames.ENPI);
		if (newEnPI != null) {
			EnergyPerformanceIndicatorContext oldEnPI = EnergyPerformanceIndicatiorAPI.getENPI(newEnPI.getId());
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
			
			newEnPI.setSpaceId(-1);
			newEnPI.setFrequency(null);
			newEnPI.setSchedule(null);
			
			FacilioModule module = ModuleFactory.getEnPIModule();
			GenericUpdateRecordBuilder updateBuilder = new GenericUpdateRecordBuilder()
															.table(module.getTableName())
															.fields(FieldFactory.getEnPIFields())
															.andCondition(CriteriaAPI.getCurrentOrgIdCondition(module))
															.andCondition(CriteriaAPI.getIdCondition(newEnPI.getId(), module));
			updateBuilder.update(FieldUtil.getAsProperties(newEnPI));
			
			if (newEnPI.getWorkflow() != null) {
				WorkflowUtil.deleteWorkflow(oldEnPI.getWorkflowId());
				ModuleCRUDBean crudBean = (ModuleCRUDBean) BeanFactory.lookup("ModuleCRUD");
				crudBean.deleteAllData(oldEnPI.getReadingField().getModule().getName());
				
				FacilioTimer.deleteJob(newEnPI.getId(), "HistoricalENPICalculator");
				FacilioTimer.scheduleOneTimeJob(newEnPI.getId(), "HistoricalENPICalculator", 30, "priority");
			}
			context.put(FacilioConstants.ContextNames.RESULT, "success");
		}
		else {
			throw new IllegalArgumentException("EnPI cannot be null during updation");
		}
		return false;
	}

}
