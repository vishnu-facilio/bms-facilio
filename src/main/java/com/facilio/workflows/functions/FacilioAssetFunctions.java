package com.facilio.workflows.functions;

import java.util.ArrayList;
import com.facilio.scriptengine.systemfunctions.FacilioSystemFunctionNameSpace;
import com.facilio.scriptengine.systemfunctions.FacilioWorkflowFunctionInterface;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import com.facilio.bmsconsole.commands.FacilioChainFactory;
import com.facilio.bmsconsole.util.AssetsAPI;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.scriptengine.exceptions.FunctionParamException;

public enum FacilioAssetFunctions implements FacilioWorkflowFunctionInterface {

	GET_ASSETS_FROM_SPACE_ID(1,"getAssetsFromSpaceId") {
		@Override
		public Object execute(Map<String, Object> globalParam, Object... objects) throws Exception {
			
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
	},
	GET_ASSETS_CATEGORY_FIELDS(1,"getAssetCategoryFields") {
		@Override
		public Object execute(Map<String, Object> globalParam, Object... objects) throws Exception {
			
			long assetCategoryID = -1;
			if(objects[0] instanceof String) {
				
				assetCategoryID = AssetsAPI.getCategory((String)objects[0]).getId();
			}
			else {
				assetCategoryID = Long.parseLong(objects[0].toString());
			}
			
			FacilioChain getCategoryReadingChain = FacilioChainFactory.getCategoryReadingsChain();
			
			FacilioContext context = getCategoryReadingChain.getContext();
			context.put(FacilioConstants.ContextNames.CATEGORY_READING_PARENT_MODULE, ModuleFactory.getAssetCategoryReadingRelModule());
			context.put(FacilioConstants.ContextNames.PARENT_CATEGORY_ID, assetCategoryID);
			
			getCategoryReadingChain.execute();
			
			List<FacilioModule> readings = (List<FacilioModule>) context.get(FacilioConstants.ContextNames.MODULE_LIST);
			
			List<FacilioField> fields = new ArrayList<>();
			if (readings != null) {
				fields = readings.stream().map(r -> r.getFields()).flatMap(r -> r.stream()).collect(Collectors.toList());
			}
			
			return FieldUtil.getAsMapList(fields, FacilioField.class);
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
