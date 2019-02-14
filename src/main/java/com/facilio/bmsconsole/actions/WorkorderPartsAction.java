package com.facilio.bmsconsole.actions;

import java.util.List;

import org.apache.commons.chain.Chain;
import org.apache.commons.chain.Command;

import com.facilio.bmsconsole.commands.ReadOnlyChainFactory;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.context.InventoryVendorContext;
import com.facilio.bmsconsole.context.WorkorderPartsContext;
import com.facilio.bmsconsole.workflow.rule.ActivityType;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;

public class WorkorderPartsAction extends FacilioAction{
	private static final long serialVersionUID = 1L;
	List<WorkorderPartsContext> workorderPartsList;
	public List<WorkorderPartsContext> getWorkorderPartsList() {
		return workorderPartsList;
	}
	public void setWorkorderPartsList(List<WorkorderPartsContext> workorderPartsList) {
		this.workorderPartsList = workorderPartsList;
	}
	
	private List<Long> workorderPartsId;
	
	public List<Long> getWorkorderPartsId() {
		return workorderPartsId;
	}
	public void setWorkorderPartsId(List<Long> workorderPartsId) {
		this.workorderPartsId = workorderPartsId;
	}
	
	private long parentId = -1;
	public long getParentId() {
		return parentId;
	}
	public void setParentId(long parentId) {
		this.parentId = parentId;
	}
	
	public String addOrUpdateWorkorderParts() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.ACTIVITY_TYPE, ActivityType.CREATE);
		context.put(FacilioConstants.ContextNames.RECORD_LIST, workorderPartsList);
		Chain addWorkorderPartChain = TransactionChainFactory.getAddOrUdpateWorkorderPartChain();
		addWorkorderPartChain.execute(context);
		setWorkorderPartsId((List<Long>) context.get(FacilioConstants.ContextNames.RECORD_ID_LIST));
		setResult("workorderPartsId", workorderPartsId);
		return SUCCESS;
	} 
	
	public String deleteWorkorderParts() throws Exception {
		FacilioContext context = new FacilioContext();
		WorkorderPartsContext workorderPart = new WorkorderPartsContext();
		workorderPart.setDeleted(true);

		context.put(FacilioConstants.ContextNames.ACTIVITY_TYPE, ActivityType.DELETE);
		context.put(FacilioConstants.ContextNames.RECORD, workorderPart);
		context.put(FacilioConstants.ContextNames.RECORD_ID_LIST, workorderPartsId);

		Chain deleteInventoryChain = TransactionChainFactory.getDeleteWorkorderPartChain();
		deleteInventoryChain.execute(context);
		setWorkorderPartsId((List<Long>) context.get(FacilioConstants.ContextNames.RECORD_ID_LIST));
		setResult("workorderPartsId", workorderPartsId);
		return SUCCESS;
	}
	
	public String workorderPartsList() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.PARENT_ID, parentId);
		Command getWorkorderPartsList = ReadOnlyChainFactory.getWorkorderPartsList();
		getWorkorderPartsList.execute(context);
		workorderPartsList = ((List<WorkorderPartsContext>) context.get(FacilioConstants.ContextNames.WORKORDER_PART_LIST));
		setResult(FacilioConstants.ContextNames.WORKORDER_PART_LIST, workorderPartsList);
		return SUCCESS;
	}

}
