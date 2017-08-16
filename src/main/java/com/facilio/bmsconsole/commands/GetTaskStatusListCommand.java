package com.facilio.bmsconsole.commands;

import java.sql.Connection;
import java.util.List;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.context.TaskStatusContext;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.SelectRecordsBuilder;
import com.facilio.constants.FacilioConstants;

public class GetTaskStatusListCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		
		String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
		String dataTableName = (String) context.get(FacilioConstants.ContextNames.MODULE_DATA_TABLE_NAME);
		List<FacilioField> fields = (List<FacilioField>) context.get(FacilioConstants.ContextNames.EXISTING_FIELD_LIST);
		Connection conn = ((FacilioContext) context).getConnectionWithoutTransaction();
		
		SelectRecordsBuilder<TaskStatusContext> builder = new SelectRecordsBuilder<TaskStatusContext>()
														.connection(conn)
														.dataTableName(dataTableName)
														.moduleName(moduleName)
														.beanClass(TaskStatusContext.class)
														.select(fields)
														.orderBy("ID");
		List<TaskStatusContext> statuses = builder.getAsBean();
		context.put(FacilioConstants.ContextNames.TASK_STATUS_LIST, statuses);
		
		return false;
	}

}
