package com.facilio.workflows.functions;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONObject;

import com.amazonaws.HttpMethod;
import com.amazonaws.util.IOUtils;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.context.ConnectionContext;
import com.facilio.bmsconsole.util.ConnectionUtil;
import com.facilio.bmsconsole.util.ExportUtil;
import com.facilio.cb.context.ChatBotConfirmContext;
import com.facilio.cb.context.ChatBotExecuteContext;
import com.facilio.cb.context.ChatBotParamContext;
import com.facilio.db.criteria.Criteria;
import com.facilio.services.FacilioHttpUtils;
import com.facilio.services.factory.FacilioFactory;
import com.facilio.services.filestore.FileStore;
import com.facilio.workflows.exceptions.FunctionParamException;

public enum FacilioHTTPFunctions implements FacilioWorkflowFunctionInterface {

	GET(1,"get") {
		@Override
		public Object execute(Map<String, Object> globalParam, Object... objects) throws Exception {
			
			checkParam(objects);
			
			String url = (String) objects[0];
			
			Map<String,String> params = null;
			Map<String,String> headers = null;
			if(objects.length > 1) {
				params = (Map<String, String>) objects[1];
				if(objects.length > 2) {
					headers = (Map<String, String>) objects[2];
				}
			}
			
			String res = FacilioHttpUtils.doHttpGet(url, headers, params);
			
			return res;
		};
		
		public void checkParam(Object... objects) throws Exception {
			if(objects.length <= 0) {
				throw new FunctionParamException("Required Object is null");
			}
		}
	},
	POST(2,"post") {
		@Override
		public Object execute(Map<String, Object> globalParam, Object... objects) throws Exception {
			
			checkParam(objects);
			
			String url = (String) objects[0];
			
			Map<String,String> params = null;
			Map<String,String> headers = null;
			String body = null;
			if(objects.length > 1) {
				params = (Map<String, String>) objects[1];
				if(objects.length > 2) {
					headers = (Map<String, String>) objects[2];
					if(objects.length > 3) {
						body = (String) objects[3];
					}
				}
				
			}
			
			String res = FacilioHttpUtils.doHttpPost(url, headers, params, body);
			
			return res;
		};
		
		public void checkParam(Object... objects) throws Exception {
			if(objects.length <= 0) {
				throw new FunctionParamException("Required Object is null");
			}
		}
	},
	GET_BLOB_AS_FILE(3,"getBlobFile") {
		@Override
		public Object execute(Map<String, Object> globalParam, Object... objects) throws Exception {
			
			checkParam(objects);
			
			String url = (String) objects[0];
			
			Map<String,String> params = (Map<String, String>) objects[1];
			Map<String,String> headers = (Map<String, String>) objects[2];
			
			String fileName = (String) objects[3];
			String type = (String) objects[4];
			
			return FacilioHttpUtils.doHttpGetWithFileResponse(url, headers, params, fileName, type);
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
	private String namespace = "http";
	private List<FacilioFunctionsParamType> params;
	private FacilioSystemFunctionNameSpace nameSpaceEnum = FacilioSystemFunctionNameSpace.HTTP;
	
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
	public List<FacilioFunctionsParamType> getParams() {
		return params;
	}
	public void setParams(List<FacilioFunctionsParamType> params) {
		this.params = params;
	}
	public void addParams(FacilioFunctionsParamType param) {
		this.params = (this.params == null) ? new ArrayList<>() :this.params;
		this.params.add(param);
	}
	FacilioHTTPFunctions(Integer value,String functionName,FacilioFunctionsParamType... params) {
		this.value = value;
		this.functionName = functionName;
		
		if(params != null ) {
			for(int i=0;i<params.length;i++) {
				addParams(params[i]);
			}
		}
	}
	
	public static Map<String, FacilioHTTPFunctions> getAllFunctions() {
		return MODULE_FUNCTIONS;
	}
	public static FacilioHTTPFunctions getFacilioHTTPFunctions(String functionName) {
		return MODULE_FUNCTIONS.get(functionName);
	}
	static final Map<String, FacilioHTTPFunctions> MODULE_FUNCTIONS = Collections.unmodifiableMap(initTypeMap());
	static Map<String, FacilioHTTPFunctions> initTypeMap() {
		Map<String, FacilioHTTPFunctions> typeMap = new HashMap<>();
		for(FacilioHTTPFunctions type : FacilioHTTPFunctions.values()) {
			typeMap.put(type.getFunctionName(), type);
		}
		return typeMap;
	}
}