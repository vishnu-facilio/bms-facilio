package com.facilio.storm;

import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.v3.V3Action;
import lombok.Getter;
import lombok.Setter;
import org.json.simple.JSONObject;

@Getter
@Setter
public class StormAction extends V3Action {

    private Integer type;
    private JSONObject data;

    public String publishInstructionForStorm() throws Exception {
        try {
            FacilioChain runStormHistorical = TransactionChainFactory.initiateStormInstructionExecChain();
            FacilioContext context = runStormHistorical.getContext();
            context.put("type", getType());
            context.put("data", getData());
            runStormHistorical.execute();

            setData("success", "Instruction Processing has begun");
        }
        catch(Exception userException) {
            setData("Failed", userException.getMessage());
            throw userException;
        }

        return SUCCESS;
    }
}
