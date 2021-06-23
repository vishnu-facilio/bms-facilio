package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.context.BulkWorkOrderContext;
import com.facilio.bmsconsole.context.TaskContext;
import com.facilio.bmsconsole.context.TaskSectionContext;
import com.facilio.bmsconsole.context.WorkOrderContext;
import com.facilio.bmsconsole.util.PreventiveMaintenanceAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;

public class BulkAddTaskSectionsCommand extends FacilioCommand {

	private static final Logger LOGGER = Logger.getLogger(BulkAddTaskSectionsCommand.class.getName());
	@Override
	public boolean executeCommand(Context context) throws Exception {
		PreventiveMaintenanceAPI.logIf(92L, "Entering BulkAddTaskSectionsCommand");
		BulkWorkOrderContext bulkWorkOrderContext = (BulkWorkOrderContext) context.get(FacilioConstants.ContextNames.BULK_WORK_ORDER_CONTEXT);

		if (bulkWorkOrderContext.getWorkOrderContexts() == null
				|| bulkWorkOrderContext.getWorkOrderContexts().isEmpty()
				|| bulkWorkOrderContext.getTaskMaps() == null
				|| bulkWorkOrderContext.getTaskMaps().isEmpty()) {
			PreventiveMaintenanceAPI.logIf(92L, "Done BulkAddTaskSectionsCommand");
			return false;
		}

		boolean isRecordPresent = false;

		GenericInsertRecordBuilder insertBuilder = new GenericInsertRecordBuilder()
				.table(ModuleFactory.getTaskSectionModule().getTableName())
				.fields(FieldFactory.getTaskSectionFields());

		for (int i = 0; i < bulkWorkOrderContext.getWorkOrderContexts().size(); i++) {
			int sequence = 1;
			WorkOrderContext workOrder = bulkWorkOrderContext.getWorkOrderContexts().get(i);
			Map<String, List<TaskContext>> taskMap = bulkWorkOrderContext.getTaskMaps().get(i);
			if (taskMap == null) {
				continue;
			}
			for(Map.Entry<String, List<TaskContext>> entry : taskMap.entrySet()) {
				if(!entry.getKey().equals(FacilioConstants.ContextNames.DEFAULT_TASK_SECTION)) {
					isRecordPresent = true;
					TaskSectionContext section = new TaskSectionContext();
					section.setParentTicketId(workOrder.getId());
					section.setName(entry.getKey());
					section.setSequenceNumber(sequence++);
					section.setPreRequest(Boolean.FALSE);
					insertBuilder.addRecord(FieldUtil.getAsProperties(section));
				}
			}
		}

		if (isRecordPresent) {
			insertBuilder.save();
			List<Map<String, Object>> records = insertBuilder.getRecords();
			for (Map<String, Object> record: records) {
				TaskSectionContext section = FieldUtil.getAsBeanFromMap(record, TaskSectionContext.class);
				if (bulkWorkOrderContext.getSectionMap().get(section.getParentTicketId()) == null) {
					bulkWorkOrderContext.getSectionMap().put(section.getParentTicketId(), new HashMap<>());
				}
				bulkWorkOrderContext.getSectionMap().get(section.getParentTicketId()).put(section.getName(), section);
			}
		}

		if (bulkWorkOrderContext.getPreRequestMaps() == null || bulkWorkOrderContext.getPreRequestMaps().isEmpty()) {
			PreventiveMaintenanceAPI.logIf(92L, "Done BulkAddTaskSectionsCommand");
			return false;
		}

		isRecordPresent = false;

		GenericInsertRecordBuilder preReqInsertBuilder = new GenericInsertRecordBuilder()
				.table(ModuleFactory.getTaskSectionModule().getTableName())
				.fields(FieldFactory.getTaskSectionFields());

		for (int i = 0; i < bulkWorkOrderContext.getWorkOrderContexts().size(); i++) {
			int sequence = 1;
			Map<String, List<TaskContext>> preRequestMap = bulkWorkOrderContext.getPreRequestMaps().get(i);
			if (preRequestMap == null || preRequestMap.isEmpty()) {
				continue;
			}
			isRecordPresent = true;
			WorkOrderContext workOrder = bulkWorkOrderContext.getWorkOrderContexts().get(i);

			List<TaskSectionContext> sections = new ArrayList<>();
			for (Map.Entry<String, List<TaskContext>> entry : preRequestMap.entrySet()) {
				if (!entry.getKey().equals(FacilioConstants.ContextNames.DEFAULT_TASK_SECTION)) {
					TaskSectionContext section = new TaskSectionContext();
					section.setParentTicketId(workOrder.getId());
					section.setName(entry.getKey());
					section.setSequenceNumber(sequence++);
					section.setPreRequest(Boolean.TRUE);
					preReqInsertBuilder.addRecord(FieldUtil.getAsProperties(section));
					sections.add(section);
				}
			}
		}

		if (isRecordPresent) {
			preReqInsertBuilder.save();
			List<Map<String, Object>> records = preReqInsertBuilder.getRecords();
			for (Map<String, Object> record: records) {
				TaskSectionContext section = FieldUtil.getAsBeanFromMap(record, TaskSectionContext.class);
				if (bulkWorkOrderContext.getPrerequisiteSectionMap().get(section.getParentTicketId()) == null) {
					bulkWorkOrderContext.getPrerequisiteSectionMap().put(section.getParentTicketId(), new HashMap<>());
				}
				bulkWorkOrderContext.getPrerequisiteSectionMap().get(section.getParentTicketId()).put(section.getName(), section);
			}
		}

		PreventiveMaintenanceAPI.logIf(92L, "Done BulkAddTaskSectionsCommand");

		return false;
	}

}
