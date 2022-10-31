package com.facilio.bmsconsoleV3.commands.jobplan;

import com.facilio.bmsconsole.context.PlannedMaintenance;
import com.facilio.bmsconsole.context.PreventiveMaintenance;
import com.facilio.bmsconsoleV3.context.jobplan.JobPlanContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;

import java.util.List;
import java.util.Map;

import com.facilio.v3.exception.ErrorCode;
import com.facilio.v3.exception.RESTException;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

public class ValidationForJobPlanCategory extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception {

        List<JobPlanContext> category = (List<JobPlanContext>) (((Map<String,Object>)context.get(FacilioConstants.ContextNames.RECORD_MAP)).get("jobplan"));
        if(CollectionUtils.isNotEmpty(category)) {
            for (JobPlanContext section : category) {
                // case for import - ad hoc tasks
                if (section.getJobPlanCategory() == null) {
                    continue;
                }
                if(section.getJobPlanCategory() == PreventiveMaintenance.PMAssignmentType.SPACE_CATEGORY.getVal()
                            && section.getSpaceCategory() == null ){
                        throw new RESTException(ErrorCode.VALIDATION_ERROR, "Space Category should not be null ");
                    }
                    else if(section.getJobPlanCategory() == PreventiveMaintenance.PMAssignmentType.ASSET_CATEGORY.getVal()
                            && section.getAssetCategory() == null){
                        throw new RESTException(ErrorCode.VALIDATION_ERROR, "Asset Category  should not be null ");
                    }
            }
        }
        return false;
    }
}

