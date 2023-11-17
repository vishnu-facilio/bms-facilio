package com.facilio.events.actions;

import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.context.WorkOrderContext;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.v3.V3Action;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class V3AlarmAction extends V3Action {
    private WorkOrderContext workorder;
    public String createWO() throws Exception {
        FacilioContext context = new FacilioContext();
        context.put(FacilioConstants.ContextNames.RECORD_ID, getId());
        context.put(FacilioConstants.ContextNames.WORK_ORDER, getWorkorder());

        FacilioChain c = TransactionChainFactory.getV2AlarmOccurrenceCreateWO();
        c.execute(context);

        WorkOrderContext wo = (WorkOrderContext) context.get(FacilioConstants.ContextNames.WORK_ORDER);
        long woId = -1;
        if (wo != null) {
            woId = wo.getId();
        }
        setData("woId", woId);

        return SUCCESS;
    }
}
