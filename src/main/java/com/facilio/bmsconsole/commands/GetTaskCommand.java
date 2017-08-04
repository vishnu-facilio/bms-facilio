package com.facilio.bmsconsole.commands;

import java.sql.Connection;
import java.util.List;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.context.TaskContext;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.SelectRecordsBuilder;
import com.facilio.constants.FacilioConstants;

public class GetTaskCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		
		long taskId = (long) context.get(FacilioConstants.ContextNames.ID);
		
		if(taskId > 0) {
			String dataTableName = (String) context.get(FacilioConstants.ContextNames.MODULE_DATA_TABLE_NAME);
			List<FacilioField> fields = (List<FacilioField>) context.get(FacilioConstants.ContextNames.EXISTING_FIELD_LIST);
			Connection conn = ((FacilioContext) context).getConnectionWithoutTransaction();
			
			SelectRecordsBuilder<TaskContext> builder = new SelectRecordsBuilder<TaskContext>()
					.connection(conn)
					.dataTableName(dataTableName)
					.beanClass(TaskContext.class)
					.select(fields)
					.where("ID = ?", taskId)
					.orderBy("ID");

			List<TaskContext> tasks = builder.getAsBean();	
			if(tasks.size() > 0) {
				TaskContext task = tasks.get(0);
				context.put(FacilioConstants.ContextNames.TASK, task);
			}
;		}
		else {
			throw new IllegalArgumentException("Invalid Task ID : "+taskId);
		}
		
		return false;
	}

}
