package com.facilio.bmsconsole.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.LabourContext;
import com.facilio.bmsconsole.context.WorkOrderLabourContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.PickListOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.fields.FacilioField;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import java.util.List;
import java.util.Map;

public class GetWorkOrderLabourListCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
		if (moduleName != null && !moduleName.isEmpty()) {
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			FacilioModule workOrderLabour = modBean.getModule(moduleName);
			List<FacilioField> workorderLabourFields = modBean.getAllFields(moduleName);
			Map<String, FacilioField> workorderLabourFieldMap = FieldFactory.getAsMap(workorderLabourFields);
			long parentId = (long) context.get(FacilioConstants.ContextNames.PARENT_ID);
			SelectRecordsBuilder<WorkOrderLabourContext> selectBuilder = new SelectRecordsBuilder<WorkOrderLabourContext>()
					.select(workorderLabourFields).table(workOrderLabour.getTableName())
					.moduleName(workOrderLabour.getName()).beanClass(WorkOrderLabourContext.class)
					.andCondition(CriteriaAPI.getCondition(workorderLabourFieldMap.get("parentId"),
							String.valueOf(parentId), PickListOperators.IS));

			List<WorkOrderLabourContext> workorderLabourRecords = selectBuilder.get();
			if (workorderLabourRecords != null && !workorderLabourRecords.isEmpty()) {
				for (WorkOrderLabourContext woLabour : workorderLabourRecords) {
					LabourContext labour = getLabour(woLabour.getLabour().getId());
					woLabour.setLabour(labour);
				}
			}
			context.put(FacilioConstants.ContextNames.WO_LABOUR, workorderLabourRecords);
		}
		return false;
	}
	
	public static LabourContext getLabour(long id) throws Exception
	{
		if(id <= 0) {
			return null;
		}
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.LABOUR);
		List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ContextNames.LABOUR);
		SelectRecordsBuilder<LabourContext> selectBuilder = new SelectRecordsBuilder<LabourContext>()
				.select(fields)
				.table(module.getTableName())
				.moduleName(module.getName())
				.beanClass(LabourContext.class)
				.andCondition(CriteriaAPI.getIdCondition(id, module));
		List<LabourContext> labourRecords =  selectBuilder.get();
		if(labourRecords!=null &&!labourRecords.isEmpty()) {
			return labourRecords.get(0);
		}
		return null;
	}
}

