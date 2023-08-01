package com.facilio.bmsconsole.commands;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import com.facilio.command.FacilioCommand;
import com.facilio.command.PostTransactionCommand;
import org.apache.commons.chain.Context;
import org.apache.commons.lang3.StringUtils;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.TaskContext;
import com.facilio.bmsconsole.context.TaskContext.InputType;
import com.facilio.bmsconsole.context.TaskContext.TaskStatus;
import com.facilio.bmsconsole.context.TaskSectionContext;
import com.facilio.bmsconsole.context.WorkOrderContext;
import com.facilio.bmsconsole.util.TicketAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.FacilioModulePredicate;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.InsertRecordBuilder;
import com.facilio.modules.fields.FacilioField;
import org.json.simple.JSONObject;


public class AddTasksCommand extends FacilioCommand implements PostTransactionCommand {
	
	private static final Logger LOGGER = Logger.getLogger(AddTasksCommand.class.getName());
	private List<Long> idsToUpdateTaskCount;
	
	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		Map<String, List<TaskContext>> taskMap = (Map<String, List<TaskContext>>) context.get(FacilioConstants.ContextNames.TASK_MAP);
		Map<String, List<TaskContext>> preRequestMap = (Map<String, List<TaskContext>>) context.get(FacilioConstants.ContextNames.PRE_REQUEST_MAP);
		WorkOrderContext workOrder = (WorkOrderContext) context.get(FacilioConstants.ContextNames.WORK_ORDER);
		String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(moduleName);
		List<FacilioField> fields = (List<FacilioField>) context.get(FacilioConstants.ContextNames.EXISTING_FIELD_LIST);
		
		if(taskMap != null && !taskMap.isEmpty()) {
			Map<String, TaskSectionContext> sections = (Map<String, TaskSectionContext>) context.get(FacilioConstants.ContextNames.TASK_SECTIONS);
			List<TaskContext> tasks = addTasks(module, taskMap, sections, workOrder, fields, false);
			context.put(FacilioConstants.ContextNames.TASK_LIST, tasks);
			
			idsToUpdateTaskCount = Collections.singletonList(workOrder.getId());
		}
		
		if (preRequestMap != null && !preRequestMap.isEmpty()) {
			Map<String, TaskSectionContext> sections = (Map<String, TaskSectionContext>) context.get(FacilioConstants.ContextNames.PRE_REQUEST_SECTIONS);
			List<TaskContext> preRequisites = addTasks(module, preRequestMap, sections, workOrder, fields, true);
			context.put(FacilioConstants.ContextNames.PRE_REQUEST_LIST, preRequisites);
		}
		return false;
	}
	
	private List<TaskContext> addTasks(FacilioModule module, Map<String, List<TaskContext>> taskMap, Map<String, TaskSectionContext> sections, WorkOrderContext workOrder, List<FacilioField> fields, boolean isPrerequest) throws Exception {
		
		InsertRecordBuilder<TaskContext> builder = new InsertRecordBuilder<TaskContext>()
														.module(module)
														.withLocalId()
														.fields(fields)
														;
		taskMap.forEach((sectionName, tasks) -> {
			long sectionId = -1;
			if(!sectionName.equals(FacilioConstants.ContextNames.DEFAULT_TASK_SECTION)) {
				sectionId = sections.get(sectionName).getId();
			}
			for(TaskContext task : tasks) {
				task.setCreatedTime(System.currentTimeMillis());
				task.setSectionId(sectionId);
				task.setStatusNew(TaskStatus.OPEN);
				task.setPreRequest(isPrerequest);
				if(workOrder != null) {
					task.setParentTicketId(workOrder.getId());
					if (task.getSiteId() == -1) {
						task.setSiteId(workOrder.getSiteId());
					}
				}
				// Handling additionalInfo for tasks from PMv1
				JSONObject jsonObject = new JSONObject();
				jsonObject.putAll(task.getData());
				task.setAdditionalInfo(jsonObject);
				task.setInputValue(isPrerequest ? null : task.getDefaultValue());
				if(StringUtils.isNotEmpty(task.getInputValue()) && StringUtils.isNotEmpty(task.getFailureValue())) {
					if (task.getInputTypeEnum() == InputType.NUMBER) {
						FacilioModulePredicate predicate = task.getDeviationOperator().getPredicate("inputValue", task.getFailureValue());
						task.setFailed(predicate.evaluate(task));
					}
					else if (task.getFailureValue().equals(task.getInputValue())) {
						task.setFailed(true);
					}
					else {
						task.setFailed(false);
					}
				}
				task.setCreatedBy(AccountUtil.getCurrentUser());
				builder.addRecord(task);
			}
		});
		
		builder.save();
		
		return builder.getRecords();
	}
	
	@Override
	public boolean postExecute() throws Exception {
		TicketAPI.updateTaskCount(idsToUpdateTaskCount);
		return false;
	}
}
