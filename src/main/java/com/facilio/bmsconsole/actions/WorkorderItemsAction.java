package com.facilio.bmsconsole.actions;

import java.util.List;

import org.apache.commons.chain.Chain;
import org.apache.commons.chain.Command;

import com.facilio.bmsconsole.commands.ReadOnlyChainFactory;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.context.WorkorderItemContext;
import com.facilio.bmsconsole.context.WorkorderPartsContext;
import com.facilio.bmsconsole.workflow.rule.ActivityType;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;

public class WorkorderItemsAction extends FacilioAction{
	private static final long serialVersionUID = 1L;
	private List<WorkorderItemContext> workorderItems;
	public List<WorkorderItemContext> getWorkorderItems() {
		return workorderItems;
	}
	public void setWorkorderItems(List<WorkorderItemContext> workorderItems) {
		this.workorderItems = workorderItems;
	}
	
	private List<Long> workorderItemsId;
	public List<Long> getWorkorderItemsId() {
		return workorderItemsId;
	}
	public void setWorkorderItemsId(List<Long> workorderItemsId) {
		this.workorderItemsId = workorderItemsId;
	}
	
	private long parentId = -1;
	public long getParentId() {
		return parentId;
	}
	public void setParentId(long parentId) {
		this.parentId = parentId;
	}
	
	public String addOrUpdateWorkorderItems() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.ACTIVITY_TYPE, ActivityType.CREATE);
		context.put(FacilioConstants.ContextNames.RECORD_LIST, workorderItems);
		Chain addWorkorderPartChain = TransactionChainFactory.getAddOrUdpateWorkorderItemsChain();
		addWorkorderPartChain.execute(context);
		setWorkorderItemsId((List<Long>) context.get(FacilioConstants.ContextNames.RECORD_ID_LIST));
		setResult("workorderPartsId", workorderItemsId);
		return SUCCESS;
	} 
	
	public String deleteWorkorderItems() throws Exception {
		FacilioContext context = new FacilioContext();
		WorkorderItemContext workorderItem = new WorkorderItemContext();
		workorderItem.setDeleted(true);

		context.put(FacilioConstants.ContextNames.ACTIVITY_TYPE, ActivityType.DELETE);
		context.put(FacilioConstants.ContextNames.RECORD, workorderItem);
		context.put(FacilioConstants.ContextNames.RECORD_ID_LIST, workorderItemsId);

		Chain deleteInventoryChain = TransactionChainFactory.getDeleteWorkorderItemChain();
		deleteInventoryChain.execute(context);
		setWorkorderItemsId((List<Long>) context.get(FacilioConstants.ContextNames.RECORD_ID_LIST));
		setResult("workorderItemsId", workorderItemsId);
		return SUCCESS;
	}
	
	public String workorderItemsList() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.PARENT_ID, parentId);
		Command getWorkorderPartsList = ReadOnlyChainFactory.getWorkorderItemsList();
		getWorkorderPartsList.execute(context);
		workorderItems = ((List<WorkorderItemContext>) context.get(FacilioConstants.ContextNames.WORKORDER_ITEMS));
		setResult(FacilioConstants.ContextNames.WORKORDER_ITEMS, workorderItems);
		return SUCCESS;
	}

}
