package com.facilio.bmsconsoleV3.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.workflow.rule.EventType;
import com.facilio.bmsconsoleV3.context.V3TransactionContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.ModuleBaseWithCustomFields;
import com.facilio.v3.context.Constants;
import com.facilio.ims.handler.AuditLogHandler;
import org.apache.commons.chain.Context;

import java.util.List;
import java.util.Map;

import static com.facilio.bmsconsole.util.AuditLogUtil.sendAuditLogs;

public class GetLogsForTransactionCommandV3 extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.TRANSACTION);
        Map<String, List<ModuleBaseWithCustomFields>> recordMap = Constants.getRecordMap(context);
        List<V3TransactionContext> recordList = Constants.getRecordListFromMap(recordMap, module.getName());
        EventType activityType = (EventType) context.get(FacilioConstants.ContextNames.EVENT_TYPE);
        for (V3TransactionContext record : recordList){

            if(activityType == EventType.CREATE){
                sendAuditLogs(new AuditLogHandler.AuditLogContext(String.format("Transaction with id #{%d} has been created ",record.getId()),
                        null,
                        AuditLogHandler.RecordType.MODULE,
                        "transaction", record.getId())
                        .setActionType(AuditLogHandler.ActionType.ADD)
                );
            }
           else if(activityType == EventType.EDIT){
                sendAuditLogs(new AuditLogHandler.AuditLogContext(String.format("Transaction with id #{%d} has been updated ",record.getId()),
                        null,
                        AuditLogHandler.RecordType.MODULE,
                        "transaction", record.getId())
                        .setActionType(AuditLogHandler.ActionType.UPDATE)
                );
            }
            else if(activityType == EventType.DELETE){
                sendAuditLogs(new AuditLogHandler.AuditLogContext(String.format("Transaction with id #{%d} has been deleted ",record.getId()),
                        null,
                        AuditLogHandler.RecordType.MODULE,
                        "transaction", record.getId())
                        .setActionType(AuditLogHandler.ActionType.DELETE)
                );
            }


        }
        return false;
    }
}
