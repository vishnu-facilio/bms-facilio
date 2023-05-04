package com.facilio.flows.chain;

import com.facilio.chain.FacilioChain;
import com.facilio.flows.command.*;

public class FlowChain {

    public static FacilioChain getAddOrUpdateFlowChain(){
        FacilioChain chain = FacilioChain.getTransactionChain();
        chain.addCommand(new AddOrUpdateFlowCommand());
        return chain;
    }

    public static FacilioChain getFlowListChain(){
        FacilioChain chain = FacilioChain.getNonTransactionChain();
        chain.addCommand(new GetFlowListCommand());
        return chain;
    }

    public static FacilioChain getFlowChain(){
        FacilioChain chain = FacilioChain.getNonTransactionChain();
        chain.addCommand(new GetFlowCommand());
        return chain;
    }

    public static FacilioChain getDeleteFlowChain(){
        FacilioChain chain = FacilioChain.getTransactionChain();
        chain.addCommand(new DeleteFlowCommand());
        return chain;
    }

    public static FacilioChain getAddOrUpdateFlowTransitionChain(){
        FacilioChain chain = FacilioChain.getTransactionChain();
        chain.addCommand(new AddOrUpdateFlowTransitionCommand());
        return chain;
    }

    public static FacilioChain getFlowTransitionListChain(){
        FacilioChain chain = FacilioChain.getNonTransactionChain();
        chain.addCommand(new GetFlowTransitionListCommand());
        return chain;
    }

    public static FacilioChain getViewFlowTransitionChain(){
        FacilioChain chain = FacilioChain.getNonTransactionChain();
        chain.addCommand(new ViewFlowTransitionCommand());
        return chain;
    }

    public static FacilioChain getDeleteFlowTransitionChain(){
        FacilioChain chain = FacilioChain.getTransactionChain();
        chain.addCommand(new DeleteFlowTransitionCommand());
        return chain;
    }

    public static FacilioChain getDeleteTransactionConnectionChain(){
        FacilioChain chain = FacilioChain.getTransactionChain();
        chain.addCommand(new DeleteTransactionConnectionCommand());
        return chain;
    }

    public static FacilioChain getAvailableBlocksChain(){
        FacilioChain chain = FacilioChain.getNonTransactionChain();
        chain.addCommand(new GetAvailableBlocksListCommand());
        return chain;
    }
}
