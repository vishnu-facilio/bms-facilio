package com.facilio.bmsconsole.commands;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.TaskContext;
import com.facilio.bmsconsole.util.TicketAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;

public class GetTaskInputDataCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		List<TaskContext> tasks = (List<TaskContext>) context.get(FacilioConstants.ContextNames.TASK_LIST);
		if(tasks == null) {
			TaskContext task = (TaskContext) context.get(FacilioConstants.ContextNames.TASK);
			if(task != null) {
				tasks = Collections.singletonList(task);
			}
		}
		if(tasks != null && !tasks.isEmpty()) {
			for(TaskContext task : tasks) {
				switch(task.getInputTypeEnum()) {
					case RADIO:
						task.setOptions(TicketAPI.getTaskInputOptions(task.getId()));
					case CHECKBOX:
						task.setOptions(TicketAPI.getTaskInputOptions(task.getId()));
						if(task.getInputValue() != null && !task.getInputValue().isEmpty()) {
							task.setInputValues(Arrays.asList(task.getInputValue().split("\\s*,\\s*")));
						}
						else {
							task.setInputValues(Collections.emptyList());
						}
						break;
					case READING:
						ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
						task.setReadingField(modBean.getField(task.getReadingFieldId()));
					default:
						break;
				}
			}
		}
		return false;
	}

}
