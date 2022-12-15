package com.facilio.bmsconsoleV3.actions;

import com.facilio.bmsconsoleV3.commands.TransactionChainFactoryV3;
import com.facilio.bmsconsoleV3.context.jobplan.JobPlanServicesContext;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.FieldUtil;
import com.facilio.v3.V3Action;

import java.util.List;

public class JobPlanServicesAction extends V3Action {
    private Long jobPlanId;
    private List<Long> servicesIds;

    public Long getJobPlanId() {
        return jobPlanId;
    }

    public void setJobPlanId(Long jobPlanId) {
        this.jobPlanId = jobPlanId;
    }

    public List<Long> getServicesIds() {
        return servicesIds;
    }

    public void setServicesIds(List<Long> servicesIds) {
        this.servicesIds = servicesIds;
    }

    public String list() throws Exception {
        FacilioChain chain = TransactionChainFactoryV3.getUnsavedJobPlanServicesListChainV3();
        FacilioContext context = chain.getContext();
        context.put(FacilioConstants.ContextNames.JOB_PLAN,jobPlanId);
        context.put(FacilioConstants.ContextNames.SERVICE,servicesIds);
        chain.execute();
        setData(FacilioConstants.ContextNames.JOB_PLAN_SERVICES, FieldUtil.getAsJSONArray((List)context.get(FacilioConstants.ContextNames.JOB_PLAN_SERVICES), JobPlanServicesContext.class));
        return V3Action.SUCCESS;
    }
}
