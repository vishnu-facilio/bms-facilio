package com.facilio.bmsconsoleV3.commands.jobplan;

import com.facilio.bmsconsole.commands.FacilioCommand;
import com.facilio.bmsconsole.context.TaskContext;
import com.facilio.bmsconsole.context.WorkOrderContext;
import com.facilio.bmsconsole.util.PreventiveMaintenanceAPI;
import com.facilio.bmsconsoleV3.context.jobplan.JobPlanContext;
import com.facilio.bmsconsoleV3.context.jobplan.JobPlanTaskSectionContext;
import com.facilio.bmsconsoleV3.context.jobplan.PMJobPlanContextV3;
import com.facilio.bmsconsoleV3.util.JobPlanAPI;
import com.facilio.constants.FacilioConstants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;
import java.util.Map;

public class AddJobPlanTasksForWoCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        WorkOrderContext workOrder = (WorkOrderContext) context.get(FacilioConstants.ContextNames.WORK_ORDER);
        if(workOrder.getPm() != null && workOrder.getResource() != null) {
            Map<String, List<TaskContext>> taskMap = (Map<String, List<TaskContext>>) context.get(FacilioConstants.ContextNames.TASK_MAP);
            JobPlanContext jobPlan = getJobPlanForWorkOrder(workOrder);
            if(jobPlan != null) {
                List<JobPlanTaskSectionContext> taskSections = jobPlan.getTaskSectionList();
                if (CollectionUtils.isNotEmpty(taskSections)) {
                    Map<String, List<TaskContext>> jobPlanTasks = JobPlanAPI.getTaskMapFromJobPlan(taskSections, workOrder.getResource().getId());
                    taskMap.putAll(jobPlanTasks);
                }
            }
        }

        return false;
    }

    private JobPlanContext getJobPlanForWorkOrder(WorkOrderContext wo) throws Exception {
        if(wo.getPm() != null && wo.getTrigger() != null) {
           return JobPlanAPI.getJobPlanForPMTrigger(wo.getTrigger().getId());
        }
        else if (wo.getJobPlan() != null) {
            return wo.getJobPlan();
        }
        return null;
     }
}
