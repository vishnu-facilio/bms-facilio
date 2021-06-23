package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.context.TaskContext;
import com.facilio.bmsconsole.context.TaskSectionContext;
import com.facilio.bmsconsole.util.TicketAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;

;

public class GetMultipleRelatedTasksCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		
		List<Long> recordIds = (List<Long>) context.get(FacilioConstants.ContextNames.RECORD_ID_LIST);
		Map<Long, Map<String, Object>> taskMap = new HashMap<>();
		recordIds.forEach(id -> {
			Map<String, Object> value = new HashMap<>();
			value.put("sections", null);
			value.put("tasks", null);
			taskMap.put(id, value);
		});
		
		setRelatedTaskSections(recordIds, taskMap);
		
		List<TaskContext> alltasks = TicketAPI.getRelatedTasks(recordIds);
		List<TaskContext> tasks = alltasks.stream().filter(t -> !t.isPreRequest()).collect(Collectors.toList());
		context.put(FacilioConstants.ContextNames.TASK_LIST, tasks);
		groupTaskBySection(tasks, taskMap);
		
		
		context.put(FacilioConstants.ContextNames.TASK_MAP, taskMap);
		
		CommonCommandUtil.loadTaskLookups(tasks);
		
		Map<Long, Map<String, Object>> prerequisiteMap = new HashMap<>();
		recordIds.forEach(id -> {
			Map<String, Object> value = new HashMap<>();
			value.put("sections", null);
			value.put("tasks", null);
			prerequisiteMap.put(id, value);
		});
		List<TaskContext> prerequisites = alltasks.stream().filter(t -> t.isPreRequest()).collect(Collectors.toList());
		context.put(FacilioConstants.ContextNames.PRE_REQUEST_LIST, prerequisites);
		groupTaskBySection(prerequisites, prerequisiteMap);
		context.put(FacilioConstants.ContextNames.PRE_REQUEST_MAP, prerequisiteMap);
		return false;
	}
	
	private void setRelatedTaskSections(List<Long> parentTicketIds, Map<Long, Map<String, Object>> taskMap) throws Exception {
		if(parentTicketIds != null && !parentTicketIds.isEmpty()) {
			FacilioModule module = ModuleFactory.getTaskSectionModule();
			List<FacilioField> fields = FieldFactory.getTaskSectionFields();
			FacilioField parentId = FieldFactory.getAsMap(fields).get("parentTicketId");
			GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
															.select(fields)
															.table(module.getTableName())
															.andCondition(CriteriaAPI.getCondition(parentId, parentTicketIds, NumberOperators.EQUALS));
			
			List<Map<String, Object>> sectionProps = selectBuilder.get();
			
			Map<Long, TaskSectionContext> sections;
			if(sectionProps != null && !sectionProps.isEmpty()) {
				for(Map<String, Object> sectionProp : sectionProps) {
					TaskSectionContext section = FieldUtil.getAsBeanFromMap(sectionProp, TaskSectionContext.class);
					if (taskMap.get(section.getParentTicketId()).get("sections") == null) {
						Map<String, Object> sectionMap = new HashMap<>();
						sections = new HashMap<Long, TaskSectionContext>();
						sectionMap.put("sections", sections);
						if (!section.isPreRequest()) {
							taskMap.put(section.getParentTicketId(), sectionMap);
						}
					}
					else {
						sections = (Map<Long, TaskSectionContext>) taskMap.get(section.getParentTicketId()).get("sections");
					}
					sections.put(section.getId(), section);
				}
			}
		}
	}
	
	private void groupTaskBySection(List<TaskContext> tasks, Map<Long, Map<String, Object>> taskMap) throws Exception {
		if(tasks != null && !tasks.isEmpty()) {
			for(TaskContext task : tasks) {
				Map<Long, List<TaskContext>> sectionIdVsTask;
				if (taskMap.get(task.getParentTicketId()).get("tasks") == null) {
					sectionIdVsTask = new HashMap<>();
					taskMap.get(task.getParentTicketId()).put("tasks", sectionIdVsTask);
				}
				else {
					sectionIdVsTask = (Map<Long, List<TaskContext>>) taskMap.get(task.getParentTicketId()).get("tasks");
				}
				List<TaskContext> taskList = sectionIdVsTask.get(task.getSectionId());
				if (taskList == null) {
					taskList = new ArrayList<>();
					sectionIdVsTask.put(task.getSectionId(), taskList);
				}
				taskList.add(task);
			}
		}
	}

}
