package com.facilio.bmsconsoleV3.plannedmaintenance.jobplan;

import com.facilio.bmsconsoleV3.context.V3TaskContext;
import com.facilio.bmsconsoleV3.context.V3WorkOrderContext;
import com.facilio.bmsconsoleV3.context.jobplan.JobPlanContext;
import com.facilio.bmsconsoleV3.util.JobPlanAPI;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.kafka.common.protocol.types.Field;


import java.util.*;
import java.util.stream.Collectors;

/**
 * FillTasksAndPrerequisitesCommand adds the task from following job plans
 * - JobPlan
 * - AdHoc JobPlan
 * - Prerequisites JobPlan
 */
public class FillTasksAndPrerequisitesCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {

        Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);
        List<V3WorkOrderContext> wos = recordMap.get(FacilioConstants.ContextNames.WORK_ORDER);
        V3WorkOrderContext workOrderContext = wos.get(0);

        // allTasks stores all the tasks. [ This has to be added to tasksString in V3WorkOrderContext ]
        LinkedHashMap<String, List<V3TaskContext>> allTasks = new LinkedHashMap<>();							// should change this model. 

        // Get the JobPlanContext
        JobPlanContext jobPlan = workOrderContext.getJobPlan(); // jobPlanContext will be available in workOrderContext
        JobPlanContext adhocJobPlan = workOrderContext.getAdhocJobPlan();
        JobPlanContext prerequisiteJobPlan = workOrderContext.getPrerequisiteJobPlan();

        if(jobPlan == null && adhocJobPlan == null && prerequisiteJobPlan == null){
            return false;
        }

        if (jobPlan != null) {
            Map<String, List<V3TaskContext>> jobPlanTasks = JobPlanAPI.getScopedTasksForWo(jobPlan, false, workOrderContext);
            allTasks.putAll(jobPlanTasks);
        }

        // Get the JobPlanContext for Ahoc tasks
        if (adhocJobPlan != null) {
            Map<String, List<V3TaskContext>> jobPlanTasks = JobPlanAPI.getScopedTasksForWo(adhocJobPlan, false, workOrderContext);
            allTasks.putAll(jobPlanTasks);
        }

        // Get the JobPlanContext for Prerequisite tasks
        if (prerequisiteJobPlan != null) {
            Map<String, List<V3TaskContext>> jobPlanPrerequisites = JobPlanAPI.getScopedTasksForWo(prerequisiteJobPlan, true, workOrderContext);
            context.put(FacilioConstants.ContextNames.PRE_REQUEST_MAP, jobPlanPrerequisites);
        }
        LinkedHashMap<String,List<V3TaskContext>> orderedTaskSection = new LinkedHashMap<>();
        if (allTasks.entrySet().iterator().hasNext()) {
            if(workOrderContext.getTasksString() != null && workOrderContext.getJobPlan() != null) {
                LinkedHashMap<String, List<V3TaskContext>> taskString = workOrderContext.getTasksString();
                List<String> sectionNameList = workOrderContext.getSectionNameList();
                for (String key : sectionNameList) {
                    if(taskString.get(key) != null) {
                        orderedTaskSection.put(key, taskString.get(key));
                    }
                }
            }
            allTasks.putAll(orderedTaskSection);
            LinkedHashMap<String,List<V3TaskContext>> orderedTaskMap = orderTaskString(allTasks);
            workOrderContext.setTasksString(orderedTaskMap);
        }
        return false;
    }
    LinkedHashMap<String,List<V3TaskContext>> orderTaskString(LinkedHashMap<String,List<V3TaskContext>> taskmap){
        if(taskmap == null){
            return null;
        }
        int sequence = 0;
        for(Map.Entry<String, List<V3TaskContext>> entry : taskmap.entrySet()){
            List<V3TaskContext> taskContextList = entry.getValue();
            for(V3TaskContext taskContext : taskContextList){
                if(sequence < taskContext.getSequence()){
                    sequence = taskContext.getSequence();
                }
                else {
                    sequence += 1;
                    taskContext.setSequence(sequence);
                    taskContext.setUniqueId(sequence);
                }
            }
        }
        return taskmap;
    }
}
