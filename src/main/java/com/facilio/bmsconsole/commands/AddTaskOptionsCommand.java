package com.facilio.bmsconsole.commands;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.context.TaskContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.ModuleFactory;

public class AddTaskOptionsCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
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
