package com.facilio.workflows.functions;

import com.facilio.bmsconsole.util.AssetsAPI;
import com.facilio.workflows.exceptions.FunctionParamException;

import java.util.*;

public enum FacilioAssetFunctions implements FacilioWorkflowFunctionInterface {

	GET_ASSETS_FROM_SPACE_ID(1,"getAssetsFromSpaceId") {
		@Override
		public Object execute(Object... objects) throws Exception {
			
			if(objects.length > 1) {
				
				Long assetCategoryId = Long.parseLong(objects[1].toString());
				if(objects[0] instanceof List) {
					return AssetsAPI.getAssetIdsFromBaseSpaceIdsWithCategory(new ArrayList((List)objects[0]),Collections.singletonList(assetCategoryId));
				}
				else {
					Long spaceID = Long.parseLong(objects[0].toString());
					return AssetsAPI.getAssetIdsFromBaseSpaceIdsWithCategory(Collections.singletonList(spaceID),Collections.singletonList(assetCategoryId));
				}
			}
			else {
				if(objects[0] instanceof List) {
					return AssetsAPI.getAssetIdsFromBaseSpaceIds(new ArrayList((List)objects[0]));
				}
				else {
					Long spaceID = Long.parseLong(objects[0].toString());
					return AssetsAPI.getAssetIdsFromBaseSpaceIds(Collections.singletonList(spaceID));
				}
			}
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
	private FacilioSystemFunctionNameSpace nameSpaceEnum = FacilioSystemFunctionNameSpace.ASSET;
	
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
	public static FacilioAssetFunctions getFacilioAssetFunction(String functionName) {
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
