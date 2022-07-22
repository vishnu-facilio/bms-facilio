package com.facilio.bmsconsoleV3.actions;

import com.facilio.bmsconsoleV3.commands.TransactionChainFactoryV3;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.v3.V3Action;
import lombok.Getter;
import lombok.Setter;

public class PlannedMaintenanceAction extends V3Action {

    @Getter
    @Setter
    private long plannerId;

    @Getter
    @Setter
    private long resourceId;

    @Getter
    @Setter
    private long pmId;

    public String executeNow() throws Exception {
        FacilioChain executeNowChain = TransactionChainFactoryV3.getExecuteNow();
        FacilioContext context = executeNowChain.getContext();
        context.put("plannerId", plannerId);
        context.put("resourceId", resourceId);
        executeNowChain.execute();
        return SUCCESS;
    }

    public String publishPM() throws Exception {
        FacilioChain publishPMChain = TransactionChainFactoryV3.getPublishPM();
        FacilioContext context = publishPMChain.getContext();
        context.put("pmId", pmId);
        publishPMChain.execute();
        return SUCCESS;
    }

}
