package com.facilio.bmsconsole.commands;

import java.sql.Connection;
import java.util.List;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.commons.lang3.StringUtils;

import com.facilio.bmsconsole.context.WorkOrderRequestContext;
import com.facilio.bmsconsole.criteria.Condition;
import com.facilio.bmsconsole.criteria.NumberOperators;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FieldFactory;
import com.facilio.bmsconsole.modules.UpdateRecordBuilder;
import com.facilio.constants.FacilioConstants;

public class UpdateWorkOrderRequestCommand implements Command {
	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		WorkOrderRequestContext workOrderRequest = (WorkOrderRequestContext) context.get(FacilioConstants.ContextNames.WORK_ORDER_REQUEST);
		List<Long> recordIds = (List<Long>) context.get(FacilioConstants.ContextNames.RECORD_ID_LIST);
		if(workOrderRequest != null && recordIds != null && !recordIds.isEmpty()) {
			String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
			String dataTableName = (String) context.get(FacilioConstants.ContextNames.MODULE_DATA_TABLE_NAME);
			List<FacilioField> fields = (List<FacilioField>) context.get(FacilioConstants.ContextNames.EXISTING_FIELD_LIST);
			Connection conn = ((FacilioContext) context).getConnectionWithTransaction();
			
			String ids = StringUtils.join(recordIds, ",");
			Condition idCondition = new Condition();
			idCondition.setField(FieldFactory.getIdField(dataTableName));
			idCondition.setOperator(NumberOperators.EQUALS);
			idCondition.setValue(ids);
			
			UpdateRecordBuilder<WorkOrderRequestContext> updateBuilder = new UpdateRecordBuilder<WorkOrderRequestContext>()
																		.connection(conn)
																		.moduleName(moduleName)
																		.table(dataTableName)
																		.fields(fields)
																		.andCondition(idCondition);
			context.put(FacilioConstants.ContextNames.ROWS_UPDATED, updateBuilder.update(workOrderRequest));
		}
		return false;
	}
}
