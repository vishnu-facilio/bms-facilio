package com.facilio.bmsconsoleV3.commands.jobplan;

import com.facilio.bmsconsoleV3.context.inventory.V3ItemTypesContext;
import com.facilio.bmsconsoleV3.context.jobplan.JobPlanContext;
import com.facilio.bmsconsoleV3.context.jobplan.JobPlanItemsContext;
import com.facilio.bmsconsoleV3.util.V3RecordAPI;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import org.apache.commons.chain.Context;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JobPlanItemsUnsavedListCommandV3 extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        List<Long> itemTypesIds = (List<Long>) context.get(FacilioConstants.ContextNames.ITEM_TYPES);
        Long jobPlanId = (Long) context.get(FacilioConstants.ContextNames.JOB_PLAN);
        if(itemTypesIds != null && jobPlanId != null) {
            JobPlanContext jobPlan = new JobPlanContext();
            jobPlan.setId(jobPlanId);
            List<V3ItemTypesContext> itemTypes = V3RecordAPI.getRecordsList(FacilioConstants.ContextNames.ITEM_TYPES,itemTypesIds, V3ItemTypesContext.class);
            List<JobPlanItemsContext> jobPlanItems = new ArrayList<>();
            for(V3ItemTypesContext itemType : itemTypes) {
                JobPlanItemsContext jobPlanItem = new JobPlanItemsContext();
                jobPlanItem.setItemType(itemType);
                jobPlanItem.setQuantity(1.0);
                jobPlanItems.add(jobPlanItem);
            }
            Map<String, List> recordMap = new HashMap<>();
            recordMap.put(FacilioConstants.ContextNames.JOB_PLAN_ITEMS,jobPlanItems);
            context.put(FacilioConstants.ContextNames.RECORD_MAP,recordMap);
            context.put(FacilioConstants.ContextNames.MODULE_NAME,FacilioConstants.ContextNames.JOB_PLAN_ITEMS);
            context.put(FacilioConstants.ContextNames.JOB_PLAN_ITEMS,jobPlanItems);
        }
        return false;
    }
}
