package com.facilio.bmsconsole.commands;

import com.facilio.bmsconsole.context.TaskContext;
import com.facilio.modules.FacilioField;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.bmsconsole.util.TicketAPI;
import com.facilio.constants.FacilioConstants;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import java.util.List;
import java.util.Map;

public class GetTasksOfTicketCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		
		long ticketId = (long) context.get(FacilioConstants.ContextNames.ID);
		boolean isAsMap = false;
		if(context.get("isAsMap") != null) {
			isAsMap = (boolean) context.get("isAsMap");
		}
		if(ticketId > 0) {
			
			String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
			String dataTableName = (String) context.get(FacilioConstants.ContextNames.MODULE_DATA_TABLE_NAME);
			List<FacilioField> fields = (List<FacilioField>) context.get(FacilioConstants.ContextNames.EXISTING_FIELD_LIST);
			
			SelectRecordsBuilder<TaskContext> builder = new SelectRecordsBuilder<TaskContext>()
					.table(dataTableName)
					.moduleName(moduleName)
					.beanClass(TaskContext.class)
					.select(fields)
					.andCustomWhere("PARENT_TICKET_ID = ?", ticketId)
					.orderBy("id");

			
			if(isAsMap) {
				List<Map<String, Object>> tasks = builder.getAsProps();
				context.put(FacilioConstants.ContextNames.TASK_MAP, tasks);
			}
			else {
				List<TaskContext> tasks = builder.get();
				context.put(FacilioConstants.ContextNames.TASK_MAP, TicketAPI.groupTaskBySection(tasks));
			}
		}
		else {
			throw new IllegalArgumentException("Invalid ticket ID : "+ticketId);
		}
		
		return false;
	}

}
