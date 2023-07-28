package com.facilio.readingrule.faulttowo;

import com.facilio.bmsconsole.context.TaskContext;
import com.facilio.bmsconsole.context.WorkOrderContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.readingrule.context.RuleAlarmDetails;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


public class FaultToWoActionsUtil {
    public static void addWODescFromRulePossibleCauses(WorkOrderContext workOrder, RuleAlarmDetails alarmDetails) throws Exception {
        List<String> possibleCauses = alarmDetails.getPossibleCauses();
        if(CollectionUtils.isNotEmpty(possibleCauses)) {
            StringBuilder desc = new StringBuilder();
            if (workOrder.getDescription() != null) {
                desc = new StringBuilder("\n" + workOrder.getDescription() + "\n");
            }
            String possibleCause = "\n  <b> Possible Causes </b> \n \n - ";

            String woDesc = possibleCauses.stream().filter(m -> !m.isEmpty()).collect(Collectors.joining("\n - "));
            desc = desc.append(possibleCause).append(woDesc);
            workOrder.setDescription(String.valueOf(desc));
        }
    }

    public static void addWoTaskToContext(WorkOrderContext workOrder, RuleAlarmDetails alarmDetails) {

        List<String> recommendations = alarmDetails.getRecommendations();
        List<TaskContext> woTaskList = new ArrayList<>();
        recommendations.stream().forEach(m -> {
            if (!m.isEmpty()) {
                TaskContext task = new TaskContext();
                task.setSubject(m);
                woTaskList.add(task);
            }
        });
        Map<String, List<TaskContext>> taskMap = Collections.singletonMap(FacilioConstants.ContextNames.DEFAULT_TASK_SECTION, woTaskList);
        workOrder.setTaskList(taskMap);
    }

}
