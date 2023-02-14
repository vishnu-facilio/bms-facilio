package com.facilio.bmsconsole.commands;

import java.util.Collections;
import java.util.List;

import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.context.TaskContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.fields.NumberField;
import com.facilio.unitconversion.UnitsUtil;
import com.facilio.workflows.util.WorkflowUtil;

public class SiUnitConversionToSelectedReadingUnit extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		
		if(!AccountUtil.isFeatureEnabled(AccountUtil.FeatureLicense.READING_FIELD_UNITS_VALIDATION)) {
			return false;
		}
		
		List<TaskContext> tasks = (List<TaskContext>) context.get(FacilioConstants.ContextNames.TASK_LIST);
		if(tasks == null) {
			TaskContext task = (TaskContext) context.get(FacilioConstants.ContextNames.TASK);
			if(task != null) {
				tasks = Collections.singletonList(task);
			}
		}
		
		if(tasks != null && !tasks.isEmpty())
		{
			for(TaskContext task: tasks)
			{
				if (task.getInputTypeEnum() == TaskContext.InputType.READING && task.getReadingField() != null && task.getReadingFieldUnit() > 0 && task.getResource() != null && task.getReadingField() instanceof NumberField)
				{
					NumberField numberField = (NumberField) task.getReadingField();
					if(numberField.getMetricEnum() != null && task.getInputValue() != null)
					{
						int siUnit = numberField.getMetricEnum().getSiUnitId();
						if (siUnit != -1) {
							String convertedReadingString = WorkflowUtil.getStringValueFromDouble(UnitsUtil.convert(task.getInputValue(), siUnit, task.getReadingFieldUnit()));
							if(convertedReadingString != null) {
								task.setInputValue(String.valueOf(convertedReadingString));
							}
						}
					}
				}
			}
			
			context.put(FacilioConstants.ContextNames.TASK_LIST, tasks);
			
		}
		
		
		return false;
	}

}
