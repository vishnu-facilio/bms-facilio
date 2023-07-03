package com.facilio.flows.action;

import com.facilio.blockfactory.enums.BlockType;
import com.facilio.bmsconsole.actions.FacilioAction;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.flows.chain.FlowChain;
import com.facilio.flows.context.FlowContext;
import com.facilio.flows.context.FlowTransitionContext;
import lombok.Getter;
import lombok.Setter;
import org.json.simple.JSONObject;

@Getter@Setter
public class FlowAction extends FacilioAction {

    private FlowContext flow;
    private int type;
    private long id;
    private String moduleName;
    private long recordId=-1l;
    private JSONObject flowTransition;
    private long flowId;
    private BlockType availableBlock;
    private FlowTransitionContext toBlock;
    private FlowTransitionContext fromBlock;
    private String handlePosition;

    public String addOrUpdateFlow() throws Exception {
        FacilioChain chain = FlowChain.getAddOrUpdateFlowChain();
        FacilioContext context = chain.getContext();

        context.put(FacilioConstants.ContextNames.FLOW, flow);

        chain.execute();

        setResult(FacilioConstants.ContextNames.FLOW, context.get(FacilioConstants.ContextNames.FLOW));

        return SUCCESS;
    }

    public String getFlowList() throws Exception {
        FacilioChain chain = FlowChain.getFlowListChain();
        FacilioContext context = chain.getContext();

        context.put(FacilioConstants.ContextNames.TYPE, type);
        context.put(FacilioConstants.ContextNames.MODULE_NAME, moduleName);

        chain.execute();

        setResult(FacilioConstants.ContextNames.FLOWS, context.get(FacilioConstants.ContextNames.FLOWS));

        return SUCCESS;

    }

    public String viewFlow() throws Exception {
        FacilioChain chain = FlowChain.getFlowChain();
        FacilioContext context = chain.getContext();
        context.put(FacilioConstants.ContextNames.ID, id);
        chain.execute();
        setResult(FacilioConstants.ContextNames.FLOW, context.get(FacilioConstants.ContextNames.FLOW));
        return SUCCESS;
    }

    public String deleteFlow() throws Exception {
        FacilioChain chain = FlowChain.getDeleteFlowChain();
        FacilioContext context = chain.getContext();
        context.put(FacilioConstants.ContextNames.ID, id);
        chain.execute();
        return SUCCESS;
    }
    public String addOrUpdateFlowTransition() throws Exception {
        FacilioChain chain = FlowChain.getInitAddOrUpdateFlowTransitionConfigChain();
        FacilioContext context = chain.getContext();
        context.put(FacilioConstants.ContextNames.FLOW_TRANSITION, flowTransition);
        chain.execute();
        setResult(FacilioConstants.ContextNames.FLOW_TRANSITION, context.get(FacilioConstants.ContextNames.FLOW_TRANSITION));
        return SUCCESS;
    }

    public String getFlowTransitionList() throws Exception {
        FacilioChain chain = FlowChain.getFlowTransitionListChain();
        FacilioContext context = chain.getContext();
        context.put(FacilioConstants.ContextNames.FLOW_ID, flowId);
        chain.execute();
        setResult(FacilioConstants.ContextNames.FLOW_TRANSITIONS, context.get(FacilioConstants.ContextNames.FLOW_TRANSITIONS));
        return SUCCESS;
    }

    public String viewFlowTransition() throws Exception {
        FacilioChain chain = FlowChain.getViewFlowTransitionChain();
        FacilioContext context = chain.getContext();
        context.put(FacilioConstants.ContextNames.ID, id);
        chain.execute();
        setResult(FacilioConstants.ContextNames.FLOW_TRANSITION, context.get(FacilioConstants.ContextNames.FLOW_TRANSITION));
        setResult(FacilioConstants.ContextNames.SUPPLEMENTS,context.get(FacilioConstants.ContextNames.SUPPLEMENTS));
        return SUCCESS;
    }

    public String deleteFlowTransition() throws Exception {
        FacilioChain chain = FlowChain.getDeleteFlowTransitionChain();
        FacilioContext context = chain.getContext();
        context.put(FacilioConstants.ContextNames.ID, id);
        chain.execute();
        return SUCCESS;
    }

    public String deleteTransactionConnection() throws Exception {
        FacilioChain chain = FlowChain.getDeleteTransactionConnectionChain();
        FacilioContext context = chain.getContext();
        context.put(FacilioConstants.ContextNames.ID, id);
        chain.execute();
        return SUCCESS;
    }

    public String availableBlocksList() throws Exception {
        FacilioChain chain = FlowChain.getAvailableBlocksChain();
        FacilioContext context = chain.getContext();
        context.put(FacilioConstants.ContextNames.TYPE, type);
        context.put(FacilioConstants.ContextNames.MODULE_NAME, moduleName);
        chain.execute();
        setResult(FacilioConstants.ContextNames.AVAILABLE_BLOCKS, context.get(FacilioConstants.ContextNames.AVAILABLE_BLOCKS));
        return SUCCESS;
    }

    public String executeFlow() throws Exception {
        FacilioChain chain = FlowChain.getExecuteFlowChain();
        FacilioContext context = chain.getContext();
        context.put(FacilioConstants.ContextNames.FLOW_ID, flowId);
        context.put(FacilioConstants.ContextNames.RECORD_ID,recordId);
        context.put(FacilioConstants.ContextNames.MODULE_NAME,moduleName);
        chain.execute();

        setResult(FacilioConstants.ContextNames.MEMORY, context.get(FacilioConstants.ContextNames.MEMORY));
        return SUCCESS;
    }
    public String updateConnection() throws Exception{
        FacilioChain chain = FlowChain.getUpdateConnectionChain();
        FacilioContext context = chain.getContext();
        context.put(FacilioConstants.ContextNames.FLOW_ID,flowId);
        context.put(FacilioConstants.ContextNames.FROM_BLOCK,fromBlock);
        context.put(FacilioConstants.ContextNames.TO_BLOCK,toBlock);
        context.put(FacilioConstants.ContextNames.HANDLE_POSITION,handlePosition);
        chain.execute();
        return SUCCESS;
    }
    public String getFlowOperators() throws Exception{
        FacilioChain chain = FlowChain.getFlowOperatorChain();
        FacilioContext context = chain.getContext();
        chain.execute();

        setResult("operators",context.get("operatorsMap"));
        return SUCCESS;
    }

}
