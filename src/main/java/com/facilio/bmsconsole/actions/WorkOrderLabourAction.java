package com.facilio.bmsconsole.actions;

import java.util.Collections;
import java.util.List;

import org.apache.commons.chain.Chain;
import org.apache.commons.chain.Command;

import com.facilio.bmsconsole.commands.ReadOnlyChainFactory;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.context.WorkOrderLabourContext;
import com.facilio.bmsconsole.workflow.rule.EventType;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;

public class WorkOrderLabourAction extends FacilioAction {
	private static final long serialVersionUID = 1L;

	private WorkOrderLabourContext workorderLabour;

	public WorkOrderLabourContext getWorkorderLabour() {
		return workorderLabour;
	}

	public void setWorkorderTools(WorkOrderLabourContext workorderLabour) {
		this.workorderLabour = workorderLabour;
	}

	private List<WorkOrderLabourContext> workorderLabourList;

	public List<WorkOrderLabourContext> getWorkorderLabourList() {
		return workorderLabourList;
	}

	public void setWorkorderLabourList(List<WorkOrderLabourContext> workorderLabourList) {
		this.workorderLabourList = workorderLabourList;
	}

	private long workorderLabourId;

	public long getWorkorderLabourId() {
		return workorderLabourId;
	}

	public void setWorkorderToolsId(long workorderLabourId) {
		this.workorderLabourId = workorderLabourId;
	}

	private long parentId = -1;

	public long getParentId() {
		return parentId;
	}

	public void setParentId(long parentId) {
		this.parentId = parentId;
	}

	private List<Long> workorderLabourIds;

	public List<Long> getWorkorderLabourIds() {
		return workorderLabourIds;
	}

	public void setWorkorderLabourIds(List<Long> workorderLabourIds) {
		this.workorderLabourIds = workorderLabourIds;
	}

	public String addOrUpdateWorkorderLabour() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.EVENT_TYPE, EventType.CREATE);
		context.put(FacilioConstants.ContextNames.RECORD_LIST, workorderLabourList);
		Chain addWorkorderPartChain = TransactionChainFactory.getAddOrUdpateWorkorderLabourChain();
		addWorkorderPartChain.execute(context);
		setWorkorderLabourIds((List<Long>) context.get(FacilioConstants.ContextNames.RECORD_ID_LIST));
		setResult("workorderLabourIds", workorderLabourIds);
		return SUCCESS;
	}
	
	public String deleteWorkorderLabour() throws Exception {
		FacilioContext context = new FacilioContext();
	
		context.put(FacilioConstants.ContextNames.EVENT_TYPE, EventType.DELETE);
		context.put(FacilioConstants.ContextNames.PARENT_ID, parentId);
		context.put(FacilioConstants.ContextNames.PARENT_ID_LIST, Collections.singletonList(parentId));
		context.put(FacilioConstants.ContextNames.RECORD_ID_LIST, workorderLabourIds);
		context.put(FacilioConstants.ContextNames.WORKORDER_COST_TYPE,3 );

		Chain deleteLabourChain = TransactionChainFactory.getDeleteWorkorderLabourChain();
		deleteLabourChain.execute(context);
		setWorkorderLabourIds((List<Long>) context.get(FacilioConstants.ContextNames.RECORD_ID_LIST));
		setResult("workorderLabourIds", workorderLabourIds);
		return SUCCESS;
	}
	
	public String workorderLabourList() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.PARENT_ID, parentId);
		Command getWorkorderLabourList = ReadOnlyChainFactory.getWorkorderLabourList();
		getWorkorderLabourList.execute(context);
		workorderLabourList = ((List<WorkOrderLabourContext>) context.get(FacilioConstants.ContextNames.WO_LABOUR));
		setResult(FacilioConstants.ContextNames.WO_LABOUR, workorderLabourList);
		return SUCCESS;
	}
	
}

