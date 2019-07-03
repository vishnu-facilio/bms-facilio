package com.facilio.bmsconsole.actions;

import java.util.Collections;
import java.util.List;

import org.apache.commons.chain.Chain;
import org.apache.commons.chain.Command;

import com.facilio.bmsconsole.commands.ReadOnlyChainFactory;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.context.WorkOrderServiceContext;
import com.facilio.bmsconsole.workflow.rule.EventType;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;

public class WorkOrderServiceAction extends FacilioAction{

	
	private static final long serialVersionUID = 1L;

	private WorkOrderServiceContext workorderService;
	private List<WorkOrderServiceContext> workorderServiceList;
	private long workorderServiceId;

		private long parentId = -1;

	public long getParentId() {
		return parentId;
	}

	public void setParentId(long parentId) {
		this.parentId = parentId;
	}

	private List<Long> workorderServiceIds;

	public WorkOrderServiceContext getWorkorderService() {
		return workorderService;
	}

	public void setWorkorderService(WorkOrderServiceContext workorderService) {
		this.workorderService = workorderService;
	}

	public List<WorkOrderServiceContext> getWorkorderServiceList() {
		return workorderServiceList;
	}

	public void setWorkorderServiceList(List<WorkOrderServiceContext> workorderServiceList) {
		this.workorderServiceList = workorderServiceList;
	}

	public long getWorkorderServiceId() {
		return workorderServiceId;
	}

	public void setWorkorderServiceId(long workorderServiceId) {
		this.workorderServiceId = workorderServiceId;
	}

	public List<Long> getWorkorderServiceIds() {
		return workorderServiceIds;
	}

	public void setWorkorderServiceIds(List<Long> workorderServiceIds) {
		this.workorderServiceIds = workorderServiceIds;
	}

	public String addOrUpdateWorkorderService() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.EVENT_TYPE, EventType.CREATE);
		context.put(FacilioConstants.ContextNames.RECORD_LIST, workorderServiceList);
		Chain addWorkorderServiceChain = TransactionChainFactory.getAddOrUdpateWorkorderServiceChain();
		addWorkorderServiceChain.execute(context);
		setResult(FacilioConstants.ContextNames.WO_SERVICES, context.get(FacilioConstants.ContextNames.RECORD_LIST));
		return SUCCESS;
	}
	
	public String deleteWorkorderService() throws Exception {
		FacilioContext context = new FacilioContext();
	
		context.put(FacilioConstants.ContextNames.EVENT_TYPE, EventType.DELETE);
		context.put(FacilioConstants.ContextNames.PARENT_ID, parentId);
		context.put(FacilioConstants.ContextNames.PARENT_ID_LIST, Collections.singletonList(parentId));
		context.put(FacilioConstants.ContextNames.RECORD_ID_LIST, workorderServiceList);
		context.put(FacilioConstants.ContextNames.WORKORDER_COST_TYPE,4 );

		Chain deleteServiceChain = TransactionChainFactory.getDeleteWorkorderServiceChain();
		deleteServiceChain.execute(context);
		setResult(FacilioConstants.ContextNames.WO_SERVICE_IDS, context.get(FacilioConstants.ContextNames.RECORD_ID_LIST));
		return SUCCESS;
	}
	
	public String workorderServiceList() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.PARENT_ID, parentId);
		
		Command getWorkorderServiceList = ReadOnlyChainFactory.getWorkorderServiceList();
		getWorkorderServiceList.execute(context);
		setResult(FacilioConstants.ContextNames.WO_SERVICES, context.get(FacilioConstants.ContextNames.RECORD_LIST));
		return SUCCESS;
	}
	
}
