package com.facilio.workflows.functions;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.facilio.bmsconsole.util.AssetsAPI;
import com.facilio.workflows.exceptions.FunctionParamException;
import com.facilio.workflows.util.FunctionUtil;

public enum FacilioAssetFunctions implements FacilioWorkflowFunctionInterface {

	GET_ASSETS_FROM_SPACE_ID(1,"getAssetsFromSpaceId") {
		@Override
		public Object execute(Object... objects) throws Exception {
			
			Long spaceID = Long.parseLong(objects[0].toString());
			return AssetsAPI.getAssetIdsFromBaseSpaceIds(Collections.singletonList(spaceID));
		};
		
		public void checkParam(Object... objects) throws Exception {
			if(objects.length <= 0) {
				throw new FunctionParamException("Required Object is null");
			}
		}
	}
	;
	private Integer value;
	private String functionName;
	private String namespace = "asset";
	private String params;
	private FacilioFunctionNameSpace nameSpaceEnum = FacilioFunctionNameSpace.ASSET;
	
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
	FacilioAssetFunctions(Integer value,String functionName) {
		this.value = value;
		this.functionName = functionName;
	}
	public static Map<String, FacilioAssetFunctions> getAllFunctions() {
		return ASSET_FUNCTIONS;
	}
	public static FacilioAssetFunctions getFacilioCostFunction(String functionName) {
		return ASSET_FUNCTIONS.get(functionName);
	}
	static final Map<String, FacilioAssetFunctions> ASSET_FUNCTIONS = Collections.unmodifiableMap(initTypeMap());
	static Map<String, FacilioAssetFunctions> initTypeMap() {
		Map<String, FacilioAssetFunctions> typeMap = new HashMap<>();
		for(FacilioAssetFunctions type : FacilioAssetFunctions.values()) {
			typeMap.put(type.getFunctionName(), type);
		}
		return typeMap;
	}
}
