package com.facilio.agentv2.sqlitebuilder;

import com.facilio.agentv2.AgentConstants;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.io.File;

public class SqliteBridge {

    private static final Logger LOGGER = LogManager.getLogger(SqliteBridge.class.getName());

    public static void main(String[] args) {
        System.out.println(File.separator);
    }

    public static long migrateAgentData(long agentId) {
        LOGGER.info(" migration bridge is on");
        try {
            FacilioChain migrationChain = TransactionChainFactory.getagentDataMigrationChain();
            FacilioContext context = migrationChain.getContext();
            context.put(AgentConstants.AGENT_ID, agentId);
            migrationChain.execute();
            if (context.containsKey(AgentConstants.FIELD_ID)) {
                return (long) context.get(AgentConstants.FIELD_ID);
            }
        } catch (Exception e) {
            LOGGER.info("Exception occurred while migrating Agentdata for agentId " + agentId + "   ", e);
        }
        return -1;
    }

}

