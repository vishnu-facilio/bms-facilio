package com.facilio.v3;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.interceptors.AuthInterceptor;
import com.facilio.bmsconsole.util.AuditLogUtil;
import com.facilio.v3.exception.ErrorCode;
import com.facilio.v3.exception.RESTException;
import com.facilio.wmsv2.handler.AuditLogHandler;
import com.opensymphony.xwork2.ActionSupport;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.log4j.Logger;
import org.apache.struts2.interceptor.ServletResponseAware;
import org.json.simple.JSONObject;
import com.facilio.exception.ErrorResponseUtil;


import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.sql.SQLDataException;
import java.sql.SQLException;
import java.util.List;

@Log4j
public class V3Action extends ActionSupport implements ServletResponseAware {

	protected enum api {
		v3,
		v4
	}

	protected api currentApi() {
		return api.v3;
	}

	public boolean isFetchGlimpseMetaFields() {
		return fetchGlimpseMetaFields;
	}

	public void setFetchGlimpseMetaFields(boolean fetchGlimpseMetaFields) {
		this.fetchGlimpseMetaFields = fetchGlimpseMetaFields;
	}

	private boolean fetchGlimpseMetaFields;
    private JSONObject data;
	private JSONObject meta;
	private JSONObject params;
	private int code = 0;
	@Getter @Setter
	private String correctiveAction;
	@Getter @Setter
	private Boolean isErrorGeneralized;
	@Getter @Setter
	private Boolean isVisible;
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
	@Getter @Setter
	private String locationValue;

	private String filters;

	public String getFilters() {
		return filters;
	}

	public void setFilters(String filters) {
		this.filters = filters;
	}
	private String quickFilter;

	public String getQuickFilter() {
		return quickFilter;
	}

	public void setQuickFilter(String quickFilter) {
		this.quickFilter = quickFilter;
	}

	private String clientCriteria;

	public void setClientCriteria(String clientCriteria) {
		this.clientCriteria = clientCriteria;
	}

	public String getClientCriteria() {
		return clientCriteria;
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

	private boolean withoutCustomButtons;

	public void setWithoutCustomButtons(boolean withoutCustomButtons) {
		this.withoutCustomButtons = withoutCustomButtons;
	}

	public boolean getWithoutCustomButtons() {
		return withoutCustomButtons;
	}

	private boolean fetchOnlyViewGroupColumn;

	public void setFetchOnlyViewGroupColumn(boolean fetchOnlyViewGroupColumn) {
		this.fetchOnlyViewGroupColumn = fetchOnlyViewGroupColumn;
	}

	public boolean getFetchOnlyViewGroupColumn() {
		return fetchOnlyViewGroupColumn;
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
			if(getFetchStackTrace() || AuthInterceptor.isPuppeteerRequest()) {
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
	
	public String handleException() throws Exception{
		ErrorCode errorCode = null;
		String message = StringUtils.EMPTY;
		ErrorResponseUtil values = null;
		Boolean errRestType = false;
		if (exception instanceof RESTException) {
			errRestType = true;
			values = new ErrorResponseUtil((RESTException) exception);
			this.setData(values.getData());
		} else if (exception instanceof IllegalArgumentException) {
			errorCode =  ErrorCode.VALIDATION_ERROR;
			message = exception.getMessage();
			if (StringUtils.isEmpty(message)) {
				message = "Error occurred due to some validation";
			}
			this.setData(null);
		} else if(exception instanceof SQLException || exception instanceof SQLDataException) {
			errorCode =  ErrorCode.VALIDATION_ERROR;
			message = ((SQLException)exception).getLocalizedMessage();
		}
		else {
			errorCode =  ErrorCode.UNHANDLED_EXCEPTION;
			message = "Internal Server Error";
			this.setData(null);
		}

		this.setCode(errRestType  ? values.getErrorCode().getCode() : errorCode.getCode());
		this.setMessage(errRestType ? values.getMessage(): message);
		this.setCorrectiveAction(errRestType ? values.getCorrectiveAction() : StringUtils.EMPTY);
		this.setIsErrorGeneralized(errRestType ? values.getIsErrorGeneralized() : false );
		this.setIsVisible(errRestType ? values.getIsVisible() : false);
		this.httpServletResponse.setStatus(errRestType ?  values.getErrorCode().getHttpStatus() : errorCode.getHttpStatus());


		this.setStackTrace(exception);
		LOGGER.error("Exception handling v3 api, moduleName: " + getModuleName(), exception);

		return ERROR;
	}

	protected void sendAuditLogs(AuditLogHandler.AuditLogContext auditLog) {
		AuditLogUtil.sendAuditLogs(auditLog);
	}

	public int getLoggerLevel() {
		return loggerLevel;
	}

	public void setLoggerLevel(int loggerLevel) {
		Logger.getLogger(V3Action.class.getName()).info(loggerLevel);
		this.loggerLevel = loggerLevel;
		if (AccountUtil.getCurrentAccount() != null) {
			AccountUtil.getCurrentAccount().setLoggerLevel(loggerLevel);
		}
	}
	private int loggerLevel = -1;
}
