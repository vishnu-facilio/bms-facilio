package com.facilio.workflows.functions;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import com.facilio.scriptengine.systemfunctions.FacilioSystemFunctionNameSpace;
import com.facilio.scriptengine.systemfunctions.FacilioWorkflowFunctionInterface;
import com.facilio.service.FacilioHttpUtilsFW;
import com.facilio.services.factory.FacilioFactory;
import com.facilio.services.filestore.FileStore;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.io.FileUtils;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.amazonaws.HttpMethod;
import com.facilio.bmsconsole.context.ConnectionContext;
import com.facilio.bmsconsole.util.ConnectionUtil;
import com.facilio.fs.FileInfo;
import com.facilio.modules.FieldUtil;
import com.facilio.scriptengine.exceptions.FunctionParamException;

public enum FacilioConnectionFunctions implements FacilioWorkflowFunctionInterface {

	GET(1,"get") {
		@Override
		public Object execute(Map<String, Object> globalParam, Object... objects) throws Exception {
			
			checkParam(objects);
			
			if(objects[0] == null) {
				return null;
			}
			ConnectionContext connectionContext = (ConnectionContext)objects[0];
			
			String url = (String) objects[1];
			
			Map<String,String> params = null;
			Map<String,String> headerParams = null;
			String bodyString = null;
			String bodyType = null;
			
			if(objects.length >2) {
				params = (Map<String, String>) objects[2];
			}
			if(objects.length >3) {
				headerParams = (Map<String, String>) objects[3];
			}
			
			String res = ConnectionUtil.getUrlResult(connectionContext, url, params, HttpMethod.GET,bodyString,bodyType,headerParams,null);
			
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
			
			if(objects[0] == null) {
				return null;
			}
			ConnectionContext connectionContext = (ConnectionContext)objects[0];
			
			String url = (String) objects[1];
			
			String bodyString = null;
			String bodyType = null;
			Map<String,String> params = null;
			Map<String,String> headerParams = null;
			if(objects.length == 3) {
				params = (Map<String, String>) objects[2];
			}
			else if (objects.length >= 4) {
				bodyString = objects[2].toString();
				bodyType = objects[3].toString();
				if(objects.length == 5) {
					headerParams = (Map<String, String>) objects[4];
				}
			}

			String res = ConnectionUtil.getUrlResult(connectionContext, url, params, HttpMethod.POST,bodyString,bodyType,headerParams,null);
			
			return res;
		};
		
		public void checkParam(Object... objects) throws Exception {
			if(objects.length <= 0) {
				throw new FunctionParamException("Required Object is null");
			}
		}
	},
	POSTWITHFILES(3,"postWithFiles") {
		@Override
		public Object execute(Map<String, Object> globalParam, Object... objects) throws Exception {
			
			checkParam(objects);
			
			if(objects[0] == null) {
				return null;
			}
			ConnectionContext connectionContext = (ConnectionContext)objects[0];
			
			String url = (String) objects[1];
			
			Map<String,String> headers = null;
			Map<String,String> params = null;
			Map<String,File> files = null;
			if(objects.length > 1) {
				headers = (Map<String, String>) objects[2];
				if(objects.length > 2) {
					params = (Map<String, String>) objects[3];
					if(objects.length > 3) {
						Map<String,Long> fileIdMap = (Map<String,Long>) objects[4];
						
						if(fileIdMap != null) {
							files = new HashMap<String, File>();
							
							FileStore fs = FacilioFactory.getFileStore();
							
							for(Entry<String, Long> set : fileIdMap.entrySet()) {
								 FileInfo fileInfo = fs.getFileInfo(set.getValue());
								InputStream ipStream = fs.readFile(set.getValue());
								File file = new File(fileInfo.getFileName());
								FileUtils.copyInputStreamToFile(ipStream, file);
								files.put(set.getKey(), file);
							}
						}
					}
				}
				
			}
			
			String res = ConnectionUtil.getUrlResult(connectionContext, url, params, HttpMethod.POST,null,null,headers,files);
			
			return res;
		};
		
		public void checkParam(Object... objects) throws Exception {
			if(objects.length <= 0) {
				throw new FunctionParamException("Required Object is null");
			}
		}
	},
	GET_ACCESS_TOKEN(4,"getAccessToken") {
		@Override
		public Object execute(Map<String, Object> globalParam, Object... objects) throws Exception {
			
			checkParam(objects);
			
			if(objects[0] == null) {
				return null;
			}
			ConnectionContext connectionContext = (ConnectionContext)objects[0];
			
			return connectionContext.getAccessToken();
		};
		
		public void checkParam(Object... objects) throws Exception {
			if(objects.length <= 0) {
				throw new FunctionParamException("Required Object is null");
			}
		}
	},
	AS_MAP(5,"asMap") {
		@Override
		public Object execute(Map<String, Object> globalParam, Object... objects) throws Exception {
			
			checkParam(objects);
			
			if(objects[0] == null) {
				return null;
			}
			ConnectionContext connectionContext = (ConnectionContext)objects[0];
			
			return FieldUtil.getAsProperties(connectionContext);
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
	private String namespace = "connection";
	private List<FacilioFunctionsParamType> params;
	private FacilioSystemFunctionNameSpace nameSpaceEnum = FacilioSystemFunctionNameSpace.CONNECTION;
	
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
	FacilioConnectionFunctions(Integer value,String functionName,FacilioFunctionsParamType... params) {
		this.value = value;
		this.functionName = functionName;
		
		if(params != null ) {
			for(int i=0;i<params.length;i++) {
				addParams(params[i]);
			}
		}
	}
	
	public static Map<String, FacilioConnectionFunctions> getAllFunctions() {
		return CONNECTION_FUNCTIONS;
	}
	public static FacilioConnectionFunctions getFacilioConnectionFunctions(String functionName) {
		return CONNECTION_FUNCTIONS.get(functionName);
	}
	static final Map<String, FacilioConnectionFunctions> CONNECTION_FUNCTIONS = Collections.unmodifiableMap(initTypeMap());
	static Map<String, FacilioConnectionFunctions> initTypeMap() {
		Map<String, FacilioConnectionFunctions> typeMap = new HashMap<>();
		for(FacilioConnectionFunctions type : FacilioConnectionFunctions.values()) {
			typeMap.put(type.getFunctionName(), type);
		}
		return typeMap;
	}
}