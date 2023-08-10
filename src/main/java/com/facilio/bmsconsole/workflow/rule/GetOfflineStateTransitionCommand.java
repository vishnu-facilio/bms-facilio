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
import org.apache.commons.collections4.MapUtils;

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
        long stateFlowId = record.getStateFlowId();

        if (moduleState == null || stateFlowId <= 0) {
            return false;
        }

        List<WorkflowRuleContext> stateTransitions = StateFlowRulesAPI.getAllStateTransitionList(stateFlowId);
        List<Long> stateIds = new ArrayList<>();
        stateIds.add(moduleState.getId());

        HashMap<Long,List<WorkflowRuleContext>> stateTransitionMap = getStateTransitionMap(stateTransitions);

        HashMap<Long,List<HashMap<String,Object>>> offlineStateTransition = getOfflineStateTransitionMap(moduleState.getId(), stateTransitionMap, new HashSet<>());

        context.put("offlineStateTransition",offlineStateTransition);

        return false;
    }

    public static HashMap<Long, List<HashMap<String,Object>>> getOfflineStateTransitionMap(long startStateId, HashMap<Long,List<WorkflowRuleContext>> stateTransitions, Set<Long> visitedIds) throws Exception {
        HashMap<Long,List<HashMap<String,Object>>> stack = new HashMap<>();

        if (visitedIds.contains(startStateId)) {
            return stack;
        }
        visitedIds.add(startStateId);

        final List<WorkflowRuleContext> workflowRuleContexts = stateTransitions.get(startStateId);
        if (CollectionUtils.isEmpty(workflowRuleContexts)) {
            return stack;
        }

        for (WorkflowRuleContext workflowRule : workflowRuleContexts) {
            StateflowTransitionContext transition = (StateflowTransitionContext) workflowRule;
            if(transition.getIsOffline() != null && transition.getIsOffline()) {
                long fromStateId = transition.getFromStateId();
                List<HashMap<String, Object>> offlineStateTransition = stack.get(fromStateId);
                if (offlineStateTransition == null) {
                    offlineStateTransition = new ArrayList<>();
                    stack.put(fromStateId, offlineStateTransition);
                }
                HashMap<String, Object> recordMap = getRecordAsMap(transition);
                offlineStateTransition.add(recordMap);

                stack.putAll(getOfflineStateTransitionMap(transition.getToStateId(), stateTransitions, visitedIds));
            }
        }

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

        return stateTransitionList.stream().collect(
                Collectors.groupingBy(StateflowTransitionContext::getFromStateId,HashMap::new,Collectors.toList()));
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