package com.facilio.flows.command;

import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.BooleanOperators;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.db.criteria.operators.StringOperators;
import com.facilio.flows.context.FlowTransitionContext;
import com.facilio.flows.util.FlowUtil;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.util.FacilioUtil;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class UpdateConnection extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        long flowId = (long) context.get(FacilioConstants.ContextNames.FLOW_ID);
        FlowTransitionContext fromBlock = (FlowTransitionContext) context.get(FacilioConstants.ContextNames.FROM_BLOCK);
        FlowTransitionContext toBlock = (FlowTransitionContext) context.get(FacilioConstants.ContextNames.TO_BLOCK);
        String handlePosition = (String) context.get(FacilioConstants.ContextNames.HANDLE_POSITION);

        FacilioUtil.throwIllegalArgumentException(flowId == -1, "flowId cannot be empty");
        FacilioUtil.throwIllegalArgumentException(toBlock == null, "toBlock can not be empty for update transition connection");

        List<Long> flowTransitionIds = new ArrayList<>();
        if(fromBlock!=null){
            flowTransitionIds.add(fromBlock.getId());
        }

        flowTransitionIds.add(toBlock.getId());

        List<FlowTransitionContext> oldTransition = FlowUtil.getFlowTransition(flowTransitionIds, flowId);
        if (CollectionUtils.isEmpty(oldTransition) || oldTransition.size() != flowTransitionIds.size()) {
            throw new IllegalArgumentException("Some Blocks are doesn't belong to this Flow");
        }

        if(fromBlock==null){
            FlowTransitionContext oldStartBlock = getStartBlock(flowId);
            if(oldStartBlock != null){
                oldStartBlock.setIsStartBlock(false);
                updateIsStartBlock(oldStartBlock);
            }
            toBlock.setIsStartBlock(true);
            updateIsStartBlock(toBlock);

        }else{
            FacilioUtil.throwIllegalArgumentException(handlePosition == null,"handlePosition can not be empty for fromBlock");
            //set null for old connected block
            FlowTransitionContext oldToBlock = FlowUtil.getConnectedToBlock(fromBlock.getId(), flowId, handlePosition);
            FacilioUtil.throwIllegalArgumentException(oldToBlock == null, "There is no connection for fromBlock to change the connection");
            oldToBlock.setConnectedFrom(-99l);
            updateConnectedFrom(oldToBlock);
            //connect fromBlock to toBlock
            toBlock.setConnectedFrom(fromBlock.getId());
            updateConnectedFrom(toBlock);
        }



        return false;
    }
    private void updateConnectedFrom(FlowTransitionContext flowTransition) throws Exception {
        Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(FieldFactory.getFlowTransitionFields());
        GenericUpdateRecordBuilder builder = new GenericUpdateRecordBuilder()
                .table(ModuleFactory.getFlowTransitionModule().getTableName())
                .fields(Arrays.asList(fieldMap.get("connectedFrom")))
                .andCondition(CriteriaAPI.getIdCondition(flowTransition.getId(), ModuleFactory.getFlowTransitionModule()));
        builder.update(FieldUtil.getAsProperties(flowTransition));
    }
    private void updateIsStartBlock(FlowTransitionContext flowTransition) throws Exception {
        Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(FieldFactory.getFlowTransitionFields());
        GenericUpdateRecordBuilder builder = new GenericUpdateRecordBuilder()
                .table(ModuleFactory.getFlowTransitionModule().getTableName())
                .fields(Arrays.asList(fieldMap.get("isStartBlock")))
                .andCondition(CriteriaAPI.getIdCondition(flowTransition.getId(),ModuleFactory.getFlowTransitionModule()));
        builder.update(FieldUtil.getAsProperties(flowTransition));
    }
    private static FlowTransitionContext getStartBlock(Long flowId) throws Exception {
        GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
                .table(ModuleFactory.getFlowTransitionModule().getTableName())
                .select(FieldFactory.getFlowTransitionFields())
                .andCondition(CriteriaAPI.getCondition("FLOW_ID", "flowId", String.valueOf(flowId), NumberOperators.EQUALS))
                .andCondition(CriteriaAPI.getCondition("START_BLOCK", "isStartBlock","true", BooleanOperators.IS));
        FlowTransitionContext flowTransition = FieldUtil.getAsBeanFromMap(builder.fetchFirst(), FlowTransitionContext.class);
        return flowTransition;
    }

}