package com.facilio.bmsconsole.commands;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.BulkWorkOrderContext;
import com.facilio.bmsconsole.context.ReadingContext;
import com.facilio.bmsconsole.context.TaskContext;
import com.facilio.bmsconsole.util.PreventiveMaintenanceAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.UpdateRecordBuilder;
import com.facilio.modules.fields.FacilioField;

public class BulkUpdateTaskReadingInfoCommand extends FacilioCommand {
	private static final Logger LOGGER = Logger.getLogger(UpdateReadingDataMetaCommand.class.getName());
	@Override
	public boolean executeCommand(Context context) throws Exception {
		PreventiveMaintenanceAPI.logIf(92L,"Entering BulkUpdateTaskReadingInfoCommand");
		Map<String, List<ReadingContext>> readingMap = (Map<String, List<ReadingContext>>) context.get(FacilioConstants.ContextNames.READINGS_MAP);
		if (readingMap == null) {
			return false;
		}

		Map<Long, ReadingContext> readings = readingMap.values().stream().flatMap(List::stream).collect(Collectors.toMap(r -> (Long) r.getReading("taskId"), Function.identity()));
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
		FacilioModule module = modBean.getModule(moduleName);
		List<FacilioField> fields = (List<FacilioField>) context.get(FacilioConstants.ContextNames.EXISTING_FIELD_LIST);

		BulkWorkOrderContext bulkWorkOrderContext = (BulkWorkOrderContext) context.get(FacilioConstants.ContextNames.BULK_WORK_ORDER_CONTEXT);

		List<TaskContext> tasks = bulkWorkOrderContext.getTaskContextList();
		if (tasks == null || tasks.isEmpty()) {
			return false;
		}

		for(TaskContext task : tasks) {
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

		PreventiveMaintenanceAPI.logIf(92L,"done BulkUpdateTaskReadingInfoCommand");
		return false;
	}
}
