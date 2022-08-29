package com.facilio.bmsconsoleV3.commands.jobplan;

import com.facilio.bmsconsoleV3.context.jobplan.JobPlanContext;
import com.facilio.bmsconsoleV3.context.jobplan.JobPlanTaskSectionContext;
import com.facilio.bmsconsoleV3.context.jobplan.JobPlanTasksContext;
import com.facilio.command.FacilioCommand;
import com.facilio.modules.FieldUtil;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class SortJobPlanTaskSectionCommand extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception {
        String moduleName = Constants.getModuleName(context);
        Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);
        List<JobPlanContext> jobPlans = recordMap.get(moduleName);

        if(CollectionUtils.isNotEmpty(jobPlans)){
            for(JobPlanContext jobplan : jobPlans){
                if(jobplan.getJobplansection() != null && !jobplan.getJobplansection().isEmpty()){
                   List<JobPlanTaskSectionContext> jobPlanSectionList = jobplan.getJobplansection();
                   // sort jobPlan section based on SequenceNumber
                   List<JobPlanTaskSectionContext> sortedJPSectionList = jobPlanSectionList.stream()
                           .sorted(Comparator.comparing(JobPlanTaskSectionContext::getSequenceNumber))
                           .collect(Collectors.toList());
                   jobplan.setJobplansection(sortedJPSectionList);
                }
            }
        }
        return false;
    }
}
