package com.facilio.bmsconsoleV3.signup.requestForQuotation;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.util.TicketAPI;
import com.facilio.bmsconsole.workflow.rule.*;
import com.facilio.bmsconsoleV3.signup.SignUpData;
import com.facilio.chain.FacilioChain;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.BooleanOperators;
import com.facilio.db.criteria.operators.CommonOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FacilioStatus;

public class VendorQuoteDefaultStateflow extends SignUpData {
    @Override
    public void addData() throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule vendorQuoteModule = modBean.getModule(FacilioConstants.ContextNames.VENDOR_QUOTES);
        if(vendorQuoteModule!=null && vendorQuoteModule.getModuleId()>0){
            String awaitingVendorQuote = addStateForVendorQuoteStateFlow("Awaiting Vendor Quote");
           String submittedQuote = addStateForVendorQuoteStateFlow("Submitted Quote");
           String underNegotiation =  addStateForVendorQuoteStateFlow("Under Negotiation");
            long stateFlowId = addVendorQuoteStateFlow(awaitingVendorQuote);
            if(stateFlowId>0){
                // transition from Awaiting Vendor Quote to Submitted Quote
                Criteria criteria = new Criteria();
                criteria.addAndCondition(CriteriaAPI.getCondition("Vendor_Quotes.FINALIZED", "isFinalized",String.valueOf(true), BooleanOperators.IS));
                addStateFlowTransitions(vendorQuoteModule,stateFlowId,awaitingVendorQuote,submittedQuote,"Submit Quote",criteria);
                // transition from Submitted Quote to Negotiation
                Criteria negotiationCriteria = new Criteria();
                negotiationCriteria.addAndCondition(CriteriaAPI.getCondition("Vendor_Quotes.NEGOTIATION", "negotiation",String.valueOf(true), BooleanOperators.IS));
                addStateFlowTransitions(vendorQuoteModule,stateFlowId,submittedQuote,underNegotiation,"Negotiate",negotiationCriteria);
                //transition from Negotiation to Submitted Quote
                Criteria submitQuoteCriteria = new Criteria();
                submitQuoteCriteria.addAndCondition(CriteriaAPI.getCondition("Vendor_Quotes.NEGOTIATION", "negotiation",String.valueOf(false), BooleanOperators.IS));
                addStateFlowTransitions(vendorQuoteModule,stateFlowId,underNegotiation,submittedQuote,"Submit Quote",submitQuoteCriteria);

            }
        }
    }
    private String addStateForVendorQuoteStateFlow(String stateDisplayName) throws Exception {
        FacilioStatus state = new FacilioStatus();
        state.setDisplayName(stateDisplayName);
        state.setTypeCode(1);
        state.setRecordLocked(false);
        state.setTimerEnabled(false);
        state.setRequestedState(false);
        FacilioChain chain = TransactionChainFactory.getAddOrUpdateTicketStatusChain();
        chain.getContext().put(FacilioConstants.ContextNames.TICKET_STATUS,state);
        chain.getContext().put(FacilioConstants.ContextNames.PARENT_MODULE,FacilioConstants.ContextNames.VENDOR_QUOTES);
        chain.execute();
        return state.getStatus();
    }
    private long addVendorQuoteStateFlow(String defaultState) throws Exception {
        StateFlowRuleContext stateFlow = new StateFlowRuleContext();
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioStatus defaultStatus = TicketAPI.getStatus(modBean.getModule(FacilioConstants.ContextNames.VENDOR_QUOTES) ,defaultState);
        if(defaultStatus!=null){
            stateFlow.setName("Default Stateflow");
            stateFlow.setModuleName(FacilioConstants.ContextNames.VENDOR_QUOTES);
            stateFlow.setDefaultStateId(defaultStatus.getId());
            stateFlow.setDefaltStateFlow(true);
            Criteria criteria = new Criteria();
            criteria.addAndCondition(CriteriaAPI.getCondition("Vendor_Quotes.RFQ_ID", "requestForQuotation",null, CommonOperators.IS_NOT_EMPTY));
            stateFlow.setCriteria(criteria);
            FacilioChain chain = TransactionChainFactory.getAddOrUpdateStateFlow();
            chain.getContext().put(FacilioConstants.ContextNames.RECORD,stateFlow);
            chain.execute();
            return stateFlow.getId();
        }
        return -1;
    }
    private void addStateFlowTransitions(FacilioModule vendorQuoteModule,long stateFlowId, String fromState, String toState,String transitionName,Criteria criteria) throws Exception {
        StateflowTransitionContext transition = new StateflowTransitionContext();
        transition.setActivityType(EventType.STATE_TRANSITION);
        transition.setRuleType(WorkflowRuleContext.RuleType.STATE_RULE);
        transition.setType(AbstractStateTransitionRuleContext.TransitionType.CONDITIONED);
        transition.setStatus(true);
        transition.setName(transitionName);
        transition.setModuleId(vendorQuoteModule.getModuleId());
        transition.setStateFlowId(stateFlowId);
        FacilioStatus fromStatus = TicketAPI.getStatus(vendorQuoteModule,fromState);
        FacilioStatus toStatus = TicketAPI.getStatus(vendorQuoteModule,toState);

        if(fromStatus!=null){
            transition.setFromStateId(fromStatus.getId());
        }
        if(toStatus!=null){
            transition.setToStateId(toStatus.getId());
        }
        transition.setCriteria(criteria);
        FacilioChain chain =TransactionChainFactory.getAddOrUpdateStateFlowTransition();
        chain.getContext().put(FacilioConstants.ContextNames.WORKFLOW_RULE,transition);
        chain.getContext().put(FacilioConstants.ContextNames.MODULE,vendorQuoteModule);
        chain.execute();
    }
}
