package com.facilio.bmsconsole.actions;

import com.facilio.bmsconsole.context.ResourceContext;
import com.facilio.bmsconsole.util.ResourceAPI;
import com.facilio.constants.FacilioConstants;

import java.util.List;

public class ResourceAction extends FacilioAction {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private List<Long> resourceId ;
	
	public List<Long> getResourceId() {
		return resourceId;
	}

	public void setResourceId(List<Long> resourceId) {
		this.resourceId = resourceId;
	}

	public String getResources () throws Exception {
		List<ResourceContext> resouces = ResourceAPI.getResources(resourceId, false);
		setResult(FacilioConstants.ContextNames.RESOURCE, resouces);
		return SUCCESS;
	}
}
