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

public class RequestForQuotationDefaultStateFlow  extends SignUpData {
    @Override
    public void addData() throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule rfqModule = modBean.getModule(FacilioConstants.ContextNames.REQUEST_FOR_QUOTATION);
        if(rfqModule!=null && rfqModule.getModuleId()>0){
            String unPublished = addStateForRequestForQuotationStateFlow("Unpublished");
            String awaitingVendorQuotes = addStateForRequestForQuotationStateFlow("Awaiting Vendor Quote");
            String discarded =  addStateForRequestForQuotationStateFlow("Discarded");
            String readyToAward =  addStateForRequestForQuotationStateFlow("Ready to Award");
            String awarded=  addStateForRequestForQuotationStateFlow("Awarded");
            String closed=  addStateForRequestForQuotationStateFlow("Closed");
            long stateFlowId = addRequestForQuotationStateFlow(unPublished);
            if(stateFlowId>0){
                // transition from unPublished to awaitingVendorQuotes
                Criteria criteria = new Criteria();
                criteria.addAndCondition(CriteriaAPI.getCondition("Request_For_Quotation.RFQ_FINALIZED", "isRfqFinalized",String.valueOf(true), BooleanOperators.IS));
                addStateFlowTransitions(rfqModule,stateFlowId,unPublished,awaitingVendorQuotes,"Publish RFQ",criteria);
                // transition from awaitingVendorQuotes to readyToAward
                Criteria closeSubmissionCriteria = new Criteria();
                closeSubmissionCriteria.addAndCondition(CriteriaAPI.getCondition("Request_For_Quotation.QUOTE_RECEIVED", "isQuoteReceived",String.valueOf(true), BooleanOperators.IS));
                addStateFlowTransitions(rfqModule,stateFlowId,awaitingVendorQuotes,readyToAward,"Close Submission",closeSubmissionCriteria);
                //transition from readyToAward to awarded
                Criteria awardCriteria = new Criteria();
                awardCriteria.addAndCondition(CriteriaAPI.getCondition("Request_For_Quotation.AWARDED", "isAwarded",String.valueOf(true), BooleanOperators.IS));
                addStateFlowTransitions(rfqModule,stateFlowId,readyToAward,awarded,"Access and Award",awardCriteria);
                //transition from awarded to closed
                Criteria closeCriteria = new Criteria();
                closeCriteria.addAndCondition(CriteriaAPI.getCondition("Request_For_Quotation.PO_CREATED", "isPoCreated",String.valueOf(true), BooleanOperators.IS));
                addStateFlowTransitions(rfqModule,stateFlowId,awarded,closed,"Create PO",closeCriteria);
                //transition from awaitingVendorQuotes to discarded
                Criteria discardCriteria1 = new Criteria();
                discardCriteria1.addAndCondition(CriteriaAPI.getCondition("Request_For_Quotation.DISCARDED", "isDiscarded",String.valueOf(true), BooleanOperators.IS));
                addStateFlowTransitions(rfqModule,stateFlowId,awaitingVendorQuotes,discarded,"Discard",discardCriteria1);
                //transition from readyToAward to discarded
                Criteria discardCriteria2 = new Criteria();
                discardCriteria2.addAndCondition(CriteriaAPI.getCondition("Request_For_Quotation.DISCARDED", "isDiscarded",String.valueOf(true), BooleanOperators.IS));
                addStateFlowTransitions(rfqModule,stateFlowId,readyToAward,discarded,"Discard",discardCriteria2);

            }
        }
    }
    private String addStateForRequestForQuotationStateFlow(String stateDisplayName) throws Exception {
        FacilioStatus state = new FacilioStatus();
        state.setDisplayName(stateDisplayName);
        state.setTypeCode(1);
        state.setRecordLocked(false);
        state.setTimerEnabled(false);
        state.setRequestedState(false);
        FacilioChain chain = TransactionChainFactory.getAddOrUpdateTicketStatusChain();
        chain.getContext().put(FacilioConstants.ContextNames.TICKET_STATUS,state);
        chain.getContext().put(FacilioConstants.ContextNames.PARENT_MODULE,FacilioConstants.ContextNames.REQUEST_FOR_QUOTATION);
        chain.execute();
        return state.getStatus();
    }
    private long addRequestForQuotationStateFlow(String defaultState) throws Exception {
        StateFlowRuleContext stateFlow = new StateFlowRuleContext();
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioStatus defaultStatus = TicketAPI.getStatus(modBean.getModule(FacilioConstants.ContextNames.REQUEST_FOR_QUOTATION) ,defaultState);
        if(defaultStatus!=null){
            stateFlow.setName("Default Stateflow");
            stateFlow.setModuleName(FacilioConstants.ContextNames.REQUEST_FOR_QUOTATION);
            stateFlow.setDefaultStateId(defaultStatus.getId());
            stateFlow.setDefaltStateFlow(true);
            Criteria criteria = new Criteria();
            criteria.addAndCondition(CriteriaAPI.getCondition("Request_For_Quotation.NAME", "name",null, CommonOperators.IS_NOT_EMPTY));
            stateFlow.setCriteria(criteria);
            FacilioChain chain = TransactionChainFactory.getAddOrUpdateStateFlow();
            chain.getContext().put(FacilioConstants.ContextNames.RECORD,stateFlow);
            chain.execute();
            return stateFlow.getId();
        }
        return -1;
    }
    private void addStateFlowTransitions(FacilioModule rfqModule,long stateFlowId, String fromState, String toState,String transitionName,Criteria criteria) throws Exception {
        StateflowTransitionContext transition = new StateflowTransitionContext();
        transition.setActivityType(EventType.STATE_TRANSITION);
        transition.setRuleType(WorkflowRuleContext.RuleType.STATE_RULE);
        transition.setType(AbstractStateTransitionRuleContext.TransitionType.CONDITIONED);
        transition.setStatus(true);
        transition.setName(transitionName);
        transition.setModuleId(rfqModule.getModuleId());
        transition.setStateFlowId(stateFlowId);
        FacilioStatus fromStatus = TicketAPI.getStatus(rfqModule,fromState);
        FacilioStatus toStatus = TicketAPI.getStatus(rfqModule,toState);

        if(fromStatus!=null){
            transition.setFromStateId(fromStatus.getId());
        }
        if(toStatus!=null){
            transition.setToStateId(toStatus.getId());
        }
        transition.setCriteria(criteria);
        FacilioChain chain =TransactionChainFactory.getAddOrUpdateStateFlowTransition();
        chain.getContext().put(FacilioConstants.ContextNames.WORKFLOW_RULE,transition);
        chain.getContext().put(FacilioConstants.ContextNames.MODULE,rfqModule);
        chain.execute();
    }
}
