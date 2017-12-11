package com.facilio.bmsconsole.commands;

import java.util.List;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.TaskContext;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.bmsconsole.modules.InsertRecordBuilder;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;

public class AddTasksCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		List<TaskContext> tasks = (List<TaskContext>) context.get(FacilioConstants.ContextNames.TASK_LIST);
		if(tasks != null && !tasks.isEmpty()) {
			String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			FacilioModule module = modBean.getModule(moduleName);
			List<FacilioField> fields = (List<FacilioField>) context.get(FacilioConstants.ContextNames.EXISTING_FIELD_LIST);
			
			for(TaskContext task : tasks) {
				task.setCreatedTime(System.currentTimeMillis());
				
				if(task.getAsset() == null && task.isReadingTask()) {
					throw new IllegalArgumentException("Asset cannot be null when reading is enabled for task");
				}
				
				if(task.isReadingTask() && task.getReadingFieldId() == -1) {
					throw new IllegalArgumentException("Reading ID cannot be null when reading is enabled for task");
				}
			}
			
			InsertRecordBuilder<TaskContext> builder = new InsertRecordBuilder<TaskContext>()
															.module(module)
															.fields(fields)
															.addRecords(tasks);
															;
			
			builder.save();
		}
		else {
//			throw new IllegalArgumentException("Task list cannot be null/ empty");
		}
		return false;
	}

}
