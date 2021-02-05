package com.facilio.bmsconsoleV3.commands.jobplan;

import com.facilio.bmsconsole.commands.FacilioCommand;
import com.facilio.bmsconsole.context.PreventiveMaintenance;
import com.facilio.bmsconsole.util.PreventiveMaintenanceAPI;
import com.facilio.bmsconsoleV3.context.jobplan.JobPlanContext;
import com.facilio.bmsconsoleV3.context.jobplan.PMJobPlanContextV3;
import com.facilio.bmsconsoleV3.util.JobPlanAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AddJobPlanPMsInContextCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        String moduleName = Constants.getModuleName(context);
        Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);
        List<JobPlanContext> jobPlans = recordMap.get(moduleName);
        if(CollectionUtils.isNotEmpty(jobPlans)){
            Map<Long, PreventiveMaintenance> pms = new HashMap<>();
            for(JobPlanContext jp : jobPlans) {
                List<PMJobPlanContextV3> pmJobPlans = JobPlanAPI.getPMForJobPlanId(jp.getId());
                if(CollectionUtils.isNotEmpty(pmJobPlans)) {
                    for(PMJobPlanContextV3 pmJobPlan : pmJobPlans) {
                        if(!pms.containsKey(pmJobPlan.getPmId())) {
                            PreventiveMaintenance pm = PreventiveMaintenanceAPI.getActivePM(pmJobPlan.getPmId(), false);
                            pms.put(pmJobPlan.getPmId(), pm);
                        }
                    }
                }
            }
            if(MapUtils.isNotEmpty(pms)) {
                context.put(FacilioConstants.ContextNames.PREVENTIVE_MAINTENANCE_LIST, pms.values());
            }

        }

            return false;
    }
}
