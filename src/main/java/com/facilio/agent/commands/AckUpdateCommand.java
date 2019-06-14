package com.facilio.agent.commands;

import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.ModuleFactory;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.util.Map;

public class AckUpdateCommand implements Command {
    private static final Logger LOGGER = LogManager.getLogger(AckUpdateCommand.class.getName());

    @Override
    public boolean execute(Context context) throws Exception {
        FacilioModule module = ModuleFactory.getPublishMessageModule();
        if( !context.containsKey(FacilioConstants.ContextNames.TO_UPDATE_MAP) && !context.containsKey(FacilioConstants.ContextNames.ID))
        {
            return false;
        }

        int count = new GenericUpdateRecordBuilder()
                .table(module.getTableName())
                .fields(FieldFactory.getPublishMessageFields())
//                .andCondition(CriteriaAPI.getCurrentOrgIdCondition(module))
                .andCondition(CriteriaAPI.getIdCondition(Integer.parseInt(context.get(FacilioConstants.ContextNames.ID).toString()), module))
                .update((Map<String, Object>) context.get(FacilioConstants.ContextNames.TO_UPDATE_MAP))
                ;
        if(count > 0){
            return true;
        }
        return false;
    }
}
