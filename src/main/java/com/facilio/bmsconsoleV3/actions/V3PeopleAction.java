package com.facilio.bmsconsoleV3.actions;

import com.facilio.bmsconsoleV3.commands.TransactionChainFactoryV3;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.v3.V3Action;
import org.json.simple.JSONObject;

public class V3PeopleAction extends V3Action {

    public String fetchActivePeoples() throws Exception {

        FacilioChain chain = TransactionChainFactoryV3.fetchActivePeopleChain();
        FacilioContext context = chain.getContext();
        chain.execute();

        setData((JSONObject) context.get(FacilioConstants.ContextNames.DATA));
        return SUCCESS;
    }
}
