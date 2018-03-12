package com.facilio.bmsconsole.commands;

import java.util.List;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.commons.lang3.StringUtils;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.ReadingContext;
import com.facilio.bmsconsole.context.TaskContext;
import com.facilio.bmsconsole.criteria.CriteriaAPI;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.bmsconsole.modules.SelectRecordsBuilder;
import com.facilio.bmsconsole.util.ReadingsAPI;
import com.facilio.bmsconsole.util.TicketAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;

public class ValidateAndCreateValuesForInputTaskCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
//		if(ActivityType.ADD_TASK_INPUT == context.get(FacilioConstants.ContextNames.ACTIVITY_TYPE)) {
		TaskContext task = (TaskContext) context.get(FacilioConstants.ContextNames.TASK);
		if(task != null) {
			if((task.getInputValue() != null && !task.getInputValue().isEmpty()) || (task.getInputValues() != null && !task.getInputValues().isEmpty())) {
				List<Long> recordIds = (List<Long>) context.get(FacilioConstants.ContextNames.RECORD_ID_LIST);
				if(recordIds != null && recordIds.size() == 1) {
					TaskContext completeRecord = getTask(recordIds.get(0));
					if(completeRecord != null) {
						if(task.getInputTime() == -1) {
							task.setInputTime(System.currentTimeMillis());
						}
						switch(completeRecord.getInputTypeEnum()) {
							case READING:
								ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
								FacilioField field = modBean.getField(completeRecord.getReadingFieldId());
								FacilioModule readingModule = field.getModule();
								ReadingContext reading = new ReadingContext();
								reading.setId(completeRecord.getReadingDataId());
								reading.setParentId(completeRecord.getResource().getId());
								reading.addReading(field.getName(), task.getInputValue());
								reading.setTtime(task.getInputTime());
								if (completeRecord.getLastReading() == null) {
									Object lastreading = ReadingsAPI.getLastReadingValue(completeRecord.getResource().getId(), field);
									task.setLastReading(lastreading != null ? lastreading : -1);
								}
								context.put(FacilioConstants.ContextNames.MODULE_NAME, readingModule.getName());
								context.put(FacilioConstants.ContextNames.READING, reading);
								break;
							case NUMBER:
								Double.parseDouble(task.getInputValue());
								break;
							case RADIO:
								List<String> options = TicketAPI.getTaskInputOptions(completeRecord.getId());
								if(!options.contains(task.getInputValue())) {
									throw new IllegalArgumentException("Invalid input value");
								}
								break;
							case CHECKBOX:
								options = TicketAPI.getTaskInputOptions(completeRecord.getId());
								if (!task.getInputValues().stream().allMatch(input -> options.contains(input))) {
									throw new IllegalArgumentException("Invalid input value");
								}
								task.setInputValue(StringUtils.join(task.getInputValues(), ","));
								break;
							case TEXT:
								break;
							case NONE:
								task.setInputValue(null);
						}
					}
				}
			}
		}
		else {
			throw new IllegalArgumentException("Task cannot be null during updation of Task");
		}
//		}
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
