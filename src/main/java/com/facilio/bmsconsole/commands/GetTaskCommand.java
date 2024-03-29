package com.facilio.bmsconsole.commands;

import java.util.List;

import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.context.TaskContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.fields.FacilioField;

public class GetTaskCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		
		long taskId = (long) context.get(FacilioConstants.ContextNames.ID);
		
		if(taskId > 0) {
			String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
			List<FacilioField> fields = (List<FacilioField>) context.get(FacilioConstants.ContextNames.EXISTING_FIELD_LIST);
			
			SelectRecordsBuilder<TaskContext> builder = new SelectRecordsBuilder<TaskContext>()
					.moduleName(moduleName)
					.beanClass(TaskContext.class)
					.select(fields)
					.andCustomWhere("Tasks.ID = ?", taskId)
					.orderBy("ID");

			List<TaskContext> tasks = builder.get();	
			if(tasks.size() > 0) {
				CommonCommandUtil.loadTaskLookups(tasks);
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