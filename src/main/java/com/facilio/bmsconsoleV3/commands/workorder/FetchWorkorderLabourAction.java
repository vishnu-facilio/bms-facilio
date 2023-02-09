 package com.facilio.bmsconsoleV3.commands.workorder;

import com.facilio.bmsconsoleV3.commands.TransactionChainFactoryV3;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.FieldUtil;
import com.facilio.v3.V3Action;

import java.util.List;

public class FetchWorkorderLabourAction extends V3Action {

    private static final long serialVersionUID = 1L;


    public Long getRecordId() {
        return recordId;
    }

    public void setRecordId(Long recordId) {
        this.recordId = recordId;
    }

    private Long  recordId;




    public Long getWorkOrderId() {
        return workOrderId;
    }

    public void setWorkOrderId(Long workOrderId) {
        this.workOrderId = workOrderId;
    }

    private Long workOrderId;


    public String actualLabour() throws Exception {
        FacilioChain chain = TransactionChainFactoryV3.getWorkorderActualsLabourChainV3();
        FacilioContext context = chain.getContext();
        context.put(FacilioConstants.ContextNames.RECORD_ID, recordId);
        context.put(FacilioConstants.ContextNames.MODULE_NAME, getModuleName());
        chain.execute();
        setData(FacilioConstants.ContextNames.WO_LABOUR, FieldUtil.getAsJSON(context.get(FacilioConstants.ContextNames.WO_LABOUR)));
        return V3Action.SUCCESS;
    }
    public String plannedLabour() throws Exception {
        FacilioChain chain = TransactionChainFactoryV3.getWorkorderPlannedLabourChainV3();
        FacilioContext context = chain.getContext();
        context.put(FacilioConstants.ContextNames.RECORD_ID, recordId);
        context.put(FacilioConstants.ContextNames.MODULE_NAME, getModuleName());
        chain.execute();
        setData(FacilioConstants.ContextNames.WorkOrderLabourPlan.WORKORDER_LABOUR_PLAN, context.get(FacilioConstants.ContextNames.WorkOrderLabourPlan.WORKORDER_LABOUR_PLAN));
        return V3Action.SUCCESS;
    }
}