package com.facilio.workflows.functions;

import com.facilio.bmsconsole.commands.FacilioChainFactory;
import com.facilio.bmsconsole.util.AssetsAPI;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.scriptengine.annotation.ScriptNameSpace;
import com.facilio.scriptengine.context.ScriptContext;
import com.facilio.scriptengine.exceptions.FunctionParamException;
import com.facilio.scriptengine.systemfunctions.FacilioNameSpaceConstants;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@ScriptNameSpace(nameSpace = FacilioNameSpaceConstants.ASSET_FUNCTION)
public class FacilioAssetFunctions {
	public Object getAssetsFromSpaceId(ScriptContext scriptContext,  Map<String, Object> globalParam, Object... objects) throws Exception {

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
	}

	public Object getAssetCategoryFields(ScriptContext scriptContext, Map<String, Object> globalParam, Object... objects) throws Exception {

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
	}

	public void checkParam(Object... objects) throws Exception {
		if(objects.length <= 0) {
			throw new FunctionParamException("Required Object is null");
		}
	}
}
