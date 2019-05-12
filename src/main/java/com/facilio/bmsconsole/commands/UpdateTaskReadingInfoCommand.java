package com.facilio.bmsconsole.commands;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.ReadingContext;
import com.facilio.bmsconsole.context.TaskContext;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.modules.FacilioField;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.UpdateRecordBuilder;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;

public class UpdateTaskReadingInfoCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		Map<String, List<ReadingContext>> readingMap = (Map<String, List<ReadingContext>>) context.get(FacilioConstants.ContextNames.READINGS_MAP);
		if (readingMap != null) {
			Map<Long, ReadingContext> readings = readingMap.values().stream().flatMap(List::stream).collect(Collectors.toMap(r -> (Long) r.getReading("taskId"), Function.identity()));
			Map<String, List<TaskContext>> taskMap = (Map<String, List<TaskContext>>) context.get(FacilioConstants.ContextNames.TASK_MAP);
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
			FacilioModule module = modBean.getModule(moduleName);
			List<FacilioField> fields = (List<FacilioField>) context.get(FacilioConstants.ContextNames.EXISTING_FIELD_LIST);
			
			if (taskMap != null && !taskMap.isEmpty()) {
				for( Entry<String, List<TaskContext>> entry : taskMap.entrySet()) {
					for(TaskContext task : entry.getValue()) {
						if (readings.containsKey(task.getId())) {
							TaskContext newTask = new TaskContext();
							newTask.setReadingDataId(readings.get(task.getId()).getId());
							
							UpdateRecordBuilder<TaskContext> updateBuilder = new UpdateRecordBuilder<TaskContext>()
									.module(module)
									.fields(fields)
									.andCondition(CriteriaAPI.getIdCondition(task.getId(), module));
							updateBuilder.update(newTask);
						}
					}
				}
			}
		}
		return false;
	}
}
