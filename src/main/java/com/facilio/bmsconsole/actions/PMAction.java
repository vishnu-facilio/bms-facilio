package com.facilio.bmsconsole.actions;

import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import org.apache.commons.chain.Chain;

public class PMAction extends FacilioAction {

    private long pmId = -1;

    private long endTime = -1;

    public String generateSchedule() throws Exception {
        FacilioContext context = new FacilioContext();
        context.put(FacilioConstants.ContextNames.PM_ID, getPmId());
        context.put(FacilioConstants.ContextNames.SCHEDULE_GENERATION_TIME, getEndTime());
        Chain chain = TransactionChainFactory.generateScheduleChain();
        chain.execute(context);
        return SUCCESS;
    }

    public long getPmId() {
        return pmId;
    }

    public void setPmId(long pmId) {
        this.pmId = pmId;
    }

    public long getEndTime() {
        return endTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }
}
