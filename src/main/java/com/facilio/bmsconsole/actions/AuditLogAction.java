package com.facilio.bmsconsole.actions;

import com.facilio.bmsconsole.commands.ReadOnlyChainFactory;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.FieldUtil;
import com.facilio.ims.handler.AuditLogHandler;

import java.util.List;

public class AuditLogAction extends FacilioAction {

    public String getAuditLogs() throws Exception {
        FacilioChain chain = ReadOnlyChainFactory.getAuditLogs();
        FacilioContext context = chain.getContext();
        context.put(FacilioConstants.ContextNames.MODULE_NAME, FacilioConstants.ContextNames.AUDIT_LOGS);
        constructListContext(context);
        chain.execute();

        Object o = FieldUtil.getAsMapList((List) context.get(FacilioConstants.ContextNames.RECORD_LIST), AuditLogHandler.AuditLogContext.class);
        setResult(FacilioConstants.ContextNames.AUDIT_LOGS, o);
        return SUCCESS;
    }
}
