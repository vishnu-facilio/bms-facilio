package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.facilio.bmsconsole.activity.WorkOrderActivityType;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.context.TaskContext;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.FieldUtil;

public class AddTaskReadingCorrectionActivityCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        List<TaskContext> tasks = (List<TaskContext>) context.get(FacilioConstants.ContextNames.TASKS);
        List<TaskContext> oldTasks = (List<TaskContext>) context.get(FacilioConstants.ContextNames.OLD_TASKS);

        if (CollectionUtils.isEmpty(tasks)) {
            return false;
        }

        List<Long> recordIds = tasks.stream().map(TaskContext::getId).collect(Collectors.toList());
        Map<Long, TaskContext> oldTaskContextMap = FieldUtil.getAsMap(oldTasks);
        Map<Long, TaskContext> taskContextMap = FieldUtil.getAsMap(tasks);

        Map<Long, List<TaskContext>> workOrderMap = new HashMap<>();

        for (long recordId: recordIds) {
            TaskContext oldTaskContext = oldTaskContextMap.get(recordId);

            if (!workOrderMap.containsKey(oldTaskContext.getParentTicketId())) {
                workOrderMap.put(oldTaskContext.getParentTicketId(), new ArrayList<>());
            }
            workOrderMap.get(oldTaskContext.getParentTicketId()).add(oldTaskContext);
        }

        for (Map.Entry<Long, List<TaskContext>> entry: workOrderMap.entrySet()) {
            List<TaskContext> oldTaskContexts = entry.getValue();
            for (TaskContext oldTaskContext: oldTaskContexts) {

                long parentTicketId = entry.getKey();
                TaskContext taskContext = taskContextMap.get(oldTaskContext.getId());

                JSONObject info = new JSONObject();
                JSONArray updatedFields = new JSONArray();
                JSONObject taskJson = new JSONObject();

                taskJson.put("subject", oldTaskContext.getSubject());
                taskJson.put("id", oldTaskContext.getId());
                info.put("task", taskJson);

                JSONObject fieldProp = new JSONObject();
                fieldProp.put("oldValue", oldTaskContext.getInputValue());
                fieldProp.put("newValue", taskContext.getInputValue());
                updatedFields.add(fieldProp);

                JSONObject timeProp = new JSONObject();
                timeProp.put("oldTime", oldTaskContext.getInputTime());
                timeProp.put("newTime", taskContext.getInputTime());
                updatedFields.add(timeProp);

                JSONObject unitProp = new JSONObject();
                if (oldTaskContext.getReadingFieldUnitEnum() != null) {
                	unitProp.put("oldUnit", oldTaskContext.getReadingFieldUnitEnum().getDisplayName());
                	
                	if (taskContext.getReadingFieldUnitEnum() != null) {
                        unitProp.put("newUnit", taskContext.getReadingFieldUnitEnum().getDisplayName());
                    } else {
                        unitProp.put("newUnit", oldTaskContext.getReadingFieldUnitEnum().getDisplayName());
                    }
                }
                
                

                info.put("updatedFields", updatedFields);
                CommonCommandUtil.addActivityToContext(parentTicketId, -1, WorkOrderActivityType.READING_CORRECTION, info, (FacilioContext) context);
            }
        }
        return false;
    }
}
