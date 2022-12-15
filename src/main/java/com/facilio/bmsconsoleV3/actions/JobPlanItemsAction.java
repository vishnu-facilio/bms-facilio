package com.facilio.bmsconsoleV3.actions;

import com.facilio.bmsconsoleV3.commands.TransactionChainFactoryV3;
import com.facilio.bmsconsoleV3.context.jobplan.JobPlanItemsContext;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.FieldUtil;
import com.facilio.v3.V3Action;

import java.util.List;

public class JobPlanItemsAction extends V3Action {
    private Long jobPlanId;
    private List<Long> itemTypesIds;

    public Long getJobPlanId() {
        return jobPlanId;
    }

    public void setJobPlanId(Long jobPlanId) {
        this.jobPlanId = jobPlanId;
    }

    public List<Long> getItemTypesIds() {
        return itemTypesIds;
    }

    public void setItemTypesIds(List<Long> itemTypesIds) {
        this.itemTypesIds = itemTypesIds;
    }

    public String list() throws Exception {
        FacilioChain chain = TransactionChainFactoryV3.getUnsavedJobPlanItemsListChainV3();
        FacilioContext context = chain.getContext();
        context.put(FacilioConstants.ContextNames.JOB_PLAN,jobPlanId);
        context.put(FacilioConstants.ContextNames.ITEM_TYPES,itemTypesIds);
        chain.execute();
        setData(FacilioConstants.ContextNames.JOB_PLAN_ITEMS, FieldUtil.getAsJSONArray((List)context.get(FacilioConstants.ContextNames.JOB_PLAN_ITEMS), JobPlanItemsContext.class));
        return V3Action.SUCCESS;
    }
}
