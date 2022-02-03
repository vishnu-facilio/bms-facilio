package com.facilio.v3;

import com.facilio.aws.util.FacilioProperties;
import com.facilio.bmsconsole.util.AuditLogUtil;
import com.facilio.util.FacilioUtil;
import com.facilio.v3.exception.ErrorCode;
import com.facilio.v3.exception.RESTException;

import com.facilio.wmsv2.handler.AuditLogHandler;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.log4j.LogManager;
import org.apache.struts2.ServletActionContext;
import org.apache.struts2.interceptor.ServletResponseAware;
import org.json.simple.JSONObject;

import com.opensymphony.xwork2.ActionSupport;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

@Log4j
public class V3Action extends ActionSupport implements ServletResponseAware {

	protected enum api {
		v3,
		v4
	}

	protected api currentApi() {
		return api.v3;
	}

    private JSONObject data;
	private JSONObject meta;
	private JSONObject params;
	private int code = 0;
	private String message;

	public JSONObject getData() {
		return this.data;
	}

	public void setData(JSONObject data) {
		this.data = data;
	}

	public void setData(String key, Object result) {
		if (this.data == null) {
			this.data = new JSONObject();
		}
		this.data.put(key, result);
	}

	private String moduleName;

	public String getModuleName() {
		return moduleName;
	}

	public void setModuleName(String moduleName) {
		this.moduleName = moduleName;
	}

	private long id;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	private String search;

	public String getSearch() {
		return search;
	}

	public void setSearch(String search) {
		this.search = search;
	}

	private String qrValue;
	public void setQrValue(String qrValue) {
		this.qrValue = qrValue;
	}
	public String getQrValue() {
		return qrValue;
	}

	private String filters;

	public String getFilters() {
		return filters;
	}

	public void setFilters(String filters) {
		this.filters = filters;
	}

	private String viewName;

	public String getViewName() {
		return viewName;
	}

	public void setViewName(String viewName) {
		this.viewName = viewName;
	}

	private boolean excludeParentFilter;
	public boolean getExcludeParentFilter() {
		return excludeParentFilter;
	}
	public void setExcludeParentFilter(boolean excludeParentFilter) {
		this.excludeParentFilter = excludeParentFilter;
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

	private int page = 1;
	public void setPage(int page) {
		this.page = page;
	}
	public int getPage() {
		return this.page;
	}

	private int perPage = 50;
	public void setPerPage(int perPage) {
		this.perPage = perPage;
	}
	public int getPerPage() {
		return this.perPage;
	}

	private boolean withCount;
	public boolean getWithCount() {
		return this.withCount;
	}

	public void setWithCount(boolean withCount) {
		this.withCount = withCount;
	}

	public JSONObject getMeta() {
		return meta;
	}
	public void setMeta(JSONObject meta) {
		this.meta = meta;
	}
	public void setMeta (String key, Object value) {
		if (this.meta == null) {
			this.meta = new JSONObject();
		}
		meta.put(key, value);
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	private Long stateTransitionId;
	public Long getStateTransitionId() {
		return stateTransitionId;
	}
	public void setStateTransitionId(Long transitionId) {
		this.stateTransitionId = transitionId;
	}

	private Long customButtonId;
	public Long getCustomButtonId() {
		return customButtonId;
	}
	public void setCustomButtonId(Long customButtonId) {
		this.customButtonId = customButtonId;
	}

	private Long approvalTransitionId;
	public Long getApprovalTransitionId() {
		return approvalTransitionId;
	}
	public void setApprovalTransitionId(Long approvalTransitionId) {
		this.approvalTransitionId = approvalTransitionId;
	}

	public JSONObject getParams() {
		return params;
	}

	public void setParams(JSONObject params) {
		this.params = params;
	}

	@Getter @Setter
	private List<File> files;

	@Getter @Setter
	private List<String> filesFileName;

	@Getter @Setter
	private List<String> filesContentType;

	private List<String> fileNames;
	public List<String> getFileNames() {
		return fileNames;
	}

	public void setFileNames(List<String> fileNames) {
		this.fileNames = fileNames;
	}

	private List<String> contentTypes;
	public List<String> getContentTypes() {
		return contentTypes;
	}

	public void setContentTypes(List<String> contentTypes) {
		this.contentTypes = contentTypes;
	}

	private String stackTrace;
	public String getStackTrace() {
		return stackTrace;
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
	
	private static final int MAX_LENGTH_OF_TRACE = 5000;
	public void setStackTrace(Exception e) {
		if (e != null) {
			if((!FacilioProperties.isProduction() && !FacilioProperties.isOnpremise()) || getFetchStackTrace()) {
				Throwable msg = e;
				if (e instanceof InvocationTargetException) {
					msg = e.getCause();
				}
				this.stackTrace = StringUtils.abbreviate(ExceptionUtils.getStackTrace(msg), MAX_LENGTH_OF_TRACE) ;
			}
		}
	}
	
	private Exception exception;
	public Exception getException() {
		return exception;
	}
	public void setException(Exception exception) {
		this.exception = exception;
	}
	
	protected HttpServletResponse httpServletResponse;
	@Override
	public void setServletResponse(HttpServletResponse httpServletResponse) {
		this.httpServletResponse = httpServletResponse;
	}
	public HttpServletResponse getHttpServletResponse() {
		return httpServletResponse;
	}
	
	public String handleException() {
		ErrorCode errorCode;
		String message;
		if (exception instanceof RESTException) {
			RESTException ex = (RESTException) exception;
			errorCode = ex.getErrorCode();
			message = ex.getMessage();
			this.setData(ex.getData());
		} else if (exception instanceof IllegalArgumentException) {
			errorCode =  ErrorCode.VALIDATION_ERROR;
			message = exception.getMessage();
			if (StringUtils.isEmpty(message)) {
				message = "Error occurred due to some validation";
			}
			this.setData(null);
		} else {
			errorCode =  ErrorCode.UNHANDLED_EXCEPTION;
			message = "Internal Server Error";
			this.setData(null);
		}

		this.setCode(errorCode.getCode());
		this.setMessage(message);
		this.httpServletResponse.setStatus(errorCode.getHttpStatus());

		this.setStackTrace(exception);
		LOGGER.error("Exception handling v3 api, moduleName: " + getModuleName(), exception);

		return ERROR;
	}

	protected void sendAuditLogs(AuditLogHandler.AuditLogContext auditLog) {
		AuditLogUtil.sendAuditLogs(auditLog);
	}
}
