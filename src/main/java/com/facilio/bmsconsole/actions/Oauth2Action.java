package com.facilio.bmsconsole.actions;

import com.facilio.bmsconsole.commands.FacilioChainFactory;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;

public class Oauth2Action extends FacilioAction {

    public String enableOauth2() throws Exception {
        FacilioChain enableOauth2Chain = FacilioChainFactory.getEnableOauth2Chain();
        FacilioContext context = new FacilioContext();
        enableOauth2Chain.execute(context);
        return SUCCESS;
    }
}
