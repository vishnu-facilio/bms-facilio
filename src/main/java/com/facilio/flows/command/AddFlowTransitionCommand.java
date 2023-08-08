package com.facilio.flows.command;

import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.flows.context.FlowTransitionContext;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import org.apache.commons.chain.Context;

import java.util.Map;

public class AddFlowTransitionCommand extends BaseFlowTransitionCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        FlowTransitionContext flowTransition = (FlowTransitionContext) context.get(FacilioConstants.ContextNames.FLOW_TRANSITION);

        if (flowTransition !=  null){
            validateFlowTransition(flowTransition);

            Map<String,Object> prop = FieldUtil.getAsProperties(flowTransition);

            GenericInsertRecordBuilder builder = new GenericInsertRecordBuilder()
                    .table(ModuleFactory.getFlowTransitionModule().getTableName())
                    .fields(FieldFactory.getFlowTransitionFields());

            builder.addRecord(prop);
            builder.save();
            long id = (long)prop.get("id");
            flowTransition.setId(id);

        }

        context.put(FacilioConstants.ContextNames.FLOW_TRANSITION,flowTransition);

        return false;
    }
}
