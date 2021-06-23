package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Logger;

import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;
import org.apache.commons.lang3.StringUtils;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.BulkWorkOrderContext;
import com.facilio.bmsconsole.context.ReadingContext;
import com.facilio.bmsconsole.context.TaskContext;
import com.facilio.bmsconsole.util.PreventiveMaintenanceAPI;
import com.facilio.bmsconsole.util.ReadingsAPI;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.fields.FacilioField;

public class BulkAddTaskDefaultValueReadingsCommand extends FacilioCommand {

	private static final Logger LOGGER = Logger.getLogger(UpdateReadingDataMetaCommand.class.getName());
	@Override
	public boolean executeCommand(Context context) throws Exception {
		PreventiveMaintenanceAPI.logIf(92L,"Entering BulkAddTaskDefaultValueReadingsCommand");
		BulkWorkOrderContext bulkWorkOrderContext = (BulkWorkOrderContext) context.get(FacilioConstants.ContextNames.BULK_WORK_ORDER_CONTEXT);
		Map<String, List<ReadingContext>> readingMap = new HashMap<>();

		List<Map<String, List<TaskContext>>> taskMaps = bulkWorkOrderContext.getTaskMaps();
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");

		for (int i = 0; i < taskMaps.size(); i++) {
			Map<String, List<TaskContext>> taskMap = taskMaps.get(i);
			if (taskMap == null || taskMap.isEmpty()) {
				continue;
			}
			long pmId = bulkWorkOrderContext.getWorkOrderContexts().get(i).getPm().getId();
			long time = System.currentTimeMillis();
			for( Entry<String, List<TaskContext>> entry : taskMap.entrySet()) {
				for(TaskContext task : entry.getValue()) {
					if (StringUtils.isNotEmpty(task.getDefaultValue()) && task.getReadingFieldId() != -1) {
						if (task.getReadingField() == null) {
							task.setReadingField(modBean.getField(task.getReadingFieldId()));
						}
						addReading(task, time, pmId, readingMap);
					}
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

		PreventiveMaintenanceAPI.logIf(92L,"done BulkAddTaskDefaultValueReadingsCommand");
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
