package com.facilio.bmsconsoleV3.commands.jobplan;

import com.facilio.bmsconsole.commands.FacilioCommand;
import com.facilio.bmsconsoleV3.context.jobplan.JobPlanContext;
import com.facilio.bmsconsoleV3.util.JobPlanAPI;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;
import java.util.Map;

public class FillJobPlanDetailsCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        String moduleName = Constants.getModuleName(context);
        Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);
        List<JobPlanContext> jobPlans = recordMap.get(moduleName);
        if(CollectionUtils.isNotEmpty(jobPlans)){
            for(JobPlanContext jp : jobPlans){
                jp.setTaskSectionList(JobPlanAPI.setJobPlanDetails(jp.getId()));
            }
        }

        return false;
    }
}
