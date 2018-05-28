package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.criteria.CriteriaAPI;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.bmsconsole.modules.FieldFactory;
import com.facilio.bmsconsole.modules.ModuleFactory;
import com.facilio.constants.FacilioConstants;
import com.facilio.sql.GenericSelectRecordBuilder;

public class ValidateAssetCategoryDeletion implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		long recordID = (Long) context.get(FacilioConstants.ContextNames.RECORD_ID);
		
		FacilioModule assetModule = ModuleFactory.getAssetsModule();
		GenericSelectRecordBuilder assetSelectBuilder = new GenericSelectRecordBuilder()
				.select(Arrays.asList(FieldFactory.getIdField(assetModule)))
				.table(assetModule.getTableName())
				.andCondition(CriteriaAPI.getCurrentOrgIdCondition(assetModule))
				.andCustomWhere("CATEGORY = ?", recordID)
				.limit(1);
		List<Map<String, Object>> result = assetSelectBuilder.get();
		List<String> moduleNames = new ArrayList<>();
		
		boolean stopChain = false;
		if (!result.isEmpty()) {
			stopChain = true;
			moduleNames.add(assetModule.getName());
		}
		
		FacilioModule readingRuleModule = ModuleFactory.getReadingRuleModule();
		GenericSelectRecordBuilder readingRuleSelectBuilder = new GenericSelectRecordBuilder()
				.select(Arrays.asList(FieldFactory.getIdField(readingRuleModule)))
				.table(readingRuleModule.getTableName())
				.andCustomWhere("ASSET_CATEGORY_ID = ?", recordID).limit(1);
		result = readingRuleSelectBuilder.get();
		
		if (!result.isEmpty()) {
			stopChain = true;
			moduleNames.add(readingRuleModule.getDisplayName());
		}
		
		FacilioModule assetCategoryModule = ModuleFactory.getAssetCategoryModule();
		GenericSelectRecordBuilder assetCategorySelectBuilder = new GenericSelectRecordBuilder()
				.select(Arrays.asList(FieldFactory.getIdField(assetCategoryModule)))
				.table(assetCategoryModule.getTableName())
				.andCondition(CriteriaAPI.getCurrentOrgIdCondition(assetCategoryModule))
				.andCustomWhere("PARENT_CATEGORY_ID = ?", recordID)
				.limit(1);
		result = assetCategorySelectBuilder.get();
		
		if (!result.isEmpty()) {
			stopChain = true;
			moduleNames.add(assetCategoryModule.getDisplayName());
		}
		
		FacilioModule assetReadingModule = ModuleFactory.getAssetCategoryReadingRelModule();
		GenericSelectRecordBuilder assetReadingModuleSelectBuilder = new GenericSelectRecordBuilder()
				.select(FieldFactory.getAssetCategoryReadingRelFields())
				.table(assetReadingModule.getTableName())
				.andCustomWhere("PARENT_CATEGORY_ID = ?", recordID)
				.limit(1);
		result = assetReadingModuleSelectBuilder.get();
		
		if (!result.isEmpty()) {
			stopChain = true;
			moduleNames.add(assetReadingModule.getDisplayName());
		}
				
		
		
		context.put(FacilioConstants.ContextNames.MODULE_NAMES, moduleNames);
		return stopChain;
	}


}
