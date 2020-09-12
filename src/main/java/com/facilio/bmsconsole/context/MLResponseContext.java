package com.facilio.bmsconsole.context;

import com.facilio.modules.ModuleBaseWithCustomFields;

public class MLResponseContext extends ModuleBaseWithCustomFields {
	
	private static final long serialVersionUID = 1L;
	
	private String usecaseId;
	private Boolean status;
	private String message;
	
	public String getUsecaseId() {
		return usecaseId;
	}
	
	public void setUsecaseId(String usecaseId) {
		this.usecaseId = usecaseId;
	}
	
	public Boolean getStatus() {
		return status;
	}
	
	public void setStatus(Boolean status) {
		this.status = status;
	}
	
	public String getMessage() {
		return message;
	}
	
	public void setMessage(String message) {
		this.message = message;
	}
	

}
