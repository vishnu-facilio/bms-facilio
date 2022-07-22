package com.facilio.plannedmaintenance;

import com.facilio.bmsconsole.context.*;
import com.facilio.bmsconsoleV3.context.V3TicketContext;
import com.facilio.bmsconsoleV3.context.V3WorkOrderContext;
import com.facilio.modules.FacilioStatus;
import com.facilio.modules.FieldUtil;
import org.apache.commons.chain.Context;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public abstract class ExecutorBase implements Executor {
    @Override
    public List<V3WorkOrderContext> execute(Context context, PlannedMaintenance plannedMaintenance, PMPlanner pmPlanner) throws Exception {
        List<Long> nextExecutionTimes = getNextExecutionTimes(context);
        FacilioStatus status = getStatus(context);
        List<V3WorkOrderContext> result = new ArrayList<>();

        for (long nextExecutionTime: nextExecutionTimes) {
            for (PMResourcePlanner pmResourcePlanner: pmPlanner.getResourcePlanners()) {
                //Cloning workorder
                Map<String, Object> asProperties = FieldUtil.getAsProperties(plannedMaintenance);
                V3WorkOrderContext workOrderCopy = FieldUtil.getAsBeanFromMap(asProperties, V3WorkOrderContext.class);

                if (pmPlanner.getPreReqJobPlan() != null) {
                    workOrderCopy.setPrerequisiteEnabled(true);
                    workOrderCopy.setPreRequestStatus(WorkOrderContext.PreRequisiteStatus.NOT_STARTED.getValue());
                } else {
                    workOrderCopy.setPreRequestStatus(WorkOrderContext.PreRequisiteStatus.COMPLETED.getValue());
                }

                workOrderCopy.setScheduledStart(nextExecutionTime);
                ResourceContext resourceContext = new ResourceContext();
                resourceContext.setId(resourceContext.getId());
                workOrderCopy.setResource(resourceContext);
                workOrderCopy.setPmId(pmResourcePlanner.getPmId());
                workOrderCopy.setTriggerId(pmPlanner.getTrigger().getId());
                workOrderCopy.setJobStatus(V3WorkOrderContext.JobsStatus.ACTIVE.getValue());
                workOrderCopy.setSourceType(V3TicketContext.SourceType.PREVENTIVE_MAINTENANCE.getIntVal());
                workOrderCopy.setStatus(status);
                result.add(workOrderCopy);
            }
        }

        return result;
    }

    protected abstract FacilioStatus getStatus(Context context) throws Exception;

    protected abstract List<Long> getNextExecutionTimes(Context context) throws Exception;
}
