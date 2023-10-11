package com.facilio.bmsconsoleV3.commands;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.aws.util.FacilioProperties;
import com.facilio.bmsconsoleV3.context.SFG20JobPlan.SFG20SyncHistoryContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.tasker.FacilioTimer;
import org.apache.commons.chain.Context;

public class AddSFG20SyncHistoryCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        SFG20SyncHistoryContext syncHistoryContext = new SFG20SyncHistoryContext();
        syncHistoryContext.setSysCreatedTime(System.currentTimeMillis());
        syncHistoryContext.setSyncStartTime(System.currentTimeMillis());
        syncHistoryContext.setStatus(SFG20SyncHistoryContext.Status.IN_PROGRESS.getIndex());
       // syncHistoryContext.setSysCreatedBy(AccountUtil.getCurrentUser());
        FacilioModule syncHistoryModule = ModuleFactory.getSFG20SyncHistoryModule();
        GenericInsertRecordBuilder insert = new GenericInsertRecordBuilder()
                .table(syncHistoryModule.getTableName())
                .fields(FieldFactory.getSFG20SyncHistoryFields());
        long id = insert.insert(FieldUtil.getAsProperties(syncHistoryContext));
        syncHistoryContext.setId(id);
        context.put(FacilioConstants.ContextNames.SFG20.SYNC_HISTORY,syncHistoryContext);

        return false;
    }
}