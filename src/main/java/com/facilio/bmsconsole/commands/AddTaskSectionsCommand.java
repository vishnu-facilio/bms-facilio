package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.context.TaskContext;
import com.facilio.bmsconsole.context.TaskSectionContext;
import com.facilio.bmsconsole.context.WorkOrderContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import org.apache.commons.collections4.CollectionUtils;

public class AddTaskSectionsCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		Map<String, List<TaskContext>> taskMap = (Map<String, List<TaskContext>>) context.get(FacilioConstants.ContextNames.TASK_MAP);
		Map<String, List<TaskContext>> preRequestMap = (Map<String, List<TaskContext>>) context.get(FacilioConstants.ContextNames.PRE_REQUEST_MAP);
		WorkOrderContext workOrder = (WorkOrderContext) context.get(FacilioConstants.ContextNames.WORK_ORDER);
		if(workOrder != null && taskMap != null && !taskMap.isEmpty()) {
			GenericInsertRecordBuilder insertBuilder = new GenericInsertRecordBuilder()
															.table(ModuleFactory.getTaskSectionModule().getTableName())
															.fields(FieldFactory.getTaskSectionFields())
															;
			int sequence = 1;
			List<TaskSectionContext> sections = new ArrayList<>();
			List<String> sectionNameList = workOrder.getSectionNameList();
			if(CollectionUtils.isEmpty(sectionNameList)){
				sectionNameList = new ArrayList<>(taskMap.keySet());
			}
			for(String sectionName : sectionNameList) {
				if(!sectionName.equals(FacilioConstants.ContextNames.DEFAULT_TASK_SECTION)) {
					TaskSectionContext section = new TaskSectionContext();
					section.setParentTicketId(workOrder.getId());
					section.setName(sectionName);
					section.setSequenceNumber(sequence++);
					section.setPreRequest(Boolean.FALSE);
					insertBuilder.addRecord(FieldUtil.getAsProperties(section));
					sections.add(section);
				}
			}
			insertBuilder.save();
			
			Map<String, TaskSectionContext> sectionMap = new HashMap<>();
			List<Map<String, Object>> sectionProps = insertBuilder.getRecords();
			for(int i = 0; i<sectionProps.size(); i++) {
				long id = (long) sectionProps.get(i).get("id");
				TaskSectionContext section = sections.get(i);
				section.setId(id);
				sectionMap.put(section.getName(), section);
			}
			context.put(FacilioConstants.ContextNames.TASK_SECTIONS, sectionMap);
		}
		if (workOrder != null && preRequestMap != null && !preRequestMap.isEmpty()) {
			GenericInsertRecordBuilder insertBuilder = new GenericInsertRecordBuilder()
					.table(ModuleFactory.getTaskSectionModule().getTableName())
					.fields(FieldFactory.getTaskSectionFields());
			int sequence = 1;
			List<TaskSectionContext> sections = new ArrayList<>();
			for (Map.Entry<String, List<TaskContext>> entry : preRequestMap.entrySet()) {

				if (!entry.getKey().equals(FacilioConstants.ContextNames.DEFAULT_TASK_SECTION)) {
					TaskSectionContext section = new TaskSectionContext();
					section.setParentTicketId(workOrder.getId());
					section.setName(entry.getKey());
					section.setSequenceNumber(sequence++);
					section.setPreRequest(Boolean.TRUE);
					insertBuilder.addRecord(FieldUtil.getAsProperties(section));
					sections.add(section);
				}
			}
			insertBuilder.save();

			Map<String, TaskSectionContext> sectionMap = new HashMap<>();
			List<Map<String, Object>> sectionProps = insertBuilder.getRecords();
			for (int i = 0; i < sectionProps.size(); i++) {
				long id = (long) sectionProps.get(i).get("id");
				TaskSectionContext section = sections.get(i);
				section.setId(id);
				sectionMap.put(section.getName(), section);
			}
			context.put(FacilioConstants.ContextNames.PRE_REQUEST_SECTIONS, sectionMap);
		}
		return false;
	}

}
