package com.facilio.bmsconsole.commands;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import com.facilio.bmsconsole.context.ReadingContext;
import com.facilio.bmsconsole.context.ReadingDataMeta;
import com.facilio.bmsconsole.context.TaskContext;
import com.facilio.bmsconsole.util.ReadingsAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.fields.NumberField;
import com.facilio.unitconversion.Unit;
import com.facilio.unitconversion.UnitsUtil;
import com.facilio.util.FacilioUtil;

public class ReadingUnitConversionToRdmOrSiUnit extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {

		List<TaskContext> taskContexts = (List<TaskContext>) context.get(FacilioConstants.ContextNames.TASKS);
		Map<TaskContext, ReadingContext> taskReadingsMap = (Map<TaskContext, ReadingContext>) context.get(FacilioConstants.ContextNames.TASK_READINGS);

		List<Long> recordIds;
		if (CollectionUtils.isEmpty(taskContexts)) {
			taskContexts = Collections.singletonList((TaskContext) context.get(FacilioConstants.ContextNames.TASK));
			ReadingContext reading = (ReadingContext) context.get(FacilioConstants.ContextNames.READING);

			taskReadingsMap = new HashMap<>();
			taskReadingsMap.put(taskContexts.get(0), reading);

			recordIds = (List<Long>) context.get(FacilioConstants.ContextNames.RECORD_ID_LIST);
		} else {
			recordIds = taskContexts.stream().map(TaskContext::getId).collect(Collectors.toList());
		}

		if (CollectionUtils.isEmpty(recordIds)) {
			return false;
		}

		for (TaskContext currentTask: taskContexts) {
			if (currentTask == null) {
				continue;
			}

			if (currentTask.getReadingFieldUnitEnum() == null) {
				continue;
			}

			ReadingContext reading = taskReadingsMap.get(currentTask);

			Map<Long, TaskContext> tasks = (Map<Long, TaskContext>) context.get(FacilioConstants.ContextNames.TASK_MAP);
			if(tasks != null && currentTask != null && reading != null) {
				TaskContext taskContext = tasks.get(recordIds.get(0));
				if(taskContext.getInputTypeEnum() != null) {
					switch(taskContext.getInputTypeEnum()) {
						case READING:
							if (taskContext.getReadingField() != null && taskContext.getResource() != null && taskContext.getReadingField() instanceof NumberField) {
								if(currentTask.getInputValue() != null) {
									ReadingDataMeta rdm = ReadingsAPI.getReadingDataMeta(taskContext.getResource().getId(), taskContext.getReadingField());
									Double currentTaskValue = FacilioUtil.parseDouble(currentTask.getInputValue());
									Double convertedInputReading;

									if(rdm != null && rdm.getUnitEnum() != null) {
										Unit rdmUnit = rdm.getUnitEnum();
										convertedInputReading = UnitsUtil.convert(currentTaskValue, currentTask.getReadingFieldUnitEnum(), rdmUnit);
									} else {
										convertedInputReading = UnitsUtil.convertToSiUnit(currentTaskValue, currentTask.getReadingFieldUnitEnum());
									}

									reading.addReading(taskContext.getReadingField().getName(), convertedInputReading);
								}
							}
						break;
					}
				}
			}
		}

		return false;
	}
}
