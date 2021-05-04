package com.facilio.bmsconsole.commands;

import com.facilio.bmsconsole.util.AuditLogUtil;
import com.facilio.constants.FacilioConstants;
import com.facilio.wmsv2.handler.AuditLogHandler;
import org.apache.commons.chain.Context;

import java.util.List;

public class GetAuditLogsCommand extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception {
        List<AuditLogHandler.AuditLogContext> auditLogs =
                AuditLogUtil.getAuditLogs();

        context.put(FacilioConstants.ContextNames.AUDIT_LOGS, auditLogs);
        return false;
    }
}
