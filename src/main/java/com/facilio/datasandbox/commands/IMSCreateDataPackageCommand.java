package com.facilio.datasandbox.commands;

import com.facilio.datamigration.util.DataMigrationConstants;
import com.facilio.ims.handler.LongRunningTaskHandler;
import com.facilio.command.FacilioCommand;
import com.facilio.ims.endpoint.Messenger;
import org.apache.commons.chain.Context;
import com.facilio.fms.message.Message;
import com.facilio.time.DateTimeUtil;
import org.json.simple.JSONObject;

import java.time.ZoneId;

public class IMSCreateDataPackageCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        long sourceOrgId = (long) context.get(DataMigrationConstants.SOURCE_ORG_ID);

        JSONObject jsonObj = new JSONObject();
        jsonObj.put("startTime", DateTimeUtil.getDateTime(ZoneId.of("Asia/Kolkata")) + "");
        jsonObj.put("methodName", DataMigrationConstants.LongTaskBeanMethodNames.CREATE_DATA_PACKAGE);

        // Params for Creation Chain
        jsonObj.put(DataMigrationConstants.LIMIT, context.get(DataMigrationConstants.LIMIT));
        jsonObj.put(DataMigrationConstants.OFFSET, context.get(DataMigrationConstants.OFFSET));
        jsonObj.put(DataMigrationConstants.FILTERS, context.get(DataMigrationConstants.FILTERS));
        jsonObj.put(DataMigrationConstants.SOURCE_ORG_ID, context.get(DataMigrationConstants.SOURCE_ORG_ID));
        jsonObj.put(DataMigrationConstants.DATA_MIGRATION_ID, context.get(DataMigrationConstants.DATA_MIGRATION_ID));
        jsonObj.put(DataMigrationConstants.CREATE_FULL_PACKAGE, context.get(DataMigrationConstants.CREATE_FULL_PACKAGE));
        jsonObj.put(DataMigrationConstants.FETCH_DELETED_RECORDS, context.get(DataMigrationConstants.FETCH_DELETED_RECORDS));
        jsonObj.put(DataMigrationConstants.DATA_MIGRATION_MODULE_NAMES, context.get(DataMigrationConstants.DATA_MIGRATION_MODULE_NAMES));
        jsonObj.put(DataMigrationConstants.RUN_ONLY_FOR_MODULES, context.get(DataMigrationConstants.RUN_ONLY_FOR_MODULES));
        jsonObj.put(DataMigrationConstants.SKIP_DATA_MIGRATION_MODULE_NAMES, context.get(DataMigrationConstants.SKIP_DATA_MIGRATION_MODULE_NAMES));

        Message message = new Message().setKey(LongRunningTaskHandler.KEY + "/" + DateTimeUtil.getCurrenTime())
                .setOrgId(sourceOrgId)
                .setContent(jsonObj);

        Messenger.getMessenger().sendMessage(message);

        return false;
    }
}
