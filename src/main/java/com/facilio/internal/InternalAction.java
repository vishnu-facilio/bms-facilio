package com.facilio.internal;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts2.ServletActionContext;

import com.facilio.bmsconsole.actions.FacilioAction;

public class InternalAction extends FacilioAction {
	
	private static final long serialVersionUID = 1L;
	
	public String checkMinClientVersion() throws Exception {
		
		HttpServletRequest request = ServletActionContext.getRequest();
		String deviceType = request.getHeader("X-Device-Type");
		double minVersion =	Double.parseDouble(request.getHeader("X-App-Version"));
		boolean isMinVersion =  InternalApi.checkMinClientVersion(deviceType, minVersion);
		setResult("minVersion", isMinVersion);
		
		return SUCCESS;
	}

}
