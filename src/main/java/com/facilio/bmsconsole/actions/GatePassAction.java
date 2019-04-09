package com.facilio.bmsconsole.actions;

import java.util.List;

import org.apache.commons.chain.Chain;

import com.facilio.bmsconsole.commands.ReadOnlyChainFactory;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.context.GatePassContext;
import com.facilio.bmsconsole.context.GatePassLineItemsContext;
import com.facilio.bmsconsole.context.ItemContext;
import com.facilio.bmsconsole.workflow.rule.EventType;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;

public class GatePassAction extends FacilioAction {

	private static final long serialVersionUID = 1L;
	private GatePassContext gatePass;

	public GatePassContext getGatePass() {
		return gatePass;
	}

	public void setGatePass(GatePassContext gatePass) {
		this.gatePass = gatePass;
	}

	private List<GatePassLineItemsContext> lineItems;

	public List<GatePassLineItemsContext> getLineItems() {
		return lineItems;
	}

	public void setLineItems(List<GatePassLineItemsContext> lineItems) {
		this.lineItems = lineItems;
	}

	private List<Long> transactionsId;

	public void setTransactionsId(List<Long> transactionsId) {
		this.transactionsId = transactionsId;
	}

	public List<Long> getTransactionsId() {
		return transactionsId;
	}

	private int approvedState;

	public int getApprovedState() {
		return approvedState;
	}

	public void setApprovedState(int approvedState) {
		this.approvedState = approvedState;
	}
	
	private long gatePassId;
	public long getGatePassId() {
		return gatePassId;
	}
	public void setGatePassId(long gatePassId) {
		this.gatePassId = gatePassId;
	}
	

	public String addGatePass() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.EVENT_TYPE, EventType.CREATE);
		context.put(FacilioConstants.ContextNames.RECORD, gatePass);
		context.put(FacilioConstants.ContextNames.RECORD_ID_LIST, transactionsId);
		context.put(FacilioConstants.ContextNames.TOOL_TRANSACTION_APPORVED_STATE, approvedState);
		context.put(FacilioConstants.ContextNames.WORKORDER_COST_TYPE, 2);

		Chain c = TransactionChainFactory.getAddGatePassChain();
		c.execute(context);
		setGatePass((GatePassContext) context.get(FacilioConstants.ContextNames.GATE_PASS));
		setResult(FacilioConstants.ContextNames.GATE_PASS, gatePass);
		return SUCCESS;
	}
	
	public String gatePassDetails() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.ID, getGatePassId());

		Chain inventryDetailsChain = ReadOnlyChainFactory.fetchGatePassDetails();
		inventryDetailsChain.execute(context);

		setGatePass((GatePassContext) context.get(FacilioConstants.ContextNames.GATE_PASS));
		setResult(FacilioConstants.ContextNames.GATE_PASS, gatePass);
		return SUCCESS;
	}
	
}
