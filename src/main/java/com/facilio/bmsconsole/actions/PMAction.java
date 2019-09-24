package com.facilio.bmsconsole.actions;

import com.facilio.bmsconsole.commands.ReadOnlyChainFactory;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;

public class PMAction extends FacilioAction {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private long pmId = -1;

    private long endTime = -1;

    private long workOrderId = -1;

    private long nextExecutionTime = -1;


    public String generateSchedule() throws Exception {
        FacilioContext context = new FacilioContext();
        context.put(FacilioConstants.ContextNames.PM_ID, getPmId());
        context.put(FacilioConstants.ContextNames.SCHEDULE_GENERATION_TIME, getEndTime());
        FacilioChain chain = TransactionChainFactory.generateScheduleChain();
        chain.execute(context);
        return SUCCESS;
    }

    public String getNextWorkOrderTime() throws Exception {
        FacilioContext context = new FacilioContext();
        context.put(FacilioConstants.ContextNames.RECORD_ID, getWorkOrderId());
        FacilioChain chain = ReadOnlyChainFactory.getNextWorkOrder();
        chain.execute(context);
        long nextExecutionTime = (long) context.get(FacilioConstants.ContextNames.RESULT);
        setResult("nextExecutionTime", nextExecutionTime);
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

    public long getWorkOrderId() {
        return workOrderId;
    }

    public void setWorkOrderId(long workOrderId) {
        this.workOrderId = workOrderId;
    }

    public long getNextExecutionTime() {
        return nextExecutionTime;
    }

    public void setNextExecutionTime(long nextExecutionTime) {
        this.nextExecutionTime = nextExecutionTime;
    }
}
