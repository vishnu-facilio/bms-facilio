package com.facilio.flows.command;

import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.flows.context.FlowTransitionContext;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import org.apache.commons.chain.Context;

import java.util.Map;

public class UpdateFlowTransitionCommand extends  BaseFlowTransitionCommand{
    @Override
    public boolean executeCommand(Context context) throws Exception {
        FlowTransitionContext flowTransition = (FlowTransitionContext) context.get(FacilioConstants.ContextNames.FLOW_TRANSITION);

        if (flowTransition !=  null){

            validateFlowTransition(flowTransition);

            Map<String,Object> prop = FieldUtil.getAsProperties(flowTransition);

            if (flowTransition.getId() > 0){

                GenericUpdateRecordBuilder builder = new GenericUpdateRecordBuilder()
                        .table(ModuleFactory.getFlowTransitionModule().getTableName())
                        .fields(FieldFactory.getFlowTransitionFields())
                        .andCondition(CriteriaAPI.getIdCondition(flowTransition.getId(),ModuleFactory.getFlowTransitionModule()));

                builder.update(prop);

            }

        }

        context.put(FacilioConstants.ContextNames.FLOW_TRANSITION,flowTransition);

        return false;
    }
}
