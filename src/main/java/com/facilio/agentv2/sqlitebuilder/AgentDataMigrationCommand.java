package com.facilio.agentv2.sqlitebuilder;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.agent.controller.FacilioControllerType;
import com.facilio.agentv2.AgentConstants;
import com.facilio.agentv2.commands.AgentV2Command;
import com.facilio.agentv2.controller.ControllerApiV2;
import com.facilio.services.factory.FacilioFactory;
import com.facilio.services.filestore.FileStore;
import com.facilio.sqlUtils.sqllite.SQLiteUtil;
import org.apache.commons.chain.Context;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.io.File;
import java.util.Map;

public class AgentDataMigrationCommand extends AgentV2Command {
    private static final Logger LOGGER = LogManager.getLogger(SqliteBridge.class.getName());


    @Override
    public boolean executeCommand(Context context) throws Exception {
        if (containsCheck(AgentConstants.AGENT_ID, context)) {
            long agentId = Long.parseLong(String.valueOf(context.get(AgentConstants.AGENT_ID)));
            String path = System.getProperties().getProperty("java.io.tmpdir") + File.separator + AccountUtil.getCurrentOrg().getOrgId();
            File directory = new File(path);
            if (!directory.exists()) {
                if (!directory.mkdir()) {
                    throw new Exception(" directory " + path + " cant be created");
                }
            }
            String fileName = agentId + ".db";
            File file = new File(path + File.separator + fileName);
            LOGGER.info("migrated db file location->" + fileName);
            Map<Long, FacilioControllerType> controllerIdsType = ControllerApiV2.getControllerIds(agentId);
            LOGGER.info(" controllers " + controllerIdsType);
            SQLiteUtil.createAlternateConnection(file);
            ControllerMigrator.migrateControllers(agentId, controllerIdsType);
            PointMigrator.migratePoints(agentId, controllerIdsType);
            SQLiteUtil.closeConnection();
            FileStore fs = FacilioFactory.getFileStoreFromOrg(AccountUtil.getCurrentOrg().getOrgId());
            long fileId = fs.addFile(file.getName(), file, "application/vnd.sqlite3");
            LOGGER.info(" fileId is ->" + fileId);
            if (fileId > 0) {
                context.put(AgentConstants.FILE_ID, fileId);
            } else {
                LOGGER.info(" file id ->" + fileId);
            }
        }


        return false;
    }
}
