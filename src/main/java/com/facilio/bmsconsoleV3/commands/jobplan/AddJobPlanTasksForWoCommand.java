package com.facilio.bmsconsoleV3.commands.jobplan;

import com.facilio.accounts.util.AccountUtil;
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
import org.apache.commons.collections4.MapUtils;

import java.util.List;
import java.util.Map;

public class AddJobPlanTasksForWoCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        if(AccountUtil.getCurrentOrg().getOrgId() == 173l) {
            WorkOrderContext workOrder = (WorkOrderContext) context.get(FacilioConstants.ContextNames.WORK_ORDER);
            if (workOrder.getPm() != null && workOrder.getResource() != null) {
                Map<String, List<TaskContext>> taskMap = (Map<String, List<TaskContext>>) context.get(FacilioConstants.ContextNames.TASK_MAP);
                Map<String, List<TaskContext>> jobPlanTasks = JobPlanAPI.getJobPlanTasksForWo(workOrder);
                if(MapUtils.isNotEmpty(jobPlanTasks)) {
                    taskMap.putAll(jobPlanTasks);
                }
            }
        }
        return false;
    }


}
