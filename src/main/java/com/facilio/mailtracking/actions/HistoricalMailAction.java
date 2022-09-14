package com.facilio.mailtracking.actions;

import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.mailtracking.commands.MailReadOnlyChainFactory;
import com.facilio.v3.V3Action;
import lombok.Setter;

@Setter
public class HistoricalMailAction extends V3Action {
    private static final long serialVersionUID = 1L;
    private long startId;
    private long endId;


    public String pushMailsToWms() throws Exception {
        FacilioChain chain = MailReadOnlyChainFactory.runHistoricChain();
        FacilioContext context = chain.getContext();
        context.put("startId", startId);
        context.put("endId", endId);
        chain.execute();
        return V3Action.SUCCESS;
    }
}
