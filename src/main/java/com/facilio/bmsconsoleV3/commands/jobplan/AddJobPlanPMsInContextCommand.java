package com.facilio.bmsconsoleV3.commands.jobplan;

import com.facilio.bmsconsole.commands.FacilioCommand;
import com.facilio.bmsconsole.context.PreventiveMaintenance;
import com.facilio.bmsconsole.util.BmsJobUtil;
import com.facilio.bmsconsole.util.PreventiveMaintenanceAPI;
import com.facilio.bmsconsoleV3.context.jobplan.JobPlanContext;
import com.facilio.bmsconsoleV3.context.jobplan.PMJobPlanContextV3;
import com.facilio.bmsconsoleV3.util.JobPlanAPI;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.tasker.FacilioTimer;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;

import java.util.ArrayList;
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
            List<Long> pmIds = new ArrayList<>();
            for(JobPlanContext jp : jobPlans) {
                List<PMJobPlanContextV3> pmJobPlans = JobPlanAPI.getPMForJobPlanId(jp.getId());
                if(CollectionUtils.isNotEmpty(pmJobPlans)) {
                    for(PMJobPlanContextV3 pmJobPlan : pmJobPlans) {
                        if(!pmIds.contains(pmJobPlan.getPmId())) {
                            pmIds.add(pmJobPlan.getPmId());
                        }
                    }
                }
            }
            if(CollectionUtils.isNotEmpty(pmIds)) {

                int delay = 300;

                for (Long id: pmIds) {
                    FacilioTimer.deleteJob(id, "SchedulePMBackgroundJob");
                    BmsJobUtil.deleteJobWithProps(id, "ScheduleNewPM");
                    FacilioTimer.scheduleOneTimeJobWithDelay(id, "SchedulePMBackgroundJob", delay, "priority");
                }
            }

        }

            return false;
    }
}
