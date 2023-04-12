package com.facilio.workflows.functions;

import java.util.Collections;
import com.facilio.scriptengine.systemfunctions.FacilioSystemFunctionNameSpace;
import com.facilio.scriptengine.systemfunctions.FacilioWorkflowFunctionInterface;
import java.util.HashMap;
import java.util.Map;

import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.FieldUtil;
import com.facilio.report.context.ReportContext;
import com.facilio.report.util.ReportUtil;
import com.facilio.scriptengine.exceptions.FunctionParamException;

public enum FacilioAnalyticsFunctions implements FacilioWorkflowFunctionInterface {

	
	GET_CRITERIA(1,"getData") {
		@Override
		public Object execute(Map<String, Object> globalParam, Object... objects) throws Exception {
			
			HashMap<String,Object> dataObjectMap = (HashMap<String,Object>)objects[0];

			FacilioContext context =  ReportUtil.getReportData(dataObjectMap);
			if(context.get(FacilioConstants.ContextNames.REPORT) != null) {
				ReportContext reportContext = (ReportContext) context.get(FacilioConstants.ContextNames.REPORT);
				context.put("report", FieldUtil.getAsProperties(reportContext));
			}
			FieldUtil.getAsProperties(context);
			
			return context;
		};
		
		public void checkParam(Object... objects) throws Exception {
			if(objects.length <= 0) {
				throw new FunctionParamException("Required Object is null");
			}
		}
	},
	;
	
	private Integer value;
	private String functionName;
	private String namespace = "analytics";
	private String params;
	private FacilioSystemFunctionNameSpace nameSpaceEnum = FacilioSystemFunctionNameSpace.ANALYTICS;
	
	public Integer getValue() {
		return value;
	}
	public void setValue(Integer value) {
		this.value = value;
	}
	public String getFunctionName() {
		return functionName;
	}
	public void setFunctionName(String functionName) {
		this.functionName = functionName;
	}
	public String getNamespace() {
		return namespace;
	}
	public void setNamespace(String namespace) {
		this.namespace = namespace;
	}
	public String getParams() {
		return params;
	}
	public void setParams(String params) {
		this.params = params;
	}
	FacilioAnalyticsFunctions(Integer value,String functionName) {
		this.value = value;
		this.functionName = functionName;
	}
	public static Map<String, FacilioAnalyticsFunctions> getAllFunctions() {
		return WORKORDER_FUNCTIONS;
	}
	public static FacilioAnalyticsFunctions getFacilioAnalyticsFunction(String functionName) {
		return WORKORDER_FUNCTIONS.get(functionName);
	}
	static final Map<String, FacilioAnalyticsFunctions> WORKORDER_FUNCTIONS = Collections.unmodifiableMap(initTypeMap());
	static Map<String, FacilioAnalyticsFunctions> initTypeMap() {
		Map<String, FacilioAnalyticsFunctions> typeMap = new HashMap<>();
		for(FacilioAnalyticsFunctions type : FacilioAnalyticsFunctions.values()) {
			typeMap.put(type.getFunctionName(), type);
		}
		return typeMap;
	}
				
}