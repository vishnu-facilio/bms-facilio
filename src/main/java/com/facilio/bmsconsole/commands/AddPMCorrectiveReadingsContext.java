package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import com.facilio.bmsconsole.context.ReadingContext;
import com.facilio.bmsconsole.context.ReadingDataMeta;
import com.facilio.bmsconsole.context.TaskContext;
import com.facilio.bmsconsole.util.ReadingsAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleBaseWithCustomFields;
import com.facilio.modules.fields.FacilioField;

public class AddPMCorrectiveReadingsContext extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        List<TaskContext> tasks = (List<TaskContext>) context.get(FacilioConstants.ContextNames.TASKS);
        List<TaskContext> oldTasks = (List<TaskContext>) context.get(FacilioConstants.ContextNames.OLD_TASKS);

        if (CollectionUtils.isEmpty(tasks) || CollectionUtils.isEmpty(tasks)) {
            return false;
        }

        List<Long> recordIds = tasks.stream().map(ModuleBaseWithCustomFields::getId).collect(Collectors.toList());

        Map<Long, TaskContext> taskMap = FieldUtil.getAsMap(tasks);
        Map<Long, TaskContext> oldTaskAsMap = FieldUtil.getAsMap(oldTasks);

        Map<String, List<ReadingContext>> readingMap = new HashMap<>();
        Map<TaskContext, ReadingContext> taskReadingMap = new HashMap<>();

        for (long recordId: recordIds) {
            TaskContext taskContext = taskMap.get(recordId);
            TaskContext oldTaskContext = oldTaskAsMap.get(recordId);

            ReadingContext reading = new ReadingContext();
            FacilioField field = oldTaskContext.getReadingField();
            FacilioModule readingModule = field.getModule();

            reading.setId(oldTaskContext.getReadingDataId());
            reading.setId(oldTaskContext.getReadingDataId());
            reading.addReading(field.getName(), taskContext.getInputValue());
            reading.setTtime(taskContext.getInputTime());
            long resourceId = oldTaskContext.getResource().getId();
            reading.setParentId(resourceId);

            if (oldTaskContext.getLastReading() == null) {
                ReadingDataMeta meta = ReadingsAPI.getReadingDataMeta(resourceId, field);
                taskContext.setLastReading(meta.getValue() != null ? meta.getValue() : -1);
            }

            if (readingMap.get(readingModule.getName()) == null) {
                readingMap.put(readingModule.getName(), new ArrayList<>());
            }

            readingMap.get(readingModule.getName()).add(reading);

            taskReadingMap.put(taskContext, reading);
        }

        context.put(FacilioConstants.ContextNames.READINGS_MAP, readingMap);
        context.put(FacilioConstants.ContextNames.TASK_READINGS, taskReadingMap);

        return false;
    }
}
