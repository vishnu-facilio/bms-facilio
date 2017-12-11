package com.facilio.bmsconsole.commands;

import java.util.Collections;
import java.util.List;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.tiles.request.collection.CollectionUtil;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.TaskContext;
import com.facilio.bmsconsole.criteria.CriteriaAPI;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.bmsconsole.modules.SelectRecordsBuilder;
import com.facilio.bmsconsole.workflow.ActivityType;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;

public class CreateReadingForTaskCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		if(ActivityType.ADD_TASK_READING_VALUE == context.get(FacilioConstants.ContextNames.ACTIVITY_TYPE)) {
			TaskContext task = (TaskContext) context.get(FacilioConstants.ContextNames.TASK);
			if(task != null) {
				if(task.getReadingData() != null) {
					List<Long> recordIds = (List<Long>) context.get(FacilioConstants.ContextNames.RECORD_ID_LIST);
					if(recordIds != null && recordIds.size() == 1) {
						TaskContext completeRecord = getTask(recordIds.get(0));
						if(completeRecord != null && completeRecord.isReadingTask()) {
							ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
							FacilioField field = modBean.getField(completeRecord.getReadingFieldId());
							FacilioModule readingModule = field.getModule();
							task.getReadingData().setParentId(completeRecord.getAsset().getId());
							context.put(FacilioConstants.ContextNames.MODULE_NAME, readingModule.getName());
							context.put(FacilioConstants.ContextNames.READINGS, Collections.singletonList(task.getReadingData()));
						}
					}
				}
				else {
					throw new IllegalArgumentException("ReadingData cannot be null during addtition of reading value for task");
				}
			}
			else {
				throw new IllegalArgumentException("Task cannot be null during updation of Task");
			}
		}
		return false;
	}
	
	private TaskContext getTask(long id) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.TASK);
		SelectRecordsBuilder<TaskContext> builder = new SelectRecordsBuilder<TaskContext>()
														.module(module)
														.beanClass(TaskContext.class)
														.select(modBean.getAllFields(FacilioConstants.ContextNames.TASK))
														.andCondition(CriteriaAPI.getIdCondition(id, module));
		
		List<TaskContext> tasks = builder.get();
		if(tasks != null && !tasks.isEmpty()) {
			return tasks.get(0);
		}
		return null;
	}

}
