package com.facilio.bmsconsole.commands;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.TaskContext;
import com.facilio.bmsconsole.context.TaskContext.TaskStatus;
import com.facilio.bmsconsole.context.TaskSectionContext;
import com.facilio.bmsconsole.context.WorkOrderContext;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.bmsconsole.modules.InsertRecordBuilder;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;


public class AddTasksCommand implements Command {
	
	private static final Logger LOGGER = Logger.getLogger(AddTasksCommand.class.getName());
	
	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		Map<String, List<TaskContext>> taskMap = (Map<String, List<TaskContext>>) context.get(FacilioConstants.ContextNames.TASK_MAP);
		WorkOrderContext workOrder = (WorkOrderContext) context.get(FacilioConstants.ContextNames.WORK_ORDER);
		if(taskMap != null && !taskMap.isEmpty()) {
			Map<String, TaskSectionContext> sections = (Map<String, TaskSectionContext>) context.get(FacilioConstants.ContextNames.TASK_SECTIONS);
			String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			FacilioModule module = modBean.getModule(moduleName);
			List<FacilioField> fields = (List<FacilioField>) context.get(FacilioConstants.ContextNames.EXISTING_FIELD_LIST);
			
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
					if(workOrder != null) {
						task.setParentTicketId(workOrder.getId());
					}
					task.setCreatedBy(AccountUtil.getCurrentUser());
					builder.addRecord(task);
				}
			});
			
			builder.save();
			context.put(FacilioConstants.ContextNames.TASK_LIST, builder.getRecords());
 			context.put(FacilioConstants.ContextNames.IDS_TO_UPDATE_TASK_COUNT, Collections.singletonList(workOrder.getId()));
		}
		else {
//			throw new IllegalArgumentException("Task list cannot be null/ empty");
		}
		return false;
	}
}
