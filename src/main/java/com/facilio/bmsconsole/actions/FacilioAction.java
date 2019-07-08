package com.facilio.bmsconsole.actions;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.struts2.ServletActionContext;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.aws.util.AwsUtil;
import com.facilio.chain.FacilioContext;
import com.facilio.bmsconsole.db.ResponseCacheUtil;
import com.facilio.cache.CacheUtil;
import com.facilio.constants.FacilioConstants;
import com.facilio.filters.MultiReadServletRequest;
import com.facilio.fw.LRUCache;
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
			this.message = exception.getMessage();
		}
		else {
			this.message = FacilioConstants.ERROR_MESSAGE;
		}
		setStackTrace(exception);
		return ERROR;
	}
	
	public String cachedResponse() throws Exception {
		HttpServletRequest request = ServletActionContext.getRequest();
		if (request instanceof HttpServletRequestWrapper && ((MultiReadServletRequest) request).isCachedRequest()) {
				MultiReadServletRequest multiReadServletRequest = (MultiReadServletRequest) request;
				String requestURI = multiReadServletRequest.getRequestURI();
		        String contentHash = multiReadServletRequest.getContentHash();
				long userId = AccountUtil.getCurrentUser().getId();
				long orgId = AccountUtil.getCurrentOrg().getId();
				
				Object object = ResponseCacheUtil.getCache(orgId, userId, requestURI, contentHash);
				
				HttpServletResponse response = ServletActionContext.getResponse();
				response.setContentType("application/json;charset=UTF-8");
				response.getWriter().write(object.toString());
				response.getWriter().flush();
		}
		return NONE;
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
	
	private boolean includeParentFilter;
	public boolean getIncludeParentFilter() {
		return includeParentFilter;
	}
	public void setIncludeParentFilter(boolean includeParentFilter) {
		this.includeParentFilter = includeParentFilter;
	}
	
	private Boolean fetchCount;
	public boolean isFetchCount() {
		if (fetchCount == null) {
			return false;
		}
		return fetchCount;
	}
	public void setFetchCount(boolean fetchCount) {
		this.fetchCount = fetchCount;
	}
	
	private String orderBy;
	public void setOrderBy(String orderBy) {
		this.orderBy = orderBy;
	}
	public String getOrderBy() {
		return this.orderBy;
	}
	
	private String orderType;
	public void setOrderType(String orderType) {
		this.orderType = orderType;
	}
	public String getOrderType() {
		return this.orderType;
	}
	
	protected FacilioContext constructListContext() throws Exception {
		FacilioContext context = new FacilioContext();
 		context.put(FacilioConstants.ContextNames.CV_NAME, getViewName());
 		if (getPage() == 0) {
 			setPage(1);
 		}
 		if (getPerPage() == -1) {
 			setPerPage(50);
 		}
 		context.put(FacilioConstants.ContextNames.PAGINATION, getPagination());
 		
 		if(getFilters() != null)
 		{	
	 		JSONParser parser = new JSONParser();
	 		JSONObject json = (JSONObject) parser.parse(getFilters());
	 		context.put(FacilioConstants.ContextNames.FILTERS, json);
	 		context.put(FacilioConstants.ContextNames.INCLUDE_PARENT_CRITERIA, getIncludeParentFilter());
 		}
 		
 		if (isFetchCount()) {
			context.put(FacilioConstants.ContextNames.FETCH_COUNT, isFetchCount());
		}
 		
 		JSONObject sorting = null;
 		if (getOrderBy() != null) {
 			sorting = new JSONObject();
 			sorting.put("orderBy", getOrderBy());
 			sorting.put("orderType", getOrderType());
 		}
 		context.put(FacilioConstants.ContextNames.SORTING, sorting);
 		
 		return context;
	}

	public boolean isDebug() {
		return debug;
	}
	public void setDebug(boolean debug) {
		this.debug = debug;
		setLoggerLevel(2);
	}
	private boolean debug;

	public int getLoggerLevel() {
		return loggerLevel;
	}
	public void setLoggerLevel(int loggerLevel) {
		this.loggerLevel = loggerLevel;
		Level level = Level.INFO;
		switch (loggerLevel) {
			case 0:
				level = Level.ALL;
				break;
			case 1:
				level = Level.TRACE;
				break;
			case 2:
				level = Level.DEBUG;
				break;
			case 3:
				level = Level.INFO;
				break;
			case 4:
				level = Level.WARN;
				break;
			case 5:
				level = Level.ERROR;
				break;
			case 6:
				level = Level.FATAL;
				break;
			case 7:
				level = Level.OFF;
				break;
			default:
				break;
		}
		if (AccountUtil.getCurrentAccount() != null) {
			AccountUtil.getCurrentAccount().setLevel(level);
		}
	}
	private int loggerLevel = -1;

}
