package com.facilio.bmsconsole.workflow.rule;

import com.facilio.bmsconsole.context.ResourceContext;
import com.facilio.bmsconsole.context.WorkOrderContext;
import com.facilio.chain.FacilioContext;

import java.util.Map;

public class SLARuleContext extends WorkflowRuleContext {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private long resourceId = -1;
	
	public long getResourceId() {
		return resourceId;
	}
	public void setResourceId(long resourceId) {
		this.resourceId = resourceId;
	}
	
	private ResourceContext resource;
	public ResourceContext getResource() {
		return resource;
	}
	public void setResource(ResourceContext resource) {
		this.resource = resource;
	}
	
	private long groupId = -1;
	public long getGroupId() {
		return groupId;
	}
	public void setGroupId(long groupId) {
		this.groupId = groupId;
	}
	
	@Override
	public boolean evaluateMisc(String moduleName, Object record, Map<String, Object> placeHolders, FacilioContext context) throws Exception {
		// TODO Auto-generated method stub
		boolean isResourceMatched = true;
		boolean isGroupMatched = true;
		if (resourceId != -1) {
			isResourceMatched = ((WorkOrderContext)record).getResource() != null && resourceId == ((WorkOrderContext)record).getResource().getId();
		}
		if (groupId != -1) {
			isGroupMatched = ((WorkOrderContext)record).getAssignmentGroup() != null && groupId == ((WorkOrderContext)record).getAssignmentGroup().getId();
		}
		return isResourceMatched && isGroupMatched;
	}
}
