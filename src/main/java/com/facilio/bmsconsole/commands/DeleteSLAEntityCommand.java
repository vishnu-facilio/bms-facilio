package com.facilio.bmsconsole.commands;

import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericDeleteRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.modules.ModuleFactory;
import org.apache.commons.chain.Context;

public class DeleteSLAEntityCommand extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception {
        Long id = (Long) context.get(FacilioConstants.ContextNames.ID);
        if (id != null && id > 0) {
            GenericDeleteRecordBuilder builder = new GenericDeleteRecordBuilder()
                    .table(ModuleFactory.getSLAEntityModule().getTableName())
                    .andCondition(CriteriaAPI.getIdCondition(id, ModuleFactory.getSLAEntityModule()));
            builder.delete();
        }
        return false;
    }
}
