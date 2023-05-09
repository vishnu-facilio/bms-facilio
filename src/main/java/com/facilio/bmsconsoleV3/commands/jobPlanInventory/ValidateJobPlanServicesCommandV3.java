package com.facilio.bmsconsoleV3.commands.jobPlanInventory;

import com.facilio.bmsconsoleV3.context.jobplan.JobPlanContext;
import com.facilio.bmsconsoleV3.context.jobplan.JobPlanServicesContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.v3.context.Constants;
import com.facilio.v3.exception.ErrorCode;
import com.facilio.v3.exception.RESTException;
import com.facilio.v3.util.V3Util;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;
import java.util.Map;


public class ValidateJobPlanServicesCommandV3 extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        String moduleName = Constants.getModuleName(context);
        Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);
        List<JobPlanServicesContext> jobPlanServices = recordMap.get(moduleName);
        if(CollectionUtils.isNotEmpty(jobPlanServices)){
            for(JobPlanServicesContext jobPlanService :jobPlanServices){
                if(jobPlanService.getJobPlan()!=null){
                    JobPlanContext jobPlan = (JobPlanContext) V3Util.getRecord(FacilioConstants.ContextNames.JOB_PLAN, jobPlanService.getJobPlan().getId(), null);
                    if(jobPlan.getJpStatusEnum()!=null && jobPlan.getJpStatusEnum().equals(JobPlanContext.JPStatus.ACTIVE)){
                        throw new RESTException(ErrorCode.VALIDATION_ERROR, "Cannot create/update after publishing Job Plan");
                    }
                }
            }
        }
        return false;
    }
}
