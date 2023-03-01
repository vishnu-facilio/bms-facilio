package com.facilio.bmsconsoleV3.commands.jobplan;

import com.facilio.bmsconsoleV3.context.jobplan.JobPlanContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import org.apache.commons.chain.Context;

import java.util.List;
import java.util.Map;

public class SetJobPlanStatusCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        if(!context.containsKey(FacilioConstants.ContextNames.RECORD_MAP)){
            throw new IllegalArgumentException("No Job plans Available");
        }
        Map<String,Object> recordMap = (Map<String, Object>) context.get(FacilioConstants.ContextNames.RECORD_MAP);
        if(recordMap == null || recordMap.isEmpty()){
            throw new IllegalArgumentException("No Job plans Available");
        }
        List<JobPlanContext> jobPlanContextList = (List<JobPlanContext>) recordMap.get(FacilioConstants.ContextNames.JOB_PLAN_LIST);
        if(jobPlanContextList == null || jobPlanContextList.isEmpty()){
            throw new IllegalArgumentException("No Job plans Available");
        }
        JobPlanContext.JPStatus jobPlanStatus = (JobPlanContext.JPStatus) context.get(FacilioConstants.JOB_PLAN.JOB_PLAN_STATUS);
        for(JobPlanContext jobPlanContext : jobPlanContextList){
            jobPlanContext.setJpStatus(jobPlanStatus.getVal());
            jobPlanContext.setJpStatusEnum(jobPlanStatus);
        }
        recordMap.put(FacilioConstants.ContextNames.JOB_PLAN_LIST,jobPlanContextList);
        context.put(FacilioConstants.ContextNames.RECORD_MAP,recordMap);
        return false;
    }
}
