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

public class IMSInstallDataPackageChain extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        long targetOrgId = (long) context.get(DataMigrationConstants.TARGET_ORG_ID);

        JSONObject jsonObj = new JSONObject();
        jsonObj.put("startTime", DateTimeUtil.getDateTime(ZoneId.of("Asia/Kolkata")) + "");
        jsonObj.put("methodName", DataMigrationConstants.LongTaskBeanMethodNames.INSTALL_DATA_PACKAGE);

        // Params for Installation Chain
        jsonObj.put(DataMigrationConstants.FILTERS, context.get(DataMigrationConstants.FILTERS));
        jsonObj.put(DataMigrationConstants.PACKAGE_ID, context.get(DataMigrationConstants.PACKAGE_ID));
        jsonObj.put(DataMigrationConstants.QUERY_LIMIT, context.get(DataMigrationConstants.QUERY_LIMIT));
        jsonObj.put(DataMigrationConstants.BUCKET_NAME, context.get(DataMigrationConstants.BUCKET_NAME));
        jsonObj.put(DataMigrationConstants.BUCKET_REGION, context.get(DataMigrationConstants.BUCKET_REGION));
        jsonObj.put(DataMigrationConstants.SOURCE_ORG_ID, context.get(DataMigrationConstants.SOURCE_ORG_ID));
        jsonObj.put(DataMigrationConstants.TARGET_ORG_ID, context.get(DataMigrationConstants.TARGET_ORG_ID));
        jsonObj.put(DataMigrationConstants.MODULE_SEQUENCE, context.get(DataMigrationConstants.MODULE_SEQUENCE));
        jsonObj.put(DataMigrationConstants.PACKAGE_FILE_URL, context.get(DataMigrationConstants.PACKAGE_FILE_URL));
        jsonObj.put(DataMigrationConstants.LOG_MODULES_LIST, context.get(DataMigrationConstants.LOG_MODULES_LIST));
        jsonObj.put(DataMigrationConstants.DATA_MIGRATION_ID, context.get(DataMigrationConstants.DATA_MIGRATION_ID));
        jsonObj.put(DataMigrationConstants.SKIP_DATA_MIGRATION_MODULE_NAMES, context.get(DataMigrationConstants.SKIP_DATA_MIGRATION_MODULE_NAMES));

        Message message = new Message().setKey(LongRunningTaskHandler.KEY + "/" + DateTimeUtil.getCurrenTime())
                .setOrgId(targetOrgId)
                .setContent(jsonObj);

        Messenger.getMessenger().sendMessage(message);

        return false;
    }
}
