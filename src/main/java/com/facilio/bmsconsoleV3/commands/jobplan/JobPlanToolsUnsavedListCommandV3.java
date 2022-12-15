package com.facilio.bmsconsoleV3.commands.jobplan;

import com.facilio.bmsconsoleV3.context.inventory.V3ToolTypesContext;
import com.facilio.bmsconsoleV3.context.jobplan.JobPlanContext;
import com.facilio.bmsconsoleV3.context.jobplan.JobPlanToolsContext;
import com.facilio.bmsconsoleV3.util.V3RecordAPI;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import org.apache.commons.chain.Context;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JobPlanToolsUnsavedListCommandV3 extends FacilioCommand {
    public boolean executeCommand(Context context) throws Exception {
        List<Long> toolTypesIds = (List<Long>) context.get(FacilioConstants.ContextNames.TOOL_TYPES);
        Long jobPlanId = (Long) context.get(FacilioConstants.ContextNames.JOB_PLAN);
        if(toolTypesIds != null && jobPlanId != null) {
            JobPlanContext jobPlan = new JobPlanContext();
            jobPlan.setId(jobPlanId);
            List<V3ToolTypesContext> toolTypes = V3RecordAPI.getRecordsList(FacilioConstants.ContextNames.TOOL_TYPES,toolTypesIds, V3ToolTypesContext.class);
            List<JobPlanToolsContext> jobPlanTools = new ArrayList<>();
            for(V3ToolTypesContext toolType : toolTypes) {
                JobPlanToolsContext jobPlanTool = new JobPlanToolsContext();
                jobPlanTool.setToolType(toolType);
                jobPlanTool.setJobPlan(jobPlan);
                jobPlanTool.setQuantity(1.0);
                jobPlanTool.setDuration(1.0);
                jobPlanTools.add(jobPlanTool);
            }
            Map<String, List> recordMap = new HashMap<>();
            recordMap.put(FacilioConstants.ContextNames.JOB_PLAN_TOOLS,jobPlanTools);
            context.put(FacilioConstants.ContextNames.RECORD_MAP,recordMap);
            context.put(FacilioConstants.ContextNames.MODULE_NAME,FacilioConstants.ContextNames.JOB_PLAN_TOOLS);
            context.put(FacilioConstants.ContextNames.JOB_PLAN_TOOLS,jobPlanTools);
        }
        return false;
    }
}
