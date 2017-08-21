package com.facilio.bmsconsole.commands;

import java.sql.Connection;
import java.util.List;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.context.TaskContext;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.SelectRecordsBuilder;
import com.facilio.constants.FacilioConstants;

public class GetTasksOfTicketCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		
		long ticketId = (long) context.get(FacilioConstants.ContextNames.ID);
		if(ticketId > 0) {
			
			String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
			String dataTableName = (String) context.get(FacilioConstants.ContextNames.MODULE_DATA_TABLE_NAME);
			List<FacilioField> fields = (List<FacilioField>) context.get(FacilioConstants.ContextNames.EXISTING_FIELD_LIST);
			Connection conn = ((FacilioContext) context).getConnectionWithoutTransaction();
			
			SelectRecordsBuilder<TaskContext> builder = new SelectRecordsBuilder<TaskContext>()
					.connection(conn)
					.table(dataTableName)
					.moduleName(moduleName)
					.beanClass(TaskContext.class)
					.select(fields)
					.andCustomWhere("parent = ?", ticketId)
					.orderBy("id");

			List<TaskContext> tasks = builder.get();
			context.put(FacilioConstants.ContextNames.TASK_LIST, tasks);
		}
		else {
			throw new IllegalArgumentException("Invalid ticket ID : "+ticketId);
		}
		
		return false;
	}

}
