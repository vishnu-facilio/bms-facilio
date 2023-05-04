package com.facilio.flows.command;

import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.flows.context.FlowTransitionContext;
import com.facilio.flows.context.ParametersContext;
import com.facilio.flows.util.FlowUtil;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import org.apache.commons.chain.Context;

import java.util.Map;

public class AddOrUpdateFlowTransitionCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
      FlowTransitionContext flowTransition = (FlowTransitionContext) context.get(FacilioConstants.ContextNames.FLOW_TRANSITION);

      if (flowTransition !=  null){

          long flowId = flowTransition.getFlowId();;

          if (!(flowTransition.getIsStartBlock()) && !(FlowUtil.validateConnectedFrom(flowTransition.getConnectedFrom(),flowId))){
              throw new IllegalArgumentException("Block doesn't belong to this Flow");
          }

          if (flowTransition.getIsStartBlock() && FlowUtil.validateStartBlock(flowId)){
              throw new IllegalArgumentException("Start block is already declared");
          }

          Map<String, Object> props = FieldUtil.getAsProperties(flowTransition);

          if (flowTransition.getId() > 0){

              GenericUpdateRecordBuilder builder = new GenericUpdateRecordBuilder()
                      .table(ModuleFactory.getFlowTransitionModule().getTableName())
                      .fields(FieldFactory.getFlowTransitionFields())
                      .andCondition(CriteriaAPI.getIdCondition(flowTransition.getId(),ModuleFactory.getFlowTransitionModule()));

              builder.update(FieldUtil.getAsProperties(flowTransition));

          }else{

              GenericInsertRecordBuilder builder = new GenericInsertRecordBuilder()
                      .table(ModuleFactory.getFlowTransitionModule().getTableName())
                      .fields(FieldFactory.getFlowTransitionFields());


              builder.addRecord(props);
              builder.save();
          }

      }

      context.put(FacilioConstants.ContextNames.FLOW_TRANSITION,flowTransition);

      return false;
    }
}
