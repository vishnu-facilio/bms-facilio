package com.facilio.bmsconsole.actions;

import java.util.List;

import org.apache.commons.chain.Chain;

import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.context.WorkorderToolsContext;
import com.facilio.bmsconsole.workflow.rule.ActivityType;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;

public class WorkorderToolsAction extends FacilioAction {
	private static final long serialVersionUID = 1L;

	private WorkorderToolsContext workorderTools;

	public WorkorderToolsContext getWorkorderTools() {
		return workorderTools;
	}

	public void setWorkorderTools(WorkorderToolsContext workorderTools) {
		this.workorderTools = workorderTools;
	}

	private List<WorkorderToolsContext> workorderToolsList;

	public List<WorkorderToolsContext> getWorkorderToolsList() {
		return workorderToolsList;
	}

	public void setWorkorderToolsList(List<WorkorderToolsContext> workorderToolsList) {
		this.workorderToolsList = workorderToolsList;
	}

	private long workorderToolsId;

	public long getWorkorderToolsId() {
		return workorderToolsId;
	}

	public void setWorkorderToolsId(long workorderToolsId) {
		this.workorderToolsId = workorderToolsId;
	}

	private long parentId = -1;

	public long getParentId() {
		return parentId;
	}

	public void setParentId(long parentId) {
		this.parentId = parentId;
	}

	private List<Long> workorderToolsIds;

	public List<Long> getWorkorderToolsIds() {
		return workorderToolsIds;
	}

	public void setWorkorderToolsIds(List<Long> workorderToolsIds) {
		this.workorderToolsIds = workorderToolsIds;
	}

	public String addOrUpdateWorkorderTools() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.ACTIVITY_TYPE, ActivityType.CREATE);
		context.put(FacilioConstants.ContextNames.RECORD_LIST, workorderToolsList);
		Chain addWorkorderPartChain = TransactionChainFactory.getAddOrUdpateWorkorderToolsChain();
		addWorkorderPartChain.execute(context);
		setWorkorderToolsIds((List<Long>) context.get(FacilioConstants.ContextNames.RECORD_ID_LIST));
		setResult("workorderToolsIds", workorderToolsIds);
		return SUCCESS;
	}
}
