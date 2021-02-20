package com.facilio.v4.action;

import java.util.Collections;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.json.simple.JSONObject;

import com.amazonaws.http.HttpMethodName;
import com.facilio.v3.RESTAPIHandler;
import com.facilio.v3.V3Action;
import com.facilio.v3.exception.ErrorCode;

public class RestAPIHandlerV4 extends RESTAPIHandler {
	
	private static final Logger LOGGER = Logger.getLogger(RestAPIHandlerV4.class.getName());

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String throwValidationException(String message) {
		 
		this.setMessage(message);
         this.setCode(ErrorCode.VALIDATION_ERROR.getCode());
         getHttpServletResponse().setStatus(ErrorCode.VALIDATION_ERROR.getHttpStatus());
         LOGGER.log(Level.SEVERE, message);
         return V3Action.FAILURE;
	}

	@Override
	public String execute() throws Exception {
		
		if(getModuleName() == null) {
			return throwValidationException("Module Name cannot be null");
		}
		
		HttpMethodName httpMethod = HttpMethodName.fromValue(getHttpServletRequest().getMethod());
		
		switch(httpMethod) {
			
			case GET:
				if(getId() > 0) {
					return summary();
				}
				else {
					return list();
				}
			case POST:
					if(getData() != null) {
						changeDataForV3();
						return create();
					}
					else {
						return throwValidationException("Data Object cannot be empty");
					}
			case PUT:
				if(getId() > 0) {
					if(getData() != null) {
						changeDataForV3();
						return patch();
					}
					else {
						return throwValidationException("Data Object cannot be empty");
					}
				}
				else {
					return throwValidationException("Record ID cannot be empty during PUT");
				}
			case DELETE:
				if(getId() > 0) {
					changeDeleteDataForV3();
					return delete();
				}
				else {
					return throwValidationException("Record ID cannot be empty during DELETE");
				}
		default:
			return throwValidationException("Unsuported HTTP Method : "+httpMethod.name());
		}
	}

	private void changeDeleteDataForV3() {
		
		JSONObject currentData = new JSONObject();
		currentData.put(getModuleName(), Collections.singletonList(getId()));
		setData(currentData);
	}

	private void changeDataForV3() {
		
		JSONObject currentData = getData();
		
		Map<String, Object> moduleData = (Map)currentData.get(getModuleName());
		
		currentData.remove(getModuleName());
		
		currentData.putAll(moduleData);
	}
}
