package com.facilio.bmsconsoleV3.commands.jobPlanInventory;

import com.facilio.bmsconsoleV3.context.jobplan.JobPlanContext;
import com.facilio.bmsconsoleV3.context.jobplan.JobPlanToolsContext;
import com.facilio.bmsconsoleV3.util.V3RecordAPI;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.v3.context.Constants;
import com.facilio.v3.exception.ErrorCode;
import com.facilio.v3.exception.RESTException;
import com.facilio.v3.util.V3Util;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;

public class ValidateJobPlanToolsBeforeDeleteCommandV3 extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        String moduleName = Constants.getModuleName(context);
        List<Long> plannedToolIds = (List<Long>) context.get("recordIds");

        if (CollectionUtils.isNotEmpty(plannedToolIds)) {
            List<JobPlanToolsContext> plannedTools = V3RecordAPI.getRecordsList(moduleName, plannedToolIds, JobPlanToolsContext.class);
            for (JobPlanToolsContext plannedTool : plannedTools) {
                if(plannedTool.getJobPlan()!=null){
                    JobPlanContext jobPlan = (JobPlanContext) V3Util.getRecord(FacilioConstants.ContextNames.JOB_PLAN, plannedTool.getJobPlan().getId(), null);
                    if(jobPlan.getJpStatusEnum()!=null && jobPlan.getJpStatusEnum().equals(JobPlanContext.JPStatus.ACTIVE)){
                        throw new RESTException(ErrorCode.VALIDATION_ERROR, "Cannot delete after publishing Job Plan");
                    }
                }
            }
        }
        return false;

    }
}
