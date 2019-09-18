package com.facilio.bmsconsole.commands;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.context.TaskContext;
import com.facilio.bmsconsole.workflow.rule.EventType;
import com.facilio.bmsconsole.workflow.rule.TicketActivity;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.FieldUtil;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class AddTaskReadingCorrectionActivityCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        List<TaskContext> tasks = (List<TaskContext>) context.get(FacilioConstants.ContextNames.TASKS);
        List<TaskContext> oldTasks = (List<TaskContext>) context.get(FacilioConstants.ContextNames.OLD_TASKS);
        long modifiedTime = System.currentTimeMillis();
        long modifiedUser = AccountUtil.getCurrentUser().getId();

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
                TicketActivity activity = new TicketActivity();
                long parentTicketId = entry.getKey();
                activity.setTicketId(parentTicketId);
                activity.setModifiedTime(modifiedTime);
                activity.setModifiedBy(modifiedUser);
                activity.setOrgId(AccountUtil.getCurrentOrg().getOrgId());
                activity.setActivityType(EventType.READING_CORRECTION);

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
                unitProp.put("oldUnit", oldTaskContext.getReadingFieldUnitEnum().getDisplayName());
                if (taskContext.getReadingFieldUnitEnum() != null) {
                    unitProp.put("newUnit", taskContext.getReadingFieldUnitEnum().getDisplayName());
                } else {
                    unitProp.put("newUnit", oldTaskContext.getReadingFieldUnitEnum().getDisplayName());
                }

                info.put("updatedFields", updatedFields);
                activity.setInfo(info);
            }
        }

        return false;
    }
}
