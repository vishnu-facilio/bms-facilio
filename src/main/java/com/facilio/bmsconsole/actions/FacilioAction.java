package com.facilio.bmsconsole.actions;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.log4j.LogManager;
import org.apache.struts2.ServletActionContext;
import org.json.simple.JSONObject;

import com.facilio.aws.util.AwsUtil;
import com.facilio.constants.FacilioConstants;
import com.opensymphony.xwork2.ActionSupport;

public class FacilioAction extends ActionSupport {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
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
	private void setMessage(String message) {
		this.message = message;
	}
	
	private List<String> selectFields;
	
	
	public List<String> getSelectFields() {
		return selectFields;
	}
	public void setSelectFields(List<String> selectFields) {
		this.selectFields = selectFields;
	}

	private Exception exception;
	public Exception getException() {
		return exception;
	}
	public void setException(Exception exception) {
		this.exception = exception;
	}
	
	public String handleException() {
		this.responseCode = 1;
		if (exception != null && exception instanceof IllegalArgumentException) {
			HttpServletResponse response = ServletActionContext.getResponse();
			HttpServletRequest request = ServletActionContext.getRequest();
			if (response != null && response.getStatus() == 304 && request.getRequestURI().contains("preview")) {
				// LogManager.getLogger(this.getClass().getName()).info("Checking 304 response", exception);
				return SUCCESS;
			}
			this.message = exception.getMessage();
		}
		else {
			this.message = FacilioConstants.ERROR_MESSAGE;
		}
		setStackTrace(exception);
		return ERROR;
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
		if (e != null) {
			if(!"production".equals(AwsUtil.getConfig("environment")) || fetchStackTrace) {
				this.stackTrace = ExceptionUtils.getStackTrace(e);
			}
			LogManager.getLogger(this.getClass().getName()).error("Exception occured: - ", e);
		}
	}
	
	private Boolean fetchStackTrace;
	public Boolean getFetchStackTrace() {
		if (fetchStackTrace == null) {
			return false;
		}
		return fetchStackTrace;
	}
	public void setFetchStackTrace(Boolean fetchStackTrace) {
		this.fetchStackTrace = fetchStackTrace;
	}

	private String viewName;
	public String getViewName() {
		return viewName;
	}
	public void setViewName(String viewName) {
		this.viewName = viewName;
	}
	
	private String filters;
	public String getFilters() {
		return filters;
	}
	public void setFilters(String filters) {
		this.filters = filters;
	}
	
	private String search;
	public void setSearch(String search) {
		this.search = search;
	}
	public String getSearch() {
		return this.search;
	}
	
	private String searchFields;
	public void setSearchFields(String searchFields) {
		this.searchFields = searchFields;
	}
	public String getSearchFields() {
		return this.searchFields;
	}
	
	private int page;
	public void setPage(int page) {
		this.page = page;
	}
	
	public int getPage() {
		return this.page;
	}
	
	private int perPage = -1;
	public void setPerPage(int perPage) {
		this.perPage = perPage;
	}
	
	public int getPerPage() {
		return this.perPage;
	}
	
	@SuppressWarnings("unchecked")
	public JSONObject getPagination () {
		if (getPage() != 0) {
			JSONObject pagination = new JSONObject();
			pagination.put("page", getPage());
			pagination.put("perPage", getPerPage());
			return pagination;
		}
		return null;
	}
	
	private String contentType="application/octet-stream";
	public String getContentType() {
		return contentType;
	}
	protected void setContentType(String contentType) {
		this.contentType = contentType;
	}
	
	private String filename;
	public String getFilename() {
		return filename;
	}
	public void setFilename(String filename) {
		this.filename = filename;
	}
	

}
