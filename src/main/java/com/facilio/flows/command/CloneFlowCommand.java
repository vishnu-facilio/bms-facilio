package com.facilio.flows.command;

import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.flows.chain.FlowChain;
import com.facilio.flows.context.FlowContext;
import com.facilio.flows.context.FlowTransitionContext;
import com.facilio.flows.util.FlowUtil;
import com.facilio.modules.FieldUtil;
import com.facilio.util.FacilioUtil;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CloneFlowCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        long flowId = (long) context.get(FacilioConstants.ContextNames.FLOW_ID);
        FlowContext flowContext = FlowUtil.getFlow(flowId);

        FacilioUtil.throwIllegalArgumentException(flowContext==null,"Invalid flow id");

        flowContext.setId(-1l);
        FlowUtil.addOrUpdateFlow(flowContext);

        List<FlowTransitionContext> oldFlowTransitions = FlowUtil.getFlowTransitionListWithExtendedConfig(flowId);

        if(CollectionUtils.isEmpty(oldFlowTransitions)){
            return false;
        }

        List<FlowTransitionContext> clonedFlowTransitions = new ArrayList<>();
        Map<Long,FlowTransitionContext> clonedFlowTransitionMap = new HashMap<>();


        long clonedFlowId  = flowContext.getId();


        Map<Long,FlowTransitionContext> oldTransitionIdVsClonedTransitionMap = new HashMap<>();
        Map<Long,List<Long>> oldParentTransitionIdVsChildIds = new HashMap<>();

        oldFlowTransitions.forEach(t->{oldParentTransitionIdVsChildIds.put(t.getId(),new ArrayList<>());});

        for(FlowTransitionContext currentTransition : oldFlowTransitions){
            long currentTransitionId = currentTransition.getId();
            long parentId = currentTransition.getConnectedFrom();

            if(parentId!=-1l){
                List<Long> childIds = oldParentTransitionIdVsChildIds.get(parentId);
                childIds.add(currentTransitionId);
            }
        }

        for(FlowTransitionContext oldFlowTransition : oldFlowTransitions){

            Long oldFlowTransitionId = oldFlowTransition.getId();
            Long oldConnectedFromId = oldFlowTransition.getConnectedFrom();
            oldFlowTransition.setId(-1l);
            oldFlowTransition.setConnectedFrom(-1l);
            oldFlowTransition.setFlowId(clonedFlowId);

            JSONObject transitionJSON = FieldUtil.getAsJSON(oldFlowTransition);
            FacilioChain chain = FlowChain.getInitAddOrUpdateFlowTransitionConfigChain();
            FacilioContext addTransitionContext = chain.getContext();
            addTransitionContext.put(FacilioConstants.ContextNames.FLOW_TRANSITION, transitionJSON);
            chain.execute();

            FlowTransitionContext clonedTransition = (FlowTransitionContext) addTransitionContext.get(FacilioConstants.ContextNames.FLOW_TRANSITION);

            clonedFlowTransitions.add(clonedTransition);
            clonedFlowTransitionMap.put(clonedTransition.getId(),clonedTransition);
            oldTransitionIdVsClonedTransitionMap.put(oldFlowTransitionId,clonedTransition);

            oldFlowTransition.setId(oldFlowTransitionId);
            oldFlowTransition.setConnectedFrom(oldConnectedFromId);
            oldFlowTransition.setFlowId(flowId);
        }

        for(Map.Entry<Long,FlowTransitionContext> entry: oldTransitionIdVsClonedTransitionMap.entrySet()){
            long oldTransitionId = entry.getKey();
            FlowTransitionContext clonedTransition = entry.getValue();
            long clonedParentId = clonedTransition.getId();

            List<Long> oldTransitionChildIDs = oldParentTransitionIdVsChildIds.get(oldTransitionId);

            for (Long oldChildId:oldTransitionChildIDs){
                FlowTransitionContext newClonedChildTransition = oldTransitionIdVsClonedTransitionMap.get(oldChildId);
                newClonedChildTransition.setConnectedFrom(clonedParentId);
            }

        }

        FlowUtil.updateFlowTransitionConnectedFrom(clonedFlowTransitions);

        context.put(FacilioConstants.ContextNames.FLOW,flowContext);
        return false;
    }
}
