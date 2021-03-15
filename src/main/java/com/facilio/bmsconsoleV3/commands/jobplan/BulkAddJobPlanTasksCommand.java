package com.facilio.bmsconsoleV3.commands.jobplan;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.commands.FacilioCommand;
import com.facilio.bmsconsole.context.BulkWorkOrderContext;
import com.facilio.bmsconsole.context.TaskContext;
import com.facilio.bmsconsole.context.WorkOrderContext;
import com.facilio.bmsconsoleV3.util.JobPlanAPI;
import com.facilio.constants.FacilioConstants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.MapUtils;

import java.util.List;
import java.util.Map;

public class BulkAddJobPlanTasksCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        if(AccountUtil.isFeatureEnabled(AccountUtil.FeatureLicense.JOB_PLAN)) {
            BulkWorkOrderContext bulkWorkOrderContext = (BulkWorkOrderContext) context.get(FacilioConstants.ContextNames.BULK_WORK_ORDER_CONTEXT);

            if (bulkWorkOrderContext.getTaskMaps() == null || bulkWorkOrderContext.getTaskMaps().isEmpty()) {
                return false;
            }

            List<WorkOrderContext> workOrderContexts = bulkWorkOrderContext.getWorkOrderContexts();
            for(int i = 0; i < workOrderContexts.size(); i++){
                WorkOrderContext wo = workOrderContexts.get(i);
                if (wo.getPm() != null && wo.getResource() != null) {
                    Map<String, List<TaskContext>> taskMap = bulkWorkOrderContext.getTaskMaps().get(i);
                    Map<String, List<TaskContext>> jobPlanTasks = JobPlanAPI.getJobPlanTasksForWo(wo);
                    if(MapUtils.isNotEmpty(jobPlanTasks)) {
                        taskMap.putAll(jobPlanTasks);
                    }
                }
            }
         }
        return false;
    }
}
