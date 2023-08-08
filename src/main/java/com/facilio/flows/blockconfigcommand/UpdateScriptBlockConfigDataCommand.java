package com.facilio.flows.blockconfigcommand;

import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.flows.context.ScriptFlowTransitionContext;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import org.apache.commons.chain.Context;

import java.util.Map;

public class UpdateScriptBlockConfigDataCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        ScriptFlowTransitionContext scriptFlowTransitionContext = (ScriptFlowTransitionContext) context.get(FacilioConstants.ContextNames.FLOW_TRANSITION);

        if(scriptFlowTransitionContext == null){
            return false;
        }

        long id = scriptFlowTransitionContext.getId();

        Map<String,Object> prop = FieldUtil.getAsProperties(scriptFlowTransitionContext);

        GenericUpdateRecordBuilder updateRecordBuilder = new GenericUpdateRecordBuilder()
                .fields(FieldFactory.getScriptBlockConfigDataFields())
                .table(ModuleFactory.getScriptBlockConfigDataModule().getTableName())
                .andCondition(CriteriaAPI.getIdCondition(id,ModuleFactory.getScriptBlockConfigDataModule()));

        updateRecordBuilder.update(prop);

        return false;
    }
}
