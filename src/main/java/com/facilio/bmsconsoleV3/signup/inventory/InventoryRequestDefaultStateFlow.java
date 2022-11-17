package com.facilio.bmsconsoleV3.signup.inventory;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.util.TicketAPI;
import com.facilio.bmsconsole.workflow.rule.*;
import com.facilio.bmsconsoleV3.context.inventory.V3InventoryRequestContext;
import com.facilio.bmsconsoleV3.signup.SignUpData;
import com.facilio.chain.FacilioChain;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.*;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FacilioStatus;

public class InventoryRequestDefaultStateFlow extends SignUpData {

    @Override
    public void addData() throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule inventoryRequestModule = modBean.getModule(FacilioConstants.ContextNames.INVENTORY_REQUEST);
        if(inventoryRequestModule !=null && inventoryRequestModule.getModuleId() > 0){
            String pending = addStateForInventoryRequestStateFlow("Pending");
            String partiallyReserved = addStateForInventoryRequestStateFlow("Partially Reserved");
            String fullyReserved = addStateForInventoryRequestStateFlow("Fully Reserved");
            long stateFlowId = addInventoryRequestStateFlow(pending);
            if(stateFlowId>0){
                // transition from pending to partiallyReserved
                Criteria partiallyReservedCriteria = new Criteria();
                partiallyReservedCriteria.addAndCondition(CriteriaAPI.getCondition("Inventory_Requests.RESERVATION_STATUS", "inventoryRequestReservationStatus",String.valueOf(V3InventoryRequestContext.InventoryRequestReservationStatus.PARTIALLY_RESERVED.getIndex()), EnumOperators.IS));
                addStateFlowTransitions(inventoryRequestModule, stateFlowId, pending, partiallyReserved, "Partially Reserve", partiallyReservedCriteria);

                //transition from pending to fullyReserved
                Criteria pendingTofullyReservedCriteria = new Criteria();
                pendingTofullyReservedCriteria.addAndCondition(CriteriaAPI.getCondition("Inventory_Requests.RESERVATION_STATUS", "inventoryRequestReservationStatus",String.valueOf(V3InventoryRequestContext.InventoryRequestReservationStatus.FULLY_RESERVED.getIndex()), EnumOperators.IS));
                addStateFlowTransitions(inventoryRequestModule, stateFlowId, pending, fullyReserved, "Pending To Fully Reserve", pendingTofullyReservedCriteria);

                // transition from partiallyReserved to fullyReserved
                Criteria fullyReservedCriteria = new Criteria();
                fullyReservedCriteria.addAndCondition(CriteriaAPI.getCondition("Inventory_Requests.RESERVATION_STATUS", "inventoryRequestReservationStatus",String.valueOf(V3InventoryRequestContext.InventoryRequestReservationStatus.FULLY_RESERVED.getIndex()), EnumOperators.IS));
                addStateFlowTransitions(inventoryRequestModule, stateFlowId, partiallyReserved, fullyReserved, "Fully Reserve", fullyReservedCriteria);

                // transition from fullyReserved to partiallyReserved
                Criteria fullyReservedToPartiallyReservedCriteria = new Criteria();
                fullyReservedToPartiallyReservedCriteria.addAndCondition(CriteriaAPI.getCondition("Inventory_Requests.RESERVATION_STATUS", "inventoryRequestReservationStatus",String.valueOf(V3InventoryRequestContext.InventoryRequestReservationStatus.PARTIALLY_RESERVED.getIndex()), EnumOperators.IS));
                addStateFlowTransitions(inventoryRequestModule, stateFlowId, fullyReserved, partiallyReserved, "Fully To Partially Reserve", fullyReservedToPartiallyReservedCriteria);
            }
        }
    }

    private String addStateForInventoryRequestStateFlow(String stateDisplayName) throws Exception {
        FacilioStatus state = new FacilioStatus();
        state.setDisplayName(stateDisplayName);
        state.setTypeCode(1);
        state.setRecordLocked(false);
        state.setTimerEnabled(false);
        state.setRequestedState(false);
        FacilioChain chain = TransactionChainFactory.getAddOrUpdateTicketStatusChain();
        chain.getContext().put(FacilioConstants.ContextNames.TICKET_STATUS,state);
        chain.getContext().put(FacilioConstants.ContextNames.PARENT_MODULE,FacilioConstants.ContextNames.INVENTORY_REQUEST);
        chain.execute();
        return state.getStatus();
    }

    private long addInventoryRequestStateFlow(String defaultState) throws Exception {
        StateFlowRuleContext stateFlow = new StateFlowRuleContext();
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioStatus defaultStatus = TicketAPI.getStatus(modBean.getModule(FacilioConstants.ContextNames.INVENTORY_REQUEST) ,defaultState);
        if(defaultStatus!=null){
            stateFlow.setName("Default Stateflow");
            stateFlow.setModuleName(FacilioConstants.ContextNames.INVENTORY_REQUEST);
            stateFlow.setDefaultStateId(defaultStatus.getId());
            stateFlow.setDefaltStateFlow(true);
            Criteria criteria = new Criteria();
            criteria.addAndCondition(CriteriaAPI.getCondition("Inventory_Requests.NAME", "name",null, CommonOperators.IS_NOT_EMPTY));
            stateFlow.setCriteria(criteria);
            FacilioChain chain = TransactionChainFactory.getAddOrUpdateStateFlow();
            chain.getContext().put(FacilioConstants.ContextNames.RECORD,stateFlow);
            chain.execute();
            return stateFlow.getId();
        }
        return -1;
    }

    private void addStateFlowTransitions(FacilioModule module,long stateFlowId, String fromState, String toState,String transitionName,Criteria criteria) throws Exception {
        StateflowTransitionContext transition = new StateflowTransitionContext();
        transition.setActivityType(EventType.STATE_TRANSITION);
        transition.setRuleType(WorkflowRuleContext.RuleType.STATE_RULE);
        transition.setType(AbstractStateTransitionRuleContext.TransitionType.CONDITIONED);
        transition.setStatus(true);
        transition.setName(transitionName);
        transition.setModuleId(module.getModuleId());
        transition.setStateFlowId(stateFlowId);
        FacilioStatus fromStatus = TicketAPI.getStatus(module,fromState);
        FacilioStatus toStatus = TicketAPI.getStatus(module,toState);

        if(fromStatus!=null){
            transition.setFromStateId(fromStatus.getId());
        }
        if(toStatus!=null){
            transition.setToStateId(toStatus.getId());
        }
        transition.setCriteria(criteria);
        FacilioChain chain =TransactionChainFactory.getAddOrUpdateStateFlowTransition();
        chain.getContext().put(FacilioConstants.ContextNames.WORKFLOW_RULE,transition);
        chain.getContext().put(FacilioConstants.ContextNames.MODULE,module);
        chain.execute();
    }
}
