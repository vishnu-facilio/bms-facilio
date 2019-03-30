package com.facilio.bmsconsole.actions;

import com.facilio.bmsconsole.commands.ReadOnlyChainFactory;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.context.WorkorderCostContext;
import com.facilio.bmsconsole.context.WorkorderItemContext;
import com.facilio.bmsconsole.workflow.rule.EventType;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import org.apache.commons.chain.Chain;
import org.apache.commons.chain.Command;

import java.util.Collections;
import java.util.List;

public class WorkorderCostAction extends FacilioAction {
	private static final long serialVersionUID = 1L;
	private List<WorkorderCostContext> workorderCosts;

	public void setWorkorderCosts(List<WorkorderCostContext> workorderCosts) {
		this.workorderCosts = workorderCosts;
	}

	public List<WorkorderCostContext> getWorkorderCosts() {
		return workorderCosts;
	}

	private long parentId;

	public long getParentId() {
		return parentId;
	}

	public void setParentId(long parentId) {
		this.parentId = parentId;
	}

	private WorkorderCostContext workorderCost;

	public WorkorderCostContext getWorkorderCost() {
		return workorderCost;
	}

	public void setWorkorderCost(WorkorderCostContext workorderCost) {
		this.workorderCost = workorderCost;
	}

	private List<Long> workordercostId;
	public List<Long> getWorkordercostId() {
		return workordercostId;
	}
	public void setWorkordercostId(List<Long> workordercostId) {
		this.workordercostId = workordercostId;
	}
	
	public String addWorkorderCost() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.EVENT_TYPE, EventType.CREATE);
		context.put(FacilioConstants.ContextNames.RECORD, workorderCost);
		context.put(FacilioConstants.ContextNames.PARENT_ID, workorderCost.getParentId());
		context.put(FacilioConstants.ContextNames.WORKORDER_COST_TYPE, 5);
		context.put(FacilioConstants.ContextNames.PARENT_ID_LIST, Collections.singletonList(workorderCost.getParentId()));
		Chain addWorkorderPartChain = TransactionChainFactory.getAddWorkorderCostChain();
		addWorkorderPartChain.execute(context);
		setWorkordercostId((List<Long>) context.get(FacilioConstants.ContextNames.RECORD_ID_LIST));
		setResult("workordercostId", workordercostId);
		return SUCCESS;
	}
	
	public String updateWorkorderCost() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.EVENT_TYPE, EventType.EDIT);
		context.put(FacilioConstants.ContextNames.RECORD, workorderCost);
		context.put(FacilioConstants.ContextNames.RECORD_ID, workorderCost.getId());
		context.put(FacilioConstants.ContextNames.RECORD_ID_LIST, Collections.singletonList(workorderCost.getId()));
		context.put(FacilioConstants.ContextNames.PARENT_ID, workorderCost.getParentId());
		context.put(FacilioConstants.ContextNames.PARENT_ID_LIST, Collections.singletonList(workorderCost.getParentId()));
		context.put(FacilioConstants.ContextNames.WORKORDER_COST_TYPE, 5);
		
		Chain addWorkorderPartChain = TransactionChainFactory.getUpdateWorkorderCostChain();
		addWorkorderPartChain.execute(context);
		setWorkordercostId((List<Long>) context.get(FacilioConstants.ContextNames.RECORD_ID_LIST));
		setResult("workordercostId", workordercostId);
		return SUCCESS;
	}
	
	public String deleteWorkorderCosts() throws Exception {
		FacilioContext context = new FacilioContext();
		WorkorderItemContext workorderItem = new WorkorderItemContext();
		workorderItem.setDeleted(true);

		context.put(FacilioConstants.ContextNames.EVENT_TYPE, EventType.DELETE);
		context.put(FacilioConstants.ContextNames.RECORD, workorderItem);
		context.put(FacilioConstants.ContextNames.RECORD_ID_LIST, workordercostId);
		context.put(FacilioConstants.ContextNames.PARENT_ID, parentId);
		context.put(FacilioConstants.ContextNames.PARENT_ID_LIST, Collections.singletonList(parentId));
		context.put(FacilioConstants.ContextNames.WORKORDER_COST_TYPE, 5);

		Chain deleteInventoryChain = TransactionChainFactory.getDeleteWorkorderCostChain();
		deleteInventoryChain.execute(context);
		setWorkordercostId((List<Long>) context.get(FacilioConstants.ContextNames.RECORD_ID_LIST));
		setResult("workordercostId", workordercostId);
		return SUCCESS;
	}

	public String workorderCostsList() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.PARENT_ID, parentId);
		Command getWorkorderPartsList = ReadOnlyChainFactory.getWorkorderCostList();
		getWorkorderPartsList.execute(context);
		workorderCosts = ((List<WorkorderCostContext>) context.get(FacilioConstants.ContextNames.WORKORDER_COST));
		setResult(FacilioConstants.ContextNames.WORKORDER_COST, workorderCosts);
		return SUCCESS;
	}
}
