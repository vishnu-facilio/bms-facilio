package com.facilio.flows.command;

import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericDeleteRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.modules.ModuleFactory;
import org.apache.commons.chain.Context;

public class DeleteFlowTransitionCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {

        long id = (long) context.get(FacilioConstants.ContextNames.ID);

        if (id <= 0){
            throw new IllegalArgumentException("Invalid Id");
        }

        GenericDeleteRecordBuilder builder = new GenericDeleteRecordBuilder()
                .table(ModuleFactory.getFlowTransitionModule().getTableName())
                .andCondition(CriteriaAPI.getIdCondition(id,ModuleFactory.getFlowTransitionModule()));
        builder.delete();

        return false;
    }
}
