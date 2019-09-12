package com.facilio.bmsconsole.commands;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Context;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.WorkOrderContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.DateOperators;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.fields.FacilioField;

public class PreventiveMaintenanceReadingsCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		long pmId = (Long) context.get(FacilioConstants.ContextNames.RECORD_ID);
		long resourceId = (Long) context.get(FacilioConstants.ContextNames.RESOURCE_ID);
		long startTime = (Long) context.get(FacilioConstants.ContextNames.START_TIME);
		long endTime = (Long) context.get(FacilioConstants.ContextNames.END_TIME);
		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule workorderModule = modBean.getModule("workorder");
		FacilioModule taskModule = modBean.getModule("task");
		
		List<FacilioField> workorderFields = modBean.getAllFields("workorder"); 
		List<FacilioField> taskFields = modBean.getAllFields("task");
		
		List<FacilioField> selectFields = new ArrayList<FacilioField>(workorderFields);
		selectFields.addAll(taskFields);
		
		Map<String, FacilioField> taskFieldMap = FieldFactory.getAsMap(taskFields);
		Map<String, FacilioField> workorderFieldMap = FieldFactory.getAsMap(workorderFields);
		
		SelectRecordsBuilder<WorkOrderContext> selectBuilder = new SelectRecordsBuilder<WorkOrderContext>();
				selectBuilder.module(workorderModule)
				.select(selectFields)
				.innerJoin(taskModule.getTableName())
				.on(workorderModule.getTableName() + ".ID = " + taskModule.getTableName() + ".PARENT_TICKET_ID")
				.andCondition(CriteriaAPI.getCondition(workorderFieldMap.get("createdTime"),startTime + "," + endTime, DateOperators.BETWEEN))
				.andCondition(CriteriaAPI.getCondition(workorderFieldMap.get("resource"), resourceId + "" , NumberOperators.EQUALS))
				.andCondition(CriteriaAPI.getCondition(workorderFieldMap.get("pm"), pmId + "" , NumberOperators.EQUALS));
				
		//workorderFieldMap.get("resource")
				selectBuilder.getAsProps();
				

		return false;
	}
	
}