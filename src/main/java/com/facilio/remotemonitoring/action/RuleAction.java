package com.facilio.remotemonitoring.action;

import com.facilio.bmsconsole.actions.FacilioAction;
import com.facilio.bmsconsoleV3.commands.TransactionChainFactoryV3;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.remotemonitoring.commands.updateAlaramFilterRuleStatusCommand;
import com.facilio.remotemonitoring.commands.updateFlaggedEventRuleStatuscommand;
import lombok.Getter;
import lombok.Setter;
import org.json.simple.JSONObject;

@Getter @Setter
public class RuleAction extends FacilioAction {

    private JSONObject data;
    public String updateAlarmFilterRuleStatus() throws Exception{
        FacilioChain chain = FacilioChain.getTransactionChain();
        chain.addCommand(new updateAlaramFilterRuleStatusCommand());
        FacilioContext context = chain.getContext();
        context.put("status",getData().get("status"));
        context.put("recordIds",getData().get("id"));
        chain.execute();
        return SUCCESS;
    }

    public String updateFlaggedEventRuleStatus() throws Exception{
        FacilioChain chain = FacilioChain.getTransactionChain();
        chain.addCommand(new updateFlaggedEventRuleStatuscommand());
        FacilioContext context = chain.getContext();
        context.put("status",getData().get("status"));
        context.put("recordIds",getData().get("id"));
        chain.execute();
        return SUCCESS;
    }

}