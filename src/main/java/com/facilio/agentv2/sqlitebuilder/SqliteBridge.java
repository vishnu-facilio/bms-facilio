package com.facilio.agentv2.sqlitebuilder;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.agentv2.AgentConstants;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.IOException;

public class SqliteBridge {

    private static final Logger LOGGER = LogManager.getLogger(SqliteBridge.class.getName());

    public static void main(String[] args) {
        System.out.println(File.separator);
    }

    public static long migrateAgentData(long agentId) throws Exception {
        LOGGER.info(" migration bridge is on");
            FacilioChain migrationChain = TransactionChainFactory.getagentV2DataSqlite();
            FacilioContext context = migrationChain.getContext();
            context.put(AgentConstants.AGENT_ID, agentId);
            migrationChain.execute();
            if (context.containsKey(AgentConstants.FILE_ID)) {
                return (long) context.get(AgentConstants.FILE_ID);
            }else {
                LOGGER.info(AgentConstants.FILE_ID+" is missing from context ");
            }
        return -1;
    }

    public static long migrateToNewAgent(Long agentId) throws Exception {
        LOGGER.info(" migrating to new Agent ");
         FacilioChain agentMigrationChain = TransactionChainFactory.getAgentMigrationChain();
         FacilioContext context = agentMigrationChain.getContext();
         context.put(AgentConstants.AGENT_ID,agentId);
         agentMigrationChain.execute();
        return -1;
    }

    public File getSqliteFile(long agentId) throws Exception {
        String path = System.getProperties().getProperty("java.io.tmpdir") + File.separator + AccountUtil.getCurrentOrg().getOrgId();
        File directory = new File(path);
        if (!directory.exists()) {
            if (!directory.mkdir()) {
                throw new Exception(" directory " + path + " cant be created");
            }
        }
        String fileName = agentId + ".db";
        File file = new File(path + File.separator + fileName);
        createNewFile(file);
        return file;
    }

    private void createNewFile(File file) throws IOException {
        if( ! file.createNewFile()){
            LOGGER.info(" file already present ");
            file.delete();
            file.createNewFile();
        }
    }
}

