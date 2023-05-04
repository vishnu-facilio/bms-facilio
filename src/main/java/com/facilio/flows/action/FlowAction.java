package com.facilio.flows.action;

import com.facilio.bmsconsole.actions.FacilioAction;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.flows.chain.FlowChain;
import com.facilio.flows.context.FlowContext;
import com.facilio.flows.context.FlowTransitionContext;

public class FlowAction extends FacilioAction {

    private FlowContext flow;

    public void setFlow(FlowContext flow) {
        this.flow = flow;
    }

    public FlowContext getFlow() {
        return flow;
    }

    private int type;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    private long id;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    private String moduleName;

    public String getModuleName() {
        return moduleName;
    }

    public void setModuleName(String moduleName) {
        this.moduleName = moduleName;
    }

    public String addOrUpdateFlow() throws Exception{
        FacilioChain chain = FlowChain.getAddOrUpdateFlowChain();
        FacilioContext context = chain.getContext();

        context.put(FacilioConstants.ContextNames.FLOW, flow);

        chain.execute();

        setResult(FacilioConstants.ContextNames.FLOW,context.get(FacilioConstants.ContextNames.FLOW));

        return SUCCESS;
    }

    public String getFlowList() throws Exception{
        FacilioChain chain = FlowChain.getFlowListChain();
        FacilioContext context = chain.getContext();

        context.put(FacilioConstants.ContextNames.TYPE,type);
        context.put(FacilioConstants.ContextNames.MODULE_NAME,moduleName);

        chain.execute();

        setResult(FacilioConstants.ContextNames.FLOWS,context.get(FacilioConstants.ContextNames.FLOWS));

        return SUCCESS;

    }

    public String viewFlow() throws Exception{
        FacilioChain chain = FlowChain.getFlowChain();
        FacilioContext context = chain.getContext();
        context.put(FacilioConstants.ContextNames.ID,id);
        chain.execute();
        setResult(FacilioConstants.ContextNames.FLOW,context.get(FacilioConstants.ContextNames.FLOW));
        return SUCCESS;
    }

    public String deleteFlow() throws Exception{
        FacilioChain chain = FlowChain.getDeleteFlowChain();
        FacilioContext context = chain.getContext();
        context.put(FacilioConstants.ContextNames.ID,id);
        chain.execute();
        return SUCCESS;
    }

    private FlowTransitionContext flowTransition;

    public FlowTransitionContext getFlowTransition() {
        return flowTransition;
    }

    public void setFlowTransition(FlowTransitionContext flowTransition) {
        this.flowTransition = flowTransition;
    }
    private long flowId;

    public long getFlowId() {
        return flowId;
    }

    public void setFlowId(long flowId) {
        this.flowId = flowId;
    }

   private FlowTransitionContext.Blocks availableBlock;

    public void setAvailableBlock(FlowTransitionContext.Blocks availableBlock) {
        this.availableBlock = availableBlock;
    }

    public FlowTransitionContext.Blocks getAvailableBlock() {
        return availableBlock;
    }

    public String addOrUpdateFlowTransition() throws Exception{
        FacilioChain chain = FlowChain.getAddOrUpdateFlowTransitionChain();
        FacilioContext context = chain.getContext();
        context.put(FacilioConstants.ContextNames.FLOW_TRANSITION,flowTransition);
        chain.execute();
        setResult(FacilioConstants.ContextNames.FLOW_TRANSITION,context.get(FacilioConstants.ContextNames.FLOW_TRANSITION));
        return SUCCESS;
    }

    public String getFlowTransitionList() throws Exception{
        FacilioChain chain = FlowChain.getFlowTransitionListChain();
        FacilioContext context = chain.getContext();
        context.put(FacilioConstants.ContextNames.FLOW_ID,flowId);
        chain.execute();
        setResult(FacilioConstants.ContextNames.FLOW_TRANSITIONS,context.get(FacilioConstants.ContextNames.FLOW_TRANSITIONS));
        return SUCCESS;
    }

    public String viewFlowTransition() throws Exception{
        FacilioChain chain = FlowChain.getViewFlowTransitionChain();
        FacilioContext context = chain.getContext();
        context.put(FacilioConstants.ContextNames.ID,id);
        chain.execute();
        setResult(FacilioConstants.ContextNames.FLOW_TRANSITION,context.get(FacilioConstants.ContextNames.FLOW_TRANSITION));
        return SUCCESS;
    }

    public String deleteFlowTransition() throws Exception{
        FacilioChain chain = FlowChain.getDeleteFlowTransitionChain();
        FacilioContext context = chain.getContext();
        context.put(FacilioConstants.ContextNames.ID,id);
        chain.execute();
        return SUCCESS;
    }

    public String deleteTransactionConnection() throws Exception{
        FacilioChain chain = FlowChain.getDeleteTransactionConnectionChain();
        FacilioContext context = chain.getContext();
        context.put(FacilioConstants.ContextNames.ID,id);
        chain.execute();
        return SUCCESS;
    }

    public String availableBlocksList() throws Exception{
        FacilioChain chain = FlowChain.getAvailableBlocksChain();
        FacilioContext context = chain.getContext();
        context.put(FacilioConstants.ContextNames.TYPE,type);
        context.put(FacilioConstants.ContextNames.MODULE_NAME,moduleName);
        chain.execute();
        setResult(FacilioConstants.ContextNames.AVAILABLE_BLOCKS,context.get(FacilioConstants.ContextNames.AVAILABLE_BLOCKS));
        return SUCCESS;
    }

}
