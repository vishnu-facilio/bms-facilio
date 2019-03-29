package com.facilio.bmsconsole.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.TaskContext;
import com.facilio.bmsconsole.context.TaskSectionContext;
import com.facilio.bmsconsole.criteria.CriteriaAPI;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.bmsconsole.modules.UpdateRecordBuilder;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import java.util.List;

public class UpdateTaskWithSectionCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		TaskSectionContext section = (TaskSectionContext) context.get(FacilioConstants.ContextNames.TASK_SECTION);
		if(section != null && section.getTaskIds() != null && !section.getTaskIds().isEmpty()) {
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			FacilioModule taskModule = modBean.getModule(FacilioConstants.ContextNames.TASK);
			List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ContextNames.TASK);
			for (int i = 0; i<section.getTaskIds().size(); i++) {
				long taskId = section.getTaskIds().get(i);
				
				TaskContext task = new TaskContext();
				task.setSectionId(section.getId());
				task.setSequence(i);
				
				UpdateRecordBuilder<TaskContext> updateBuilder = new UpdateRecordBuilder<TaskContext>()
																		.module(taskModule)
																		.fields(fields)
																		.andCondition(CriteriaAPI.getIdCondition(taskId, taskModule));
				updateBuilder.update(task);
			}
		}
		return false;
	}

}
