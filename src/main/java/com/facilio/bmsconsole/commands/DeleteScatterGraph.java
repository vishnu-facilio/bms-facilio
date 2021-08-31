package com.facilio.bmsconsole.commands;

import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericDeleteRecordBuilder;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.ModuleFactory;
import org.apache.commons.chain.Context;

public class DeleteScatterGraph extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        FacilioModule module = ModuleFactory.getScatterGraphLineModule();
        GenericDeleteRecordBuilder deleteBuilder = new GenericDeleteRecordBuilder()
                .table(module.getTableName())
                .andCustomWhere("ID = ?", context.get(FacilioConstants.ContextNames.SCATTER_GRAPH_ID));
        deleteBuilder.delete();
        return false;
    }
}
