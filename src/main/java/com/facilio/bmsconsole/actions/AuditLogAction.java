package com.facilio.bmsconsole.actions;

import com.facilio.bmsconsole.commands.ReadOnlyChainFactory;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;

public class AuditLogAction extends FacilioAction {

    public String getAuditLogs() throws Exception {
        FacilioChain chain = ReadOnlyChainFactory.getAuditLogs();
        FacilioContext context = chain.getContext();
        chain.execute();

        setResult(FacilioConstants.ContextNames.AUDIT_LOGS, context.get(FacilioConstants.ContextNames.AUDIT_LOGS));
        return SUCCESS;
    }
}
