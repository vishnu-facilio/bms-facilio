package com.facilio.bmsconsoleV3.actions;

import com.facilio.bmsconsoleV3.commands.TransactionChainFactoryV3;
import com.facilio.bmsconsoleV3.context.jobplan.JobPlanToolsContext;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.FieldUtil;
import com.facilio.v3.V3Action;

import java.util.List;

public class JobPlanToolsAction extends V3Action {
    private Long jobPlanId;
    private List<Long> toolTypesIds;

    public Long getJobPlanId() {
        return jobPlanId;
    }

    public void setJobPlanId(Long jobPlanId) {
        this.jobPlanId = jobPlanId;
    }

    public List<Long> getToolTypesIds() {
        return toolTypesIds;
    }

    public void setToolTypesIds(List<Long> toolTypesIds) {
        this.toolTypesIds = toolTypesIds;
    }

    public String list() throws Exception {
        FacilioChain chain = TransactionChainFactoryV3.getUnsavedJobPlanToolsListChainV3();
        FacilioContext context = chain.getContext();
        context.put(FacilioConstants.ContextNames.JOB_PLAN,jobPlanId);
        context.put(FacilioConstants.ContextNames.TOOL_TYPES,toolTypesIds);
        chain.execute();
        setData(FacilioConstants.ContextNames.JOB_PLAN_TOOLS, FieldUtil.getAsJSONArray((List)context.get(FacilioConstants.ContextNames.JOB_PLAN_TOOLS), JobPlanToolsContext.class));
        return V3Action.SUCCESS;
    }
}
