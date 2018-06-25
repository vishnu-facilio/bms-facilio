package com.facilio.bmsconsole.commands;

import java.util.Collections;
import java.util.List;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.TaskContext;
import com.facilio.bmsconsole.modules.EnumField;
import com.facilio.bmsconsole.util.TicketAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;

public class GetTaskInputDataCommand implements Command {
	private static final Logger LOGGER = LogManager.getLogger(GetTaskInputDataCommand.class.getName());

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
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			for(TaskContext task : tasks) {
				switch(task.getInputTypeEnum()) {
					case TEXT:
					case NUMBER:
					case READING:
					case BOOLEAN:
						task.setReadingField(modBean.getField(task.getReadingFieldId()));
						break;
					case RADIO:
						if (task.getReadingFieldId() != -1) {
							task.setReadingField(modBean.getField(task.getReadingFieldId()));
							task.setOptions(((EnumField)task.getReadingField()).getValues());
						}
						else {
							task.setOptions(TicketAPI.getTaskInputOptions(task.getId()));
						}
						break;
//					case CHECKBOX:
//						task.setOptions(TicketAPI.getTaskInputOptions(task.getId()));
//						if(task.getInputValue() != null && !task.getInputValue().isEmpty()) {
//							task.setInputValues(Arrays.asList(task.getInputValue().split("\\s*,\\s*")));
//						}
//						else {
//							task.setInputValues(Collections.emptyList());
//						}
//						break;
					default:
						break;
				}
			}
		}
		return false;
	}

}
