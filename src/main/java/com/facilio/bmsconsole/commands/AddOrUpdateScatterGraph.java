package com.facilio.bmsconsole.commands;

import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import com.facilio.report.context.ScatterGraphLineContext;
import org.apache.commons.chain.Context;

import java.util.Map;

public class AddOrUpdateScatterGraph extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception {
        FacilioModule module = ModuleFactory.getScatterGraphLineModule();
        ScatterGraphLineContext graphLineContext = new ScatterGraphLineContext();

        graphLineContext.setGraphValue((String) context.get(FacilioConstants.ContextNames.SCATTER_GRAPH_VALUE));
        graphLineContext.setLabel((String) context.get(FacilioConstants.ContextNames.SCATTER_GRAPH_LABEL));

        if((Integer) context.get(FacilioConstants.ContextNames.SCATTER_GRAPH_ID) != 0){
            Map<String, Object> props = FieldUtil.getAsProperties(graphLineContext);
            GenericUpdateRecordBuilder updateRecordBuilder = new GenericUpdateRecordBuilder()
                    .table(module.getTableName())
                    .fields(FieldFactory.getScatterGraphFields())
                    .andCustomWhere("ID = ?", context.get(FacilioConstants.ContextNames.SCATTER_GRAPH_ID));
            long id = updateRecordBuilder.update(props);
            context.put(FacilioConstants.ContextNames.SCATTER_GRAPH_RESULT,id);
        } else {
            Map<String, Object> props = FieldUtil.getAsProperties(graphLineContext);
            GenericInsertRecordBuilder insert = new GenericInsertRecordBuilder()
                    .table(module.getTableName())
                    .fields(FieldFactory.getScatterGraphFields());
            long id = insert.insert(props);
            context.put(FacilioConstants.ContextNames.SCATTER_GRAPH_RESULT,id);
        }
        return false;
    }
}
