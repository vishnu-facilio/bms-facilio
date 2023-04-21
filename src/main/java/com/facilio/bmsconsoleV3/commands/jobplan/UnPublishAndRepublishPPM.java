package com.facilio.bmsconsoleV3.commands.jobplan;

import com.facilio.bmsconsoleV3.commands.TransactionChainFactoryV3;
import com.facilio.bmsconsoleV3.context.jobplan.JobPlanContext;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.plannedmaintenance.PlannedMaintenanceAPI;
import org.apache.commons.chain.Context;

import java.util.*;

public class UnPublishAndRepublishPPM extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        Map<String,Object> recordMap = (Map<String, Object>) context.get(FacilioConstants.ContextNames.RECORD_MAP);
        List<JobPlanContext> jobPlanContextList = new ArrayList<>();
        if(recordMap.containsKey(FacilioConstants.ContextNames.JOB_PLAN)){
            jobPlanContextList.add((JobPlanContext) recordMap.get(FacilioConstants.ContextNames.JOB_PLAN));
        }
        else if(recordMap.containsKey(FacilioConstants.ContextNames.JOB_PLAN_LIST)) {
            jobPlanContextList.addAll((List<JobPlanContext>) recordMap.get(FacilioConstants.ContextNames.JOB_PLAN_LIST));
        }
        if(jobPlanContextList == null || jobPlanContextList.isEmpty()){
            throw new IllegalArgumentException("No Job Plan Available");
        }
        Set<Map<String,Object>> plannedMaintenanceList = new HashSet<>();
        for(JobPlanContext jobPlanContext : jobPlanContextList){
             plannedMaintenanceList.addAll(PlannedMaintenanceAPI.getPPmDetailsAssociatedWithJobPlan(jobPlanContext.getId()));
        }
        if(plannedMaintenanceList != null && plannedMaintenanceList.size() > 0) {
            List<Long> pmIds = new ArrayList<>();
            for (Map<String, Object> plannedMaintenance : plannedMaintenanceList) {
                pmIds.add((Long)plannedMaintenance.get("pmId"));
            }
            FacilioChain bulkUnpublishChain = TransactionChainFactoryV3.getDeactivatePM();
            FacilioContext unPublishContext = bulkUnpublishChain.getContext();
            unPublishContext.put("pmIds",pmIds);
            bulkUnpublishChain.execute();
            FacilioChain publishPMChain = TransactionChainFactoryV3.getPublishPM();
            FacilioContext publishContext = publishPMChain.getContext();
            publishContext.put("pmIds", pmIds);
            publishPMChain.execute();
        }

        return false;
    }
}
