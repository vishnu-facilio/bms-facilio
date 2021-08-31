package com.facilio.bmsconsole.commands;

import com.facilio.chain.FacilioChain;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.ModuleFactory;
import org.apache.commons.chain.Context;

import java.util.List;
import java.util.Map;

public class GetScatterGraphById extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        FacilioModule module = ModuleFactory.getScatterGraphLineModule();
        GenericSelectRecordBuilder select = new GenericSelectRecordBuilder()
                .select(FieldFactory.getScatterGraphFields())
                .table(module.getTableName())
                .andCustomWhere("ID = ?",context.get(FacilioConstants.ContextNames.SCATTER_GRAPH_ID));
        List<Map<String, Object>> result = select.get();
        context.put(FacilioConstants.ContextNames.SCATTER_GRAPH_RESULT, result.get(0));
        return false;
    }
}
