package com.facilio.bmsconsole.workflow.rule;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.util.StateFlowRulesAPI;
import com.facilio.bmsconsole.util.TicketAPI;
import com.facilio.cb.util.ChatBotWitAIUtil;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.*;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class GetOfflineStateTransitionCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
        Long recordId = (Long) context.get(FacilioConstants.ContextNames.ID);

        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(moduleName);

        if(module == null){
            throw new IllegalArgumentException("Invalid moduleName");
        }

        ModuleBaseWithCustomFields record = (ModuleBaseWithCustomFields) FieldUtil.getRecord(module,recordId);

        if(record == null){
            return false;
        }

        FacilioStatus moduleState = record.getModuleState();
        Long stateFlowId = record.getStateFlowId();

        if (moduleState == null || stateFlowId <= 0) {
            return false;
        }

        List<WorkflowRuleContext> stateTransitions = StateFlowRulesAPI.getAllStateTransitionList(stateFlowId);

        List<Long> visitedIds = new ArrayList<>();
        List<Long> stateIds = new ArrayList<>();
        HashMap<Long,List<HashMap<String,Object>>> offlineStateTransition = new HashMap<>();
        stateIds.add(moduleState.getId());

        HashMap<Long,List<WorkflowRuleContext>> stateTransitionMap = getStateTransitionMap(stateTransitions);

        offlineStateTransition = getOfflineStateTransitionMap(stateIds,stateTransitionMap,offlineStateTransition,visitedIds);

        context.put("offlineStateTransition",offlineStateTransition);

        return false;
    }

    public static HashMap<Long, List<HashMap<String,Object>>> getOfflineStateTransitionMap(List<Long> stateIds,HashMap<Long,List<WorkflowRuleContext>> stateTransitions,HashMap<Long,List<HashMap<String,Object>>> stack,List<Long> visitedIds) throws Exception {
        List<Long> unVisitedStateIds = new ArrayList<>();
        Iterator<Long> stateIdsItr = stateIds.iterator();

        if(stateTransitions == null){
            return null;
        }

        while(stateIdsItr.hasNext()) {
            Long stateId = stateIdsItr.next();
            List<WorkflowRuleContext> transitions = stateTransitions.get(stateId);

            if (CollectionUtils.isNotEmpty(transitions) && !visitedIds.contains(stateId)) {
                visitedIds.add(stateId);
                Iterator<WorkflowRuleContext> transitionItr = transitions.iterator();
                List<HashMap<String, Object>> states = new ArrayList<>();

                while (transitionItr.hasNext()) {
                    StateflowTransitionContext stateTransition = (StateflowTransitionContext) transitionItr.next();
                    if(stateTransition.getIsOffline() != null && stateTransition.getIsOffline()) {
                        HashMap<String, Object> recordMap = getRecordAsMap(stateTransition);
                        states.add(recordMap);
                        unVisitedStateIds.add(stateTransition.getToStateId());
                    }
                }

                if (CollectionUtils.isNotEmpty(states)) {
                    stack.put(stateId, states);
                }
            }

            stateIds.remove(stateId);
            if(CollectionUtils.isEmpty(stateIds)) break;
        }

        if(!unVisitedStateIds.isEmpty()) return getOfflineStateTransitionMap(unVisitedStateIds, stateTransitions, stack, visitedIds);

        return stack;
    }


    public static HashMap<String,Object> getRecordAsMap(StateflowTransitionContext record) throws Exception {
        HashMap<String,Object> result = new HashMap<>();
        FacilioStatus fromState = TicketAPI.getStatus(record.getFromStateId());
        FacilioStatus toState = TicketAPI.getStatus(record.getToStateId());

        HashMap<String,Object> fromStateMap = getOfflineStateTransitionMap(fromState);
        HashMap<String,Object> toStateMap = getOfflineStateTransitionMap(toState);

        result.put("fromState",fromStateMap);
        result.put("toState",toStateMap);
        result.put("isOffline",record.getIsOffline());
        result.put("id",record.getId());
        result.put("name",record.getName());

        return result;
    }

    public static HashMap<Long,List<WorkflowRuleContext>> getStateTransitionMap(List<WorkflowRuleContext> stateTransitions){
        if(CollectionUtils.isEmpty(stateTransitions)) {
            return null;
        }

        List<StateflowTransitionContext> stateTransitionList = new ArrayList<>();
        for(WorkflowRuleContext stateTransition : stateTransitions){
            stateTransitionList.add((StateflowTransitionContext) stateTransition);
        }

        HashMap<Long,List<WorkflowRuleContext>> stateTransitionMap = stateTransitionList.stream().collect(
                Collectors.groupingBy(StateflowTransitionContext::getFromStateId,HashMap::new,Collectors.toList()));

        return stateTransitionMap;
    }

    public static HashMap<String,Object> getOfflineStateTransitionMap(FacilioStatus state){
        HashMap<String,Object> stateMap = new HashMap<>();
        stateMap.put("id",state.getId());
        stateMap.put("primaryValue",state.getPrimaryValue());
        stateMap.put("displayName",state.getDisplayName());
        stateMap.put("recordLocked",state.isRecordLocked());
        stateMap.put("timerEnabled",state.isTimerEnabled());
        stateMap.put("type",state.getType().getStringVal());
        stateMap.put("typeCode",state.getType().getIntVal());

        return stateMap;
    }
}