package com.facilio.bmsconsoleV3.plannedmaintenance.jobplan;

import com.facilio.bmsconsoleV3.context.V3TaskContext;
import com.facilio.bmsconsoleV3.context.V3WorkOrderContext;
import com.facilio.bmsconsoleV3.context.jobplan.JobPlanContext;
import com.facilio.bmsconsoleV3.util.JobPlanAPI;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;


import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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
        LinkedHashMap<String, List<V3TaskContext>> allTasks = new LinkedHashMap<>();

        // Get the JobPlanContext
        JobPlanContext jobPlan = workOrderContext.getJobPlan(); // jobPlanContext will be available in workOrderContext
        JobPlanContext adhocJobPlan = workOrderContext.getAdhocJobPlan();
        JobPlanContext prerequisiteJobPlan = workOrderContext.getPrerequisiteJobPlan();

        if(jobPlan == null && adhocJobPlan == null && prerequisiteJobPlan == null){
            return false;
        }

        if (jobPlan != null) {
            Map<String, List<V3TaskContext>> jobPlanTasks = JobPlanAPI.getTasksForWo(jobPlan, false);
            allTasks.putAll(jobPlanTasks);
        }

        // Get the JobPlanContext for Ahoc tasks
        if (adhocJobPlan != null) {
            Map<String, List<V3TaskContext>> jobPlanTasks = JobPlanAPI.getTasksForWo(adhocJobPlan, false);
            allTasks.putAll(jobPlanTasks);
        }

        // Get the JobPlanContext for Prerequisite tasks
        if (prerequisiteJobPlan != null) {
            Map<String, List<V3TaskContext>> jobPlanPrerequisites = JobPlanAPI.getTasksForWo(prerequisiteJobPlan, true);
            context.put(FacilioConstants.ContextNames.PRE_REQUEST_MAP, jobPlanPrerequisites);
        }

        if (allTasks.entrySet().iterator().hasNext()) {
            workOrderContext.setTasksString(allTasks);
        }
        return false;
    }
}
