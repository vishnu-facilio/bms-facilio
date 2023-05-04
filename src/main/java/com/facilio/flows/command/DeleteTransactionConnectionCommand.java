package com.facilio.flows.command;

import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.flows.context.FlowTransitionContext;
import com.facilio.flows.util.FlowUtil;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import org.apache.commons.chain.Context;

public class DeleteTransactionConnectionCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {

        long id = (long) context.get(FacilioConstants.ContextNames.ID);

        if (id <= 0){
            return false;
        }

        FlowTransitionContext flowTransition = FlowUtil.getFlowTransition(id);

        flowTransition.setConnectedFrom(-99);
        flowTransition.setIsStartBlock(false);

        GenericUpdateRecordBuilder builder = new GenericUpdateRecordBuilder()
                .table(ModuleFactory.getFlowTransitionModule().getTableName())
                .fields(FieldFactory.getFlowTransitionFields())
                .andCondition(CriteriaAPI.getIdCondition(id,ModuleFactory.getFlowTransitionModule()));

        builder.update(FieldUtil.getAsProperties(flowTransition));

        return false;
    }
}
