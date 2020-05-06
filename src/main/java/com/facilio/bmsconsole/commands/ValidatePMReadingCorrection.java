package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import com.facilio.bmsconsole.context.TaskContext;
import com.facilio.bmsconsole.context.WorkOrderContext;
import com.facilio.bmsconsole.util.PreventiveMaintenanceAPI;
import com.facilio.bmsconsole.util.TicketAPI;
import com.facilio.bmsconsole.util.WorkOrderAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleBaseWithCustomFields;
import com.facilio.time.DateTimeUtil;


/**
 * Validates PM Reading correction.
 * Currently validates - Time restriction for reading input
 */
public class ValidatePMReadingCorrection extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {

        List<TaskContext> tasks = (List<TaskContext>) context.get(FacilioConstants.ContextNames.TASKS);

        if(CollectionUtils.isEmpty(tasks)) {
            return false;
        }

        List<Long> recordIds = tasks.stream().map(ModuleBaseWithCustomFields::getId).collect(Collectors.toList());

        Map<Long, TaskContext> tasksAsMap = FieldUtil.getAsMap(tasks);

        Map<Long, TaskContext> oldTasks = TicketAPI.getTaskMap(recordIds);
        if (oldTasks != null && !oldTasks.isEmpty()) {
            Collection<TaskContext> values = oldTasks.values();
            context.put(FacilioConstants.ContextNames.OLD_TASKS, new ArrayList<>(values));
            context.put(FacilioConstants.ContextNames.TASK_MAP, oldTasks);
        }

        for(int i = 0; i < recordIds.size(); i++) {
            validateTaskInputTime(oldTasks.get(recordIds.get(i)), tasksAsMap.get(recordIds.get(i)));
        }

        return false;
    }

    private void validateTaskInputTime(TaskContext oldTask, TaskContext task) throws Exception {
        if (oldTask.getReadingFieldId() == -1) {
            return;
        }

        long parentTicketId = task.getParentTicketId();

        WorkOrderContext workOrder = WorkOrderAPI.getWorkOrder(parentTicketId);

        long nextExec = PreventiveMaintenanceAPI.getNextExecutionTimeForWorkOrder(parentTicketId);

        if (nextExec == -1L) {
            return;
        }

        long inputTime = task.getInputTime();
        long createdTime = workOrder.getCreatedTime();

        String createdTimeFmt = DateTimeUtil.getFormattedTime(createdTime, "MMM dd, h:mm a");
        String nextExecFmt = DateTimeUtil.getFormattedTime(nextExec, "MMM dd, h:mm a");

        if (inputTime < createdTime || inputTime >= nextExec) {
            throw new IllegalArgumentException("Remember to choose a time between " + createdTimeFmt + " and " + nextExecFmt);
        }
    }

}
