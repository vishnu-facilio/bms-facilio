package com.facilio.bmsconsole.commands;

import java.util.List;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.context.TaskContext;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.InsertRecordBuilder;
import com.facilio.constants.FacilioConstants;

public class AddTaskCommand implements Command {
	
	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		TaskContext task = (TaskContext) context.get(FacilioConstants.ContextNames.TASK);
		if(task != null) {
			String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
			String dataTableName = (String) context.get(FacilioConstants.ContextNames.MODULE_DATA_TABLE_NAME);
			List<FacilioField> fields = (List<FacilioField>) context.get(FacilioConstants.ContextNames.EXISTING_FIELD_LIST);
			task.setCreatedTime(System.currentTimeMillis());
			
			if(task.getAsset() == null && task.isReadingTask()) {
				throw new IllegalArgumentException("Asset cannot be null when reading is enabled for task");
			}
			
			if(task.isReadingTask() && task.getReadingId() == -1) {
				throw new IllegalArgumentException("Reading ID cannot be null when reading is enabled for task");
			}
			
			InsertRecordBuilder<TaskContext> builder = new InsertRecordBuilder<TaskContext>()
															.moduleName(moduleName)
															.table(dataTableName)
															.fields(fields);
			long taskId = builder.insert(task);
			task.setId(taskId);
			context.put(FacilioConstants.ContextNames.RECORD_ID, taskId);
		}
		else {
			throw new IllegalArgumentException("Task Object cannot be null");
		}
		
		return false;
	}
}
