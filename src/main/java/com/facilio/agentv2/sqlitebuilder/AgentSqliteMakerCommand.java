package com.facilio.agentv2.sqlitebuilder;

import com.facilio.agentv2.commands.AgentV2Command;
import org.apache.commons.chain.Context;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.io.File;
import java.util.Map;

public class AgentSqliteMakerCommand extends AgentV2Command {
    private static final Logger LOGGER = LogManager.getLogger(AgentSqliteMakerCommand.class.getName());


    @Override
    public boolean executeCommand(Context context) throws Exception {
       /* if (containsCheck(AgentConstants.AGENT_ID, context)) {
            long agentId = Long.parseLong(String.valueOf(context.get(AgentConstants.AGENT_ID)));
            SqliteBridge bridge = new SqliteBridge();
            File file = bridge.getSqliteFile(agentId);
            LOGGER.info("---------------SQLITE FILE LOADED-----------------");
            Map<Long, FacilioControllerType> controllerIdsType = AgentConstants.getControllerBean().getControllerIdsType(agentId);
            LOGGER.info(" controllers " + controllerIdsType);
            SQLiteUtil.createAlternateConnection(file);
            if (!controllerIdsType.isEmpty()) {
                ControllerMigrator.migrateControllers(agentId, controllerIdsType);
                //PointMigrator.migratePoints(agentId, controllerIdsType);
                SQLiteUtil.closeConnection();
                FileStore fs = FacilioFactory.getFileStoreFromOrg(AccountUtil.getCurrentOrg().getOrgId());
                long fileId = fs.addFile(file.getName(), file, "application/vnd.sqlite3");
                LOGGER.info(" fileId is ->" + fileId);
                context.put(AgentConstants.FILE_ID, fileId);
                if (fileId <= 0) {
                    throw new Exception(" fileId cant be less than 1, ->" + fileId);
                }

            } else {
                file.delete();
                throw new Exception(" no controllers found for agent " + agentId);
            }
        }*/
        return false;
    }


}
