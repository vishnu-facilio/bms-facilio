package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.ReadingDataMeta;
import com.facilio.bmsconsole.context.TaskContext;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.util.ReadingsAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;

public class ValidateTasksCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		List<TaskContext> tasks = null;
		Map<String, List<TaskContext>> taskMap = (Map<String, List<TaskContext>>) context.get(FacilioConstants.ContextNames.TASK_MAP);
		if(taskMap == null) {
			tasks = (List<TaskContext>) context.get(FacilioConstants.ContextNames.TASK_LIST);
			if(tasks == null) {
				TaskContext task = (TaskContext) context.get(FacilioConstants.ContextNames.TASK);
				if(task != null) {
					tasks = Collections.singletonList(task);
				}
			}
		}
		else {
			tasks = new ArrayList<>();
			for(Map.Entry<String, List<TaskContext>> entry : taskMap.entrySet()) {
				tasks.addAll(entry.getValue());
			}
		}
		
		if (tasks != null && !tasks.isEmpty()) {
			List<ReadingDataMeta> metaList = new ArrayList<>();
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			for(TaskContext task : tasks) {
				if (task.getInputTypeEnum() == null) {
					task.setInputType(TaskContext.InputType.NONE);
				}
				else {
					switch(task.getInputTypeEnum()) {
						case READING:
							if (task.getResource() == null || task.getResource().getId() == -1) {
								throw new IllegalArgumentException("Resource cannot be null when reading is enabled for task");
							}
							if(task.getReadingFieldId() == -1) {
								throw new IllegalArgumentException("Reading ID cannot be null when reading is enabled for task");
							}
							FacilioField readingField = modBean.getField(task.getReadingFieldId());
							ReadingDataMeta meta = ReadingsAPI.getReadingDataMeta(task.getResource().getId(), readingField);
							switch (meta.getInputTypeEnum()) {
								case CONTROLLER_MAPPED:
									throw new IllegalArgumentException("Readings that are mapped with controller cannot be used.");
								case FORMULA_FIELD:
								case HIDDEN_FORMULA_FIELD:
									throw new IllegalArgumentException("Readings that are mapped with formula field cannot be used.");
								case TASK:
									throw new IllegalArgumentException(readingField.getName()+" cannot be used as it is already used in another task.");
								default:
									metaList.add(meta);
									break;
							}
							break;
						case CHECKBOX:
						case RADIO:
							if(task.getOptions() == null || task.getOptions().size() < 2) {
								throw new IllegalArgumentException("Minimum two options has to be added for CHECKBOX/ RADIO task");
							}
							break;
						default:
							break;
					}
				}
			}
			if (!metaList.isEmpty()) {
				context.put(FacilioConstants.ContextNames.READING_DATA_META_LIST, metaList);
				context.put(FacilioConstants.ContextNames.READING_DATA_META_TYPE, ReadingDataMeta.ReadingInputType.TASK);
			}
		}
		return false;
	}

}
