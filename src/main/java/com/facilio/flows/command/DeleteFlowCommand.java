package com.facilio.flows.command;

import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericDeleteRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.modules.ModuleFactory;
import org.apache.commons.chain.Context;

public class DeleteFlowCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {

        long id = (long) context.get(FacilioConstants.ContextNames.ID);

        if (id <= 0){
            throw new IllegalArgumentException("Invalid Record Id");
        }

        GenericDeleteRecordBuilder builder = new GenericDeleteRecordBuilder()
                .table(ModuleFactory.getFlowModule().getTableName())
                .andCondition(CriteriaAPI.getIdCondition(id,ModuleFactory.getFlowModule()));
        builder.delete();

        return false;
    }
}
