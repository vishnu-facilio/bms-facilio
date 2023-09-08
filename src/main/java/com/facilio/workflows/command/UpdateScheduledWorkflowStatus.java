package com.facilio.workflows.command;

import com.facilio.bmsconsole.util.WorkflowRuleAPI;
import com.facilio.command.FacilioCommand;
import com.facilio.taskengine.job.JobContext;
import com.facilio.tasker.FacilioTimer;
import com.facilio.time.DateTimeUtil;
import com.facilio.workflows.context.ScheduledWorkflowContext;
import com.facilio.workflowv2.util.WorkflowV2Util;
import org.apache.commons.chain.Context;

public class UpdateScheduledWorkflowStatus extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        ScheduledWorkflowContext scheduledWorkflowContext = (ScheduledWorkflowContext) context.get(WorkflowV2Util.SCHEDULED_WORKFLOW_CONTEXT);
        WorkflowRuleAPI.updateScheduledWorkflowStatus(scheduledWorkflowContext);

        ScheduledWorkflowContext scheduledWorkflow = WorkflowRuleAPI.getScheduledWorkflowContext(scheduledWorkflowContext.getId(),null);

        if(scheduledWorkflow == null){
            return false;
        }

        JobContext job = FacilioTimer.getJob(scheduledWorkflow.getId(), WorkflowV2Util.SCHEDULED_WORKFLOW_JOB_NAME);
        boolean isActive = scheduledWorkflow.getIsActive();
        if(job != null){
            FacilioTimer.deleteJob(scheduledWorkflow.getId(), WorkflowV2Util.SCHEDULED_WORKFLOW_JOB_NAME);
        }

        if(isActive){
            long startTime = scheduledWorkflow.getStartTime();
            startTime = Math.max(startTime, DateTimeUtil.getCurrenTime());

            FacilioTimer.scheduleCalendarJob(scheduledWorkflow.getId(), WorkflowV2Util.SCHEDULED_WORKFLOW_JOB_NAME, startTime, scheduledWorkflow.getSchedule(), "facilio");
        }

        context.put(WorkflowV2Util.SCHEDULED_WORKFLOW_CONTEXT,scheduledWorkflow);
        return false;
    }
}
