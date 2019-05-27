package com.facilio.bmsconsole.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.ReadingContext;
import com.facilio.bmsconsole.context.TaskContext;
import com.facilio.bmsconsole.context.WorkOrderContext;
import com.facilio.bmsconsole.util.ReadingsAPI;
import com.facilio.bmsconsole.util.WorkOrderAPI;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.fields.FacilioField;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class AddTaskDefaultValueReadingsCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		Map<String, List<TaskContext>> taskMap = (Map<String, List<TaskContext>>) context.get(FacilioConstants.ContextNames.TASK_MAP);
		if (taskMap != null && !taskMap.isEmpty()) {
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			Map<String, List<ReadingContext>> readingMap = new HashMap<>();
			long pmId = -1;
			long time = System.currentTimeMillis();
			for( Entry<String, List<TaskContext>> entry : taskMap.entrySet()) {
				for(TaskContext task : entry.getValue()) {
					if (StringUtils.isNotEmpty(task.getDefaultValue()) && task.getReadingFieldId() != -1) {
						if (pmId == -1) {
							WorkOrderContext wo = WorkOrderAPI.getWorkOrder(task.getParentTicketId());
							pmId = wo.getPm().getId();
						}
						if (task.getReadingField() == null) {
							task.setReadingField(modBean.getField(task.getReadingFieldId()));
						}
						addReading(task, time, pmId, readingMap);
					}
				}
			}
			
			if (!readingMap.isEmpty()) {
				context.put(FacilioConstants.ContextNames.READINGS_MAP, readingMap);
				FacilioContext newContext = new FacilioContext();
				newContext.put(FacilioConstants.ContextNames.READINGS_MAP, readingMap);
				newContext.put(FacilioConstants.ContextNames.ADJUST_READING_TTIME, false);
				ReadOnlyChainFactory.getAddOrUpdateReadingValuesChain().execute(newContext);
			}
		}
		return false;
	}
	
	private void addReading(TaskContext task, long time, long pmId, Map<String, List<ReadingContext>> readingMap) throws Exception {
		FacilioField field = task.getReadingField();
		FacilioModule readingModule = field.getModule();
		
		ReadingContext reading = new ReadingContext();
		reading.addReading(field.getName(), task.getInputValue());
		reading.setTtime(time);
		reading.setParentId(pmId);
		
		ReadingsAPI.addDefaultPMTaskReading(task, reading);
		
		List<ReadingContext> readings = readingMap.get(readingModule.getName());
		if (readings == null) {
			readings = new ArrayList<>();
			readingMap.put(readingModule.getName(), readings);
		}
		readings.add(reading);
	}

}
