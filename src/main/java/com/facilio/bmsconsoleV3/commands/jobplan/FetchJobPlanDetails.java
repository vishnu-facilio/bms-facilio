package com.facilio.bmsconsoleV3.commands.jobplan;

import com.facilio.bmsconsoleV3.context.jobplan.JobPlanContext;
import com.facilio.bmsconsoleV3.context.jobplan.JobPlanTaskSectionContext;
import com.facilio.bmsconsoleV3.util.JobPlanAPI;
import com.facilio.bmsconsoleV3.util.V3RecordAPI;
import com.facilio.chain.FacilioContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import org.apache.commons.chain.Context;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FetchJobPlanDetails extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        List<Long> jobPlanIdList = new ArrayList<>();
        if(context.containsKey(FacilioConstants.JOB_PLAN.JOB_PLAN_ID)) {
            Long jobPlanId = (Long) context.get(FacilioConstants.JOB_PLAN.JOB_PLAN_ID);
            if (jobPlanId == null) {
                throw new IllegalArgumentException(jobPlanId+"Job Plan Can't Be Empty");
            }
            jobPlanIdList.add(jobPlanId);
        }
        else if(context.containsKey("jobPlanIds")){
            List<Long> jobPlanIds = (List<Long>) context.get("jobPlanIds");
            if(jobPlanIds == null || jobPlanIds.isEmpty()){
                throw new IllegalArgumentException("Must contain atLeast One JobPlan");
            }
            jobPlanIdList.addAll(jobPlanIds);
        }
        List<JobPlanContext> jobPlanContextList = new ArrayList<>();
        for(long jobPlanId : jobPlanIdList){
            JobPlanContext jobPlanContext = V3RecordAPI.getRecord(FacilioConstants.ContextNames.JOB_PLAN,jobPlanId, JobPlanContext.class);
            List<JobPlanTaskSectionContext> jobPlanTaskSectionContextList = JobPlanAPI.setJobPlanDetails(jobPlanId);
            if(jobPlanTaskSectionContextList == null || jobPlanTaskSectionContextList.isEmpty()){
                throw new IllegalArgumentException(jobPlanId+"Job Plan don't have any taskSection");
            }
            jobPlanContext.setJobplansection(jobPlanTaskSectionContextList);
            jobPlanContextList.add(jobPlanContext);
        }
        Map<String,List> recordMap = new HashMap<>();
        recordMap.put(FacilioConstants.ContextNames.JOB_PLAN_LIST,jobPlanContextList);
        context.put(FacilioConstants.ContextNames.RECORD_MAP,recordMap);
        return false;
    }
}
