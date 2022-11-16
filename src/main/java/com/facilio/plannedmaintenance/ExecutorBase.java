package com.facilio.plannedmaintenance;

import com.facilio.accounts.util.AccountUtil;
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

        PMTriggerV2 trigger = (PMTriggerV2) context.get("trigger");

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

                ResourceContext resourceContext = new ResourceContext();
                resourceContext.setId(pmResourcePlanner.getResource().getId());
                resourceContext.setSiteId(pmResourcePlanner.getResource().getSiteId());
                workOrderCopy.setScheduledStart(nextExecutionTime * 1000);
                workOrderCopy.setResource(resourceContext);
                workOrderCopy.setPmV2(pmResourcePlanner.getPmId());
                workOrderCopy.setSiteId(resourceContext.getSiteId());
                if (trigger != null) {
                    workOrderCopy.setPmTriggerV2(trigger.getId());
                }
                workOrderCopy.setPmPlanner(pmPlanner.getId());
                workOrderCopy.setPmResourcePlanner(pmResourcePlanner.getId());
                workOrderCopy.setJobPlan(pmResourcePlanner.getJobPlan());
                workOrderCopy.setAdhocJobPlan(pmPlanner.getAdhocJobPlan());
                workOrderCopy.setPrerequisiteJobPlan(pmPlanner.getPreReqJobPlan());
                workOrderCopy.setCreatedTime(nextExecutionTime * 1000);

                workOrderCopy.setJobStatus(V3WorkOrderContext.JobsStatus.ACTIVE.getValue());
                workOrderCopy.setSourceType(V3TicketContext.SourceType.PREVENTIVE_MAINTENANCE.getIntVal());
                workOrderCopy.setStatus(status);
                workOrderCopy.setModuleState(null);
                workOrderCopy.setStateFlowId(-1);

                // Set Duration, DueDate, Estimated End
                workOrderCopy.setDuration(plannedMaintenance.getDueDuration());
                if(workOrderCopy.getDuration() != null) {
                    workOrderCopy.setDueDate(workOrderCopy.getCreatedTime()+(workOrderCopy.getDuration()*1000));
                }
                workOrderCopy.setEstimatedEnd(workOrderCopy.getDueDate());

                workOrderCopy.setCreatedBy(AccountUtil.getCurrentUser());
                workOrderCopy.setAssignedTo(pmResourcePlanner.getAssignedTo());

                result.add(workOrderCopy);
            }
        }

        return result;
    }

    protected abstract FacilioStatus getStatus(Context context) throws Exception;

    protected abstract List<Long> getNextExecutionTimes(Context context) throws Exception;
}
