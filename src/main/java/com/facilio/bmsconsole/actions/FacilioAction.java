package com.facilio.bmsconsole.actions;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.log4j.LogManager;
import org.json.simple.JSONObject;

import com.facilio.constants.FacilioConstants;
import com.opensymphony.xwork2.ActionSupport;

public class FacilioAction extends ActionSupport {
	
	private int responseCode = 0;
	public int getResponseCode() {
		return responseCode;
	}
	public void setResponseCode(int responseCode) {
		this.responseCode = responseCode;
	}
	
	private String message;
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	
	public void setMessage(Exception e) {
		if (e instanceof IllegalArgumentException) {
			this.message = e.getMessage();			
		}
		else {
			this.message = FacilioConstants.ERROR_MESSAGE;
		}
		setStackTrace(e);
	}
	
	private JSONObject result;
	public JSONObject getResult() {
		return result;
	}
	
	@SuppressWarnings("unchecked")
	public void setResult(String key, Object result) {
		if (this.result == null) {
			this.result = new JSONObject();
		}
		this.result.put(key, result);			
	}
	
	private String stackTrace;
	public String getStackTrace() {
		return stackTrace;
	}
	public void setStackTrace(Exception e) {
		this.stackTrace = ExceptionUtils.getStackTrace(e);
		LogManager.getLogger(this.getClass().getName()).error("Exception occured: - ", e);
	}

}
