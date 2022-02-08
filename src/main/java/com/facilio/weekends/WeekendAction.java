package com.facilio.weekends;

import com.facilio.bmsconsole.actions.FacilioAction;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import org.apache.commons.chain.Context;

public class WeekendAction extends FacilioAction {

    private WeekendContext weekend;
    public WeekendContext getWeekend() { return weekend; }
    public void setWeekend(WeekendContext weekend) { this.weekend = weekend; }

    public String getWeekendList() throws Exception {
        FacilioChain weekendListChain = TransactionChainFactory.getWeekendListChain();
        Context context = weekendListChain.getContext();
        weekendListChain.execute();
        setResult("weekends", context.get(FacilioConstants.ContextNames.WEEKEND_LIST));
        return SUCCESS;
    }

    public String getWeekendData() throws Exception {
        FacilioChain weekendChain = TransactionChainFactory.getWeekendChain();
        Context context = weekendChain.getContext();
        context.put(FacilioConstants.ContextNames.WEEKEND, weekend);
        weekendChain.execute();
        setResult("weekend", context.get(FacilioConstants.ContextNames.WEEKEND));
        return SUCCESS;
    }

    public String addOrUpdateWeekend() throws Exception {
        FacilioChain chain = TransactionChainFactory.addOrUpdateWeekendChain();
        Context context = chain.getContext();
        context.put(FacilioConstants.ContextNames.WEEKEND, weekend);
        chain.execute();
        setResult("weekend", context.get(FacilioConstants.ContextNames.WEEKEND));
        return SUCCESS;
    }

    public String deleteWeekend() throws Exception {
        FacilioChain chain = TransactionChainFactory.deleteWeekendChain();
        Context context = chain.getContext();
        context.put(FacilioConstants.ContextNames.WEEKEND, weekend);
        chain.execute();
        setResult(FacilioConstants.ContextNames.RESULT, "success");
        return SUCCESS;
    }
}
