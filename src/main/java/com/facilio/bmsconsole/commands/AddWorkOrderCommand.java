package com.facilio.bmsconsole.commands;

import java.util.Collections;
import java.util.List;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.commons.lang3.StringUtils;

import com.facilio.bmsconsole.context.WorkOrderContext;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.InsertRecordBuilder;
import com.facilio.bmsconsole.util.TicketAPI;
import com.facilio.bmsconsole.workflow.ActivityType;
import com.facilio.constants.FacilioConstants;

public class AddWorkOrderCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		WorkOrderContext workOrder = (WorkOrderContext) context.get(FacilioConstants.ContextNames.WORK_ORDER);
		if(workOrder != null) {
			if(workOrder.getRequester() == null || StringUtils.isBlank(workOrder.getRequester().getEmail()))
			{
				workOrder.setRequester(null);
			}
			
			String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
			String dataTableName = (String) context.get(FacilioConstants.ContextNames.MODULE_DATA_TABLE_NAME);
			List<FacilioField> fields = (List<FacilioField>) context.get(FacilioConstants.ContextNames.EXISTING_FIELD_LIST);
			workOrder.setCreatedTime(System.currentTimeMillis());
			
			if(workOrder.getDuration() != -1) {
				workOrder.setDueDate(workOrder.getCreatedTime()+(workOrder.getDuration()*1000));
			}
			
			TicketAPI.updateTicketStatus(workOrder);
			
			InsertRecordBuilder<WorkOrderContext> builder = new InsertRecordBuilder<WorkOrderContext>()
																.moduleName(moduleName)
																.table(dataTableName)
																.fields(fields);
			
			Integer insertLevel = (Integer) context.get(FacilioConstants.ContextNames.INSERT_LEVEL);
			if(insertLevel != null) {
				builder.level(insertLevel);
			}
			
			long workOrderId = builder.insert(workOrder);
			workOrder.setId(workOrderId);
			context.put(FacilioConstants.ContextNames.ACTIVITY_TYPE, ActivityType.CREATE);
			context.put(FacilioConstants.ContextNames.RECORD, workOrder);
			context.put(FacilioConstants.ContextNames.RECORD_ID_LIST, Collections.singletonList(workOrderId));
			
		}
		else {
			throw new IllegalArgumentException("WorkOrder Object cannot be null");
		}
		return false;
	}
}
