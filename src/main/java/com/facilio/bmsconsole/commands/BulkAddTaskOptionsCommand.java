package com.facilio.bmsconsole.commands;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.context.BulkWorkOrderContext;
import com.facilio.bmsconsole.context.TaskContext;
import  com.facilio.bmsconsole.context.TaskContext.InputType;
import com.facilio.bmsconsole.util.PreventiveMaintenanceAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.ModuleFactory;

public class BulkAddTaskOptionsCommand extends FacilioCommand {
	private static final Logger LOGGER = Logger.getLogger(BulkAddActionForTaskCommand.class.getName());
	@Override
	public boolean executeCommand(Context context) throws Exception {
		PreventiveMaintenanceAPI.logIf(92L,"Entering BulkAddTaskOptionsCommand");
		BulkWorkOrderContext bulkWorkOrderContext = (BulkWorkOrderContext) context.get(FacilioConstants.ContextNames.BULK_WORK_ORDER_CONTEXT);

		List<TaskContext> tasks = bulkWorkOrderContext.getTaskContextList();
		if (tasks == null || tasks.isEmpty()) {
			return false;
		}

		FacilioModule module = ModuleFactory.getTaskInputOptionModule();

		GenericInsertRecordBuilder insertBuilder = new GenericInsertRecordBuilder()
				.table(module.getTableName())
				.fields(FieldFactory.getTaskInputOptionsFields());

		for (TaskContext task : tasks) {
			if (task.getInputTypeEnum() == InputType.RADIO) {
				if (task.getOptions() != null && !task.getOptions().isEmpty()) {
					for (String option: task.getOptions()) {
						Map<String, Object> optionMap = new HashMap<>();
						optionMap.put("taskId", task.getId());
						optionMap.put("option", option);
						insertBuilder.addRecord(optionMap);
					}
				}
			}
		}

		insertBuilder.save();

		PreventiveMaintenanceAPI.logIf(92L,"done BulkAddTasksCommand");
		return false;
	}
}

