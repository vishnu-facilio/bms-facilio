package com.facilio.workflows.functions;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.amazonaws.util.IOUtils;
import com.facilio.ftp.FTPUtil;
import com.facilio.services.factory.FacilioFactory;
import com.facilio.services.filestore.FileStore;

public enum FacilioFileFunction implements FacilioWorkflowFunctionInterface  {

	GET_FILE_CONTENT(1,"getFileContent") {
		@Override
		public Object execute(Map<String, Object> globalParam, Object... objects) throws Exception {
			
			Long fileId = (long) Double.parseDouble(objects[0].toString());
			FileStore fs = FacilioFactory.getFileStore();
			
			
			try (InputStream is = fs.readFile(fileId)) {
	            String content = IOUtils.toString(is);
	            return content;
	        }
			catch(Exception e) {
				throw new RuntimeException("Cannot Read File :: "+fileId,e);
			}
		};
	},
	
	PUT_FILE_TO_REMOTE_FS(1,"putFileToRemoteFileServer") {
		@Override
		public Object execute(Map<String, Object> globalParam, Object... objects) throws Exception {
			
			String stringContent = objects[5].toString();
			
			InputStream is = new ByteArrayInputStream(stringContent.getBytes());
			
			FTPUtil.putFile(objects[0].toString(), objects[1].toString(), objects[2].toString(), objects[3].toString(), is, objects[4].toString());
			
			return null;
		};
	},
	
	;
	
	private Integer value;
	private String functionName;
	private String namespace = "file";
	private String params;
	private FacilioSystemFunctionNameSpace nameSpaceEnum = FacilioSystemFunctionNameSpace.FILE;
	
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
	FacilioFileFunction(Integer value,String functionName) {
		this.value = value;
		this.functionName = functionName;
	}
	public static Map<String, FacilioFileFunction> getAllFunctions() {
		return MATH_FUNCTIONS;
	}
	public static FacilioFileFunction getFacilioFileFunction(String functionName) {
		return MATH_FUNCTIONS.get(functionName);
	}
	static final Map<String, FacilioFileFunction> MATH_FUNCTIONS = Collections.unmodifiableMap(initTypeMap());
	static Map<String, FacilioFileFunction> initTypeMap() {
		Map<String, FacilioFileFunction> typeMap = new HashMap<>();
		for(FacilioFileFunction type : FacilioFileFunction.values()) {
			typeMap.put(type.getFunctionName(), type);
		}
		return typeMap;
	}
}
