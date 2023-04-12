package com.facilio.workflows.functions;

import java.io.ByteArrayInputStream;
import com.facilio.scriptengine.systemfunctions.FacilioSystemFunctionNameSpace;
import com.facilio.scriptengine.systemfunctions.FacilioWorkflowFunctionInterface;
import java.io.InputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import com.facilio.fs.FileInfo;
import com.facilio.modules.FieldUtil;

import com.amazonaws.util.IOUtils;
import com.facilio.ftp.SFTPUtil;
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
	
	PUT_FILE_TO_REMOTE_FS(2,"putFileToRemoteFileServer") {
		@Override
		public Object execute(Map<String, Object> globalParam, Object... objects) throws Exception {
			
			String stringContent = objects[5].toString();
			
			try(InputStream is = new ByteArrayInputStream(stringContent.getBytes())) {
				
				SFTPUtil.putFile((String)objects[0], objects[1].toString(), objects[2].toString(), objects[3].toString(), is, objects[4].toString());
			}
			
			return null;
		};
	},
	
	GET_FILE_FROM_REMOTE_FS(3,"getFileFromRemoteFileServer") {
		@Override
		public Object execute(Map<String, Object> globalParam, Object... objects) throws Exception {
			
			String filePath = objects[4].toString();
				
			String result = SFTPUtil.getFile((String)objects[0], objects[1].toString(), objects[2].toString(), objects[3].toString(), filePath);
			
			FileStore fs = FacilioFactory.getFileStore();
			
			long fileId = fs.addFile(objects[5].toString(), result, objects[6].toString());
			
			return fileId;
		};
	},

	GET_FILE_INFO(4,"getFileInfo") {
		@Override
		public Object execute(Map<String, Object> globalParam, Object... objects) throws Exception {

			Long fileId = (long) Double.parseDouble(objects[0].toString());
			FileStore fs = FacilioFactory.getFileStore();

			FileInfo fileInfo = fs.getFileInfo(fileId);
			if (fileInfo != null) {
				return FieldUtil.getAsProperties(fileInfo);
			}
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
