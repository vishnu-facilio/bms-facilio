package com.facilio.bmsconsoleV3.commands.jobplan;

import com.facilio.bmsconsoleV3.context.V3ServiceContext;
import com.facilio.bmsconsoleV3.context.inventory.V3ItemTypesContext;
import com.facilio.bmsconsoleV3.context.inventory.V3ToolTypesContext;
import com.facilio.bmsconsoleV3.context.jobplan.JobPlanContext;
import com.facilio.bmsconsoleV3.context.jobplan.JobPlanItemsContext;
import com.facilio.bmsconsoleV3.context.jobplan.JobPlanServicesContext;
import com.facilio.bmsconsoleV3.context.jobplan.JobPlanToolsContext;
import com.facilio.bmsconsoleV3.util.V3RecordAPI;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import org.apache.commons.chain.Context;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JobPlanServicesUnsavedListCommandV3 extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        List<Long> servicesIds = (List<Long>) context.get(FacilioConstants.ContextNames.SERVICE);
        Long jobPlanId = (Long) context.get(FacilioConstants.ContextNames.JOB_PLAN);
        if(servicesIds != null && jobPlanId != null) {
            JobPlanContext jobPlan = new JobPlanContext();
            jobPlan.setId(jobPlanId);
            List<V3ServiceContext> services = V3RecordAPI.getRecordsList(FacilioConstants.ContextNames.SERVICE,servicesIds, V3ServiceContext.class);
            List<JobPlanServicesContext> jobPlanServices = new ArrayList<>();
            for(V3ServiceContext service : services) {
                JobPlanServicesContext jobPlanService = new JobPlanServicesContext();
                jobPlanService.setService(service);
                jobPlanService.setQuantity(1.0);
                jobPlanService.setDuration(1.0);
                jobPlanServices.add(jobPlanService);
            }
            Map<String, List> recordMap = new HashMap<>();
            recordMap.put(FacilioConstants.ContextNames.JOB_PLAN_SERVICES,jobPlanServices);
            context.put(FacilioConstants.ContextNames.RECORD_MAP,recordMap);
            context.put(FacilioConstants.ContextNames.MODULE_NAME,FacilioConstants.ContextNames.JOB_PLAN_SERVICES);
            context.put(FacilioConstants.ContextNames.JOB_PLAN_SERVICES,jobPlanServices);
        }
        return false;
    }
}
