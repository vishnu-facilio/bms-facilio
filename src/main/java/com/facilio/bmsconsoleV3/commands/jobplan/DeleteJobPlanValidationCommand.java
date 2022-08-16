package com.facilio.bmsconsoleV3.commands.jobplan;

import com.facilio.bmsconsoleV3.context.jobplan.JobPlanContext;
import com.facilio.bmsconsoleV3.util.V3RecordAPI;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.v3.exception.ErrorCode;
import com.facilio.v3.exception.RESTException;
import org.apache.commons.chain.Context;

import java.util.ArrayList;
import java.util.List;

/**
 * DeleteJobPlanValidationCommand Checks for JobPlan's status to allow Deletion.
 * - An active or disabled JobPlan cannot be deleted.
 */
public class DeleteJobPlanValidationCommand extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception {

        List<Long> recordIds = (List<Long>) context.get(FacilioConstants.ContextNames.RECORD_ID_LIST);
        if (recordIds != null && recordIds.size() > 0) {

            for (Long recordId : recordIds) {
                JobPlanContext jobPlanContext = V3RecordAPI.getRecord(FacilioConstants.ContextNames.JOB_PLAN, recordId);
                if(jobPlanContext == null){
                    throw new RESTException(ErrorCode.VALIDATION_ERROR, "No JobPlan with ID " + recordId);
                } else if (jobPlanContext.getIsActive() || jobPlanContext.getIsDisabled()) {
                    throw new RESTException(ErrorCode.VALIDATION_ERROR, "Active or Disabled JobPlan(s) cannot be deleted.");
                }
            }
        }
        return false;
    }
}
