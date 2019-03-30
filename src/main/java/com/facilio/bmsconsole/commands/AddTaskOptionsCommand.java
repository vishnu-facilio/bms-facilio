package com.facilio.bmsconsole.commands;

import com.facilio.bmsconsole.context.TaskContext;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.bmsconsole.modules.FieldFactory;
import com.facilio.bmsconsole.modules.ModuleFactory;
import com.facilio.constants.FacilioConstants;
import com.facilio.sql.GenericInsertRecordBuilder;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AddTaskOptionsCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		List<TaskContext> tasks = (List<TaskContext>) context.get(FacilioConstants.ContextNames.TASK_LIST);
		if(tasks == null) {
			TaskContext task = (TaskContext) context.get(FacilioConstants.ContextNames.TASK);
			if(task != null) {
				tasks = Collections.singletonList(task);
			}
		}
		
		if (tasks != null && !tasks.isEmpty()) {
			FacilioModule module = ModuleFactory.getTaskInputOptionModule();
	
			GenericInsertRecordBuilder insertBuilder = new GenericInsertRecordBuilder()
															.table(module.getTableName())
															.fields(FieldFactory.getTaskInputOptionsFields())
															;
			
			for(TaskContext task : tasks) {
				switch(task.getInputTypeEnum()) {
//					case CHECKBOX:
					case RADIO:
						if (task.getOptions() != null && !task.getOptions().isEmpty()) {
							for (String option : task.getOptions()) {
								Map<String, Object> optionMap = new HashMap<>();
								optionMap.put("taskId", task.getId());
								optionMap.put("option", option);
								insertBuilder.addRecord(optionMap);
							}
						}
						break;
//					case BOOLEAN:
//						if (task.getOptions() != null && !task.getOptions().isEmpty()) {
//							for (String option : task.getOptions()) {
//								Map<String, Object> optionMap = new HashMap<>();
//								optionMap.put("taskId", task.getId());
//								optionMap.put("option", option);
//								insertBuilder.addRecord(optionMap);
//							}
//						}
//						break;
					default:
						break;
				}
			}
			insertBuilder.save();
		}
		return false;
	}

}
