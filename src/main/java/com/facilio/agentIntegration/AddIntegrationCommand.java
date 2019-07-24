package com.facilio.agentIntegration;

import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Context;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.facilio.agent.commands.AddLogChainCommand;
import com.facilio.bmsconsole.commands.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.fields.FacilioField;

public class AddIntegrationCommand extends FacilioCommand {
    private static final Logger LOGGER = LogManager.getLogger(AddLogChainCommand.class.getName());

    @Override
    public boolean executeCommand(Context context) throws Exception {
        FacilioModule module = (FacilioModule) context.get(FacilioConstants.ContextNames.MODULE);
        GenericInsertRecordBuilder insertRecordBuilder = new GenericInsertRecordBuilder().table(String.valueOf(context.get(FacilioConstants.ContextNames.TABLE_NAME)))
                .fields((List<FacilioField>) context.get(FacilioConstants.ContextNames.FIELDS));
        try {
            insertRecordBuilder.addRecords((List<Map<String, Object>>) context.get(FacilioConstants.ContextNames.TO_INSERT_MAP));
            insertRecordBuilder.save();
            return true;
        }catch (Exception e){
            LOGGER.info("Exception while adding agentIntegration ",e);
            return false;
        }
    }
}
