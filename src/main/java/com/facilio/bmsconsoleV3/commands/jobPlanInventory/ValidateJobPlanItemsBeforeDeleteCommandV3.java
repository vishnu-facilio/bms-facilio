package com.facilio.bmsconsoleV3.commands.jobPlanInventory;

import com.facilio.bmsconsoleV3.context.jobplan.JobPlanContext;
import com.facilio.bmsconsoleV3.context.jobplan.JobPlanItemsContext;
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

public class ValidateJobPlanItemsBeforeDeleteCommandV3  extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        String moduleName = Constants.getModuleName(context);
        List<Long> plannedItemIds = (List<Long>) context.get("recordIds");

        if (CollectionUtils.isNotEmpty(plannedItemIds)) {
            List<JobPlanItemsContext> plannedItems = V3RecordAPI.getRecordsList(moduleName, plannedItemIds, JobPlanItemsContext.class);
            for (JobPlanItemsContext plannedItem : plannedItems) {
                if(plannedItem.getJobPlan()!=null){
                    JobPlanContext jobPlan = (JobPlanContext) V3Util.getRecord(FacilioConstants.ContextNames.JOB_PLAN, plannedItem.getJobPlan().getId(), null);
                    if(jobPlan.getJpStatusEnum()!=null && jobPlan.getJpStatusEnum().equals(JobPlanContext.JPStatus.ACTIVE)){
                        throw new RESTException(ErrorCode.VALIDATION_ERROR, "Cannot delete after publishing Job Plan");
                    }
                }
            }
        }
        return false;

    }
}
