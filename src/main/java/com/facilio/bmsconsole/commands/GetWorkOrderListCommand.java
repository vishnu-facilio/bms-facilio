package com.facilio.bmsconsole.commands;

import java.sql.Connection;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.context.WorkOrderContext;
import com.facilio.bmsconsole.criteria.Condition;
import com.facilio.bmsconsole.criteria.Criteria;
import com.facilio.bmsconsole.criteria.DateOperators;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FieldType;
import com.facilio.bmsconsole.modules.SelectRecordsBuilder;
import com.facilio.bmsconsole.view.FacilioView;
import com.facilio.constants.FacilioConstants;

public class GetWorkOrderListCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
		String dataTableName = (String) context.get(FacilioConstants.ContextNames.MODULE_DATA_TABLE_NAME);
		List<FacilioField> fields = (List<FacilioField>) context.get(FacilioConstants.ContextNames.EXISTING_FIELD_LIST);
		Connection conn = ((FacilioContext) context).getConnectionWithoutTransaction();
		FacilioView view = (FacilioView) context.get(FacilioConstants.ContextNames.CUSTOM_VIEW);
		
		SelectRecordsBuilder<WorkOrderContext> builder = new SelectRecordsBuilder<WorkOrderContext>()
														.connection(conn)
														.table(dataTableName)
														.moduleName(moduleName)
														.beanClass(WorkOrderContext.class)
														.select(fields)
														.orderBy("ID");

		if(view != null) {
			Criteria criteria = view.getCriteria();
			builder.andCriteria(criteria);
		}
		
		Map<String, Object> filters = (Map<String, Object>) context.get(FacilioConstants.ContextNames.FILTERS);
		if(filters != null && !filters.isEmpty())
		{	
			FacilioField dueField = new FacilioField();
			dueField.setName("createdDate");
			dueField.setColumnName("CREATED_DATE");
			dueField.setDataType(FieldType.DATE_TIME);
			dueField.setModuleTableName("Tickets");
			
			Condition overdue = new Condition();
			overdue.setField(dueField);
			overdue.setOperator(DateOperators.TODAY);
			builder.andCondition(overdue);
		}
		
		List<WorkOrderContext> workOrders = builder.get();
		context.put(FacilioConstants.ContextNames.WORK_ORDER_LIST, workOrders);
		
		return false;
	}

}
