package com.facilio.internal;

import com.facilio.bmsconsole.actions.FacilioAction;
import org.apache.struts2.ServletActionContext;

import javax.servlet.http.HttpServletRequest;

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
