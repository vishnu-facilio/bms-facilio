package com.facilio.bmsconsole.actions;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.db.ResponseCacheUtil;
import com.facilio.bmsconsole.exception.DependencyException;
import com.facilio.bmsconsole.interceptors.AuthInterceptor;
import com.facilio.bmsconsole.util.AuditLogUtil;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.constants.FacilioConstants.ContextNames;
import com.facilio.exception.ErrorResponseUtil;
import com.facilio.filters.MultiReadServletRequest;
import com.facilio.fw.FacilioException;
import com.facilio.ims.handler.AuditLogHandler;
import com.facilio.util.FacilioUtil;
import com.facilio.v3.exception.ErrorCode;
import com.facilio.v3.exception.RESTException;
import com.opensymphony.xwork2.ActionSupport;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.struts2.ServletActionContext;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.InvocationTargetException;
import java.sql.SQLDataException;
import java.sql.SQLException;
import java.util.List;

@Log4j
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
	@Getter @Setter
	private String correctiveAction;
	@Getter @Setter
	private Boolean isErrorGeneralized = false;
	@Getter @Setter
	private Boolean isVisible = false;

	private JSONObject errorData;
	public String getErrorData() {
		return (errorData != null) ? errorData.toJSONString() : null;
	}
	public void setErrorData(JSONObject errorData) {
		this.errorData = errorData;
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
		try {
			this.responseCode = 1;
			if (exception instanceof RESTException) {
					ErrorResponseUtil values = new ErrorResponseUtil((RESTException) exception);
					this.responseCode = ((RESTException) exception).getErrorCode().getCode();
					this.message = values.getMessage();
					this.errorData = ((RESTException) exception).getData();
					this.correctiveAction = values.getCorrectiveAction();
					this.responseCode = values.getErrorCode().getCode();
					this.setIsErrorGeneralized(values.getIsErrorGeneralized());
					this.setIsVisible(values.getIsVisible());
				} else if(exception instanceof SQLException || exception instanceof SQLDataException) {
				this.responseCode = ErrorCode.VALIDATION_ERROR.getCode();
				this.message = ((SQLException) exception).getLocalizedMessage();
			}
			else {
					this.message = FacilioUtil.constructMessageFromException(exception);
					if(exception instanceof DependencyException) {
						this.responseCode = ((DependencyException) exception).getErrorCode().getCode();
						this.errorData = ((DependencyException) exception).getData();
					}
			}
			setStackTrace(exception);
		} catch (Exception e) {
			LogManager.getLogger(this.getClass().getName()).error("Exception occurred inside handle Exception: - ", e);
		}
		if (sendErrorCode && (
						this.message != FacilioUtil.ERROR_MESSAGE // != is valid here. So 400 if it's valid error or SQL Exception. 500 otherwise
					||	exception instanceof SQLException
					)
		) {
			return INPUT;
		}
		return ERROR;
	}

	private boolean sendErrorCode = false;
	public boolean isSendErrorCode() {
		return sendErrorCode;
	}
	public void setSendErrorCode(boolean sendErrorCode) {
		this.sendErrorCode = sendErrorCode;
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
	
	// value in  seconds
	public void setExpiry(String value) {
		setCacheControl("max-age=" + value);
	}
	public void setDayExpiry() {
		setExpiry("86400");
	}
	public void setCacheControl(String value) {
		setHeader("Cache-Control", value);
	}
	public void setLastModified(String value) {
		setHeader("Last-Modified", value);
	}
	
	public void setHeader(String name, String value) {
		HttpServletResponse response = ServletActionContext.getResponse();
		response.setHeader(name, value);
	}
	
	protected String getHeader(String name) {
		HttpServletResponse response = ServletActionContext.getResponse();
		return response.getHeader(name);
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

	private static final int MAX_LENGTH_OF_TRACE = 5000;
	public void setStackTrace(Exception e) {
		if (e != null) {
			if (e instanceof FacilioException || e.getCause() instanceof FacilioException) {
				LOGGER.debug("Exception occured: - ", e);
			}
			else {
				LOGGER.error("Exception occured: - ", e);
			}
			if(getFetchStackTrace() || AuthInterceptor.isPuppeteerRequest()) {
				Throwable msg = e;
				if (e instanceof InvocationTargetException) {
					msg = e.getCause();
				}
				this.stackTrace = StringUtils.abbreviate(ExceptionUtils.getStackTrace(msg), MAX_LENGTH_OF_TRACE) ;
//				System.out.println(this.stackTrace);
			}
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

	@Getter @Setter
	public boolean skipModuleCriteria = false;
	
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
	
	private boolean overrideSorting;
	
	public boolean isOverrideSorting() {
		return overrideSorting;
	}
	public void setOverrideSorting(boolean overrideSorting) {
		this.overrideSorting = overrideSorting;
	}
	protected FacilioContext constructListContext(FacilioContext context) throws Exception {
 		context.put(FacilioConstants.ContextNames.CV_NAME, getViewName());
 		if (getPage() == 0) {
 			setPage(1);
 		}
 		if (getPerPage() == -1) {
 			setPerPage(5000);
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
 		context.put(ContextNames.OVERRIDE_SORTING, isOverrideSorting());
 		context.put(ContextNames.SKIP_MODULE_CRITERIA, skipModuleCriteria);
 		
 		return context;
	}

	public boolean isDebug() {
		return debug;
	}
	public void setDebug(boolean debug) {
		this.debug = debug;
		if (debug) {
			setLoggerLevel(2);
		}
	}
	private boolean debug;

	public int getLoggerLevel() {
		return loggerLevel;
	}

	public void setLoggerLevel(int loggerLevel) {
		Logger.getLogger(FacilioAction.class.getName()).info(loggerLevel);
		this.loggerLevel = loggerLevel;
		if (AccountUtil.getCurrentAccount() != null) {
			AccountUtil.getCurrentAccount().setLoggerLevel(loggerLevel);
		}
	}
	private int loggerLevel = -1;

	protected void sendAuditLogs(AuditLogHandler.AuditLogContext auditLog) {
		AuditLogUtil.sendAuditLogs(auditLog);
	}

}
