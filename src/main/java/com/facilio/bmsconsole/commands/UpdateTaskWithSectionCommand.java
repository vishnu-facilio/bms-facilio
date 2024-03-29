package com.facilio.bmsconsole.commands;

import java.util.List;

import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.TaskContext;
import com.facilio.bmsconsole.context.TaskSectionContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.UpdateRecordBuilder;
import com.facilio.modules.fields.FacilioField;

public class UpdateTaskWithSectionCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
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
