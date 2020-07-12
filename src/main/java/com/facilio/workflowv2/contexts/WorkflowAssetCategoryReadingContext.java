package com.facilio.workflowv2.contexts;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.facilio.bmsconsole.commands.FacilioChainFactory;
import com.facilio.bmsconsole.context.AssetCategoryContext;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;

public class WorkflowAssetCategoryReadingContext extends WorkflowCategoryReadingContext {

	AssetCategoryContext category;
	
	public WorkflowAssetCategoryReadingContext(AssetCategoryContext category,List<Long> parentIDs) {
		setCategory(category);
		setParentIds(parentIDs);
	}
	
	public AssetCategoryContext getCategory() {
		return category;
	}

	public void setCategory(AssetCategoryContext category) {
		this.category = category;
	}
	
	@Override
	public FacilioField getField(String fieldName) throws Exception {
		FacilioChain getCategoryReadingChain = FacilioChainFactory.getCategoryReadingsChain();
		FacilioContext context = getCategoryReadingChain.getContext();
		context.put(FacilioConstants.ContextNames.CATEGORY_READING_PARENT_MODULE, ModuleFactory.getAssetCategoryReadingRelModule());
		context.put(FacilioConstants.ContextNames.PARENT_CATEGORY_ID, getCategory().getId());
		
		getCategoryReadingChain.execute();
		
		List<FacilioModule> readings = (List<FacilioModule>) context.get(FacilioConstants.ContextNames.MODULE_LIST);
		
		Map<String,FacilioField> fieldMap = new HashMap<>();
		if (readings != null) {
			for(FacilioModule reading :readings) {
				for(FacilioField readingFields :reading.getFields()) {
					fieldMap.put(readingFields.getName(), readingFields);
				}
			}
		}
		
		FacilioField field = fieldMap.get(fieldName);
		
		if(field == null) {
			throw new Exception("field does not exist.");
		}
		return field;
	}

}
