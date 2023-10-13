package com.facilio.mailtracking.actions;

import lombok.Setter;

import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.mailtracking.commands.MailReadOnlyChainFactory;
import com.facilio.services.email.EmailClient;
import com.facilio.services.email.EmailFactory;
import com.facilio.v3.V3Action;
import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONObject;

@Setter
public class OutgoingMailAction extends V3Action {
    private static final long serialVersionUID = 1L;
    private long startId;
    private long endId;
    private boolean runAll = false;

    private long startTime;
    private long endTime = -1L;
    private String emailAddress;

    public String resendFailedMails() throws Exception {
        FacilioChain chain = MailReadOnlyChainFactory.runHistoricChain();
        FacilioContext context = chain.getContext();
        context.put("startId", startId);
        context.put("endId", endId);
        context.put("runAll", runAll);
        chain.execute();
        setData((JSONObject) context.get("response"));
        return V3Action.SUCCESS;
    }

    /***
     * Possible apis :
     * /api/v3/ogmail/history?startId=180&endId=184 - range of ids & recipient!=0
     * /api/v3/ogmail/history?startId=180&endId=184&runAll=true - range of ids, irrespective of recipient count
     * /api/v3/ogmail/history?startId=180 - single id & recipient!=0
     * /api/v3/ogmail/history?startId=180&runAll=true - single id, irrespective of recipient count
     * @return map of (rowCount, mailSent)
     * @throws Exception
     */
    public String apiStats() throws Exception {
        FacilioChain chain = MailReadOnlyChainFactory.getApiStatsChain();
        FacilioContext context = chain.getContext();
        context.put("startTime", startTime);
        context.put("endTime", endTime);
        chain.execute();
        setData((JSONObject) context.get("data"));
        return V3Action.SUCCESS;
    }

    public String getSuppressionInfo() {
        if(!StringUtils.isEmpty(emailAddress)) {
            EmailClient emailClient = EmailFactory.getEmailClient();
            for(String email : emailAddress.split(",")) {
                setData(email, emailClient.getSuppressionStatus(email));
            }
        } else {
            setData("message", "emailAddress is not found");
        }
        return V3Action.SUCCESS;
    }
}
