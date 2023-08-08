package com.facilio.flows.blockconfigcommand;

import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.flows.context.ScriptFlowTransitionContext;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import org.apache.commons.chain.Context;

import java.util.Map;

public class AddScriptBlockConfigDataCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        ScriptFlowTransitionContext scriptFlowTransitionContext = (ScriptFlowTransitionContext) context.get(FacilioConstants.ContextNames.FLOW_TRANSITION);

        if(scriptFlowTransitionContext == null){
            return false;
        }

        Map<String,Object> prop = FieldUtil.getAsProperties(scriptFlowTransitionContext);

        GenericInsertRecordBuilder insertRecordBuilder = new GenericInsertRecordBuilder()
                .fields(FieldFactory.getScriptBlockConfigDataFields())
                .table(ModuleFactory.getScriptBlockConfigDataModule().getTableName());

        insertRecordBuilder.insert(prop);
        return false;
    }
}
