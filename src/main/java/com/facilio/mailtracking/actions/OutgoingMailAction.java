package com.facilio.mailtracking.actions;

import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.mailtracking.commands.MailReadOnlyChainFactory;
import com.facilio.v3.V3Action;
import lombok.Setter;
import org.json.simple.JSONObject;

@Setter
public class OutgoingMailAction extends V3Action {
    private static final long serialVersionUID = 1L;
    private long startId;
    private long endId;

    private long startTime;
    private long endTime = -1L;


    public String resendFailedMails() throws Exception {
        FacilioChain chain = MailReadOnlyChainFactory.runHistoricChain();
        FacilioContext context = chain.getContext();
        context.put("startId", startId);
        context.put("endId", endId);
        chain.execute();
        return V3Action.SUCCESS;
    }

    public String apiStats() throws Exception {
        FacilioChain chain = MailReadOnlyChainFactory.getApiStatsChain();
        FacilioContext context = chain.getContext();
        context.put("startTime", startTime);
        context.put("endTime", endTime);
        chain.execute();
        setData((JSONObject) context.get("data"));
        return V3Action.SUCCESS;
    }
}
