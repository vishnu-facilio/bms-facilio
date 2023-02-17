package com.facilio.bmsconsoleV3.actions;

import com.facilio.bmsconsoleV3.commands.TransactionChainFactoryV3;
import com.facilio.chain.FacilioChain;
import com.facilio.v3.V3Action;

public class WorkflowExportAction extends V3Action {

    public String export () throws Exception {

        FacilioChain chain = TransactionChainFactoryV3.getWorkflowExportChain();
        chain.execute();

        setData("downloadURL",chain.getContext().get("downloadURL"));

        return SUCCESS;
    }
}
