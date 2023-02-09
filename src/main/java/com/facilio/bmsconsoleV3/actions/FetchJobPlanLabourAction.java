package com.facilio.bmsconsoleV3.actions;

import com.facilio.bmsconsoleV3.commands.TransactionChainFactoryV3;

import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.FieldUtil;
import com.facilio.v3.V3Action;

public class FetchJobPlanLabourAction extends V3Action {

    private static final long serialVersionUID = 1L;


    public Long getRecordId() {
        return recordId;
    }

    public void setRecordId(Long recordId) {
        this.recordId = recordId;
    }

    private Long  recordId;


    public String jobPlanLabour() throws Exception {
        FacilioChain chain = TransactionChainFactoryV3.getJobPlanLabourChainV3();
        FacilioContext context = chain.getContext();
        context.put(FacilioConstants.ContextNames.RECORD_ID, recordId);
        context.put(FacilioConstants.ContextNames.MODULE_NAME, getModuleName());
        chain.execute();
        setData(FacilioConstants.ContextNames.JOB_PLAN_LABOURS,  context.get(FacilioConstants.ContextNames.JOB_PLAN_LABOURS));
        return V3Action.SUCCESS;
    }
}
