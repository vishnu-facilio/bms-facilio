package com.facilio.bmsconsole.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.AssetCategoryContext;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.NumberOperators;
import com.facilio.bmsconsole.modules.*;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.commons.collections.CollectionUtils;

import java.util.*;

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

		// TODO - check this
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		SelectRecordsBuilder<AssetCategoryContext> selectRecordsBuilder = new SelectRecordsBuilder<AssetCategoryContext>()
				.beanClass(AssetCategoryContext.class)
				.moduleName(assetCategoryModule.getName())
				.select(modBean.getAllFields(assetCategoryModule.getName()))
				.andCondition(CriteriaAPI.getIdCondition(recordID, assetCategoryModule));
		List<AssetCategoryContext> list = selectRecordsBuilder.get();
		if (CollectionUtils.isNotEmpty(list)) {
			long assetModuleId = list.get(0).getAssetModuleID();
			GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
					.table("SubModulesRel")
					.select(Collections.singletonList(FieldFactory.getField("parentModuleId", "PARENT_MODULE_ID", FieldType.NUMBER)))
					.andCondition(CriteriaAPI.getCondition("PARENT_MODULE_ID", "parentModuleId", String.valueOf(assetModuleId), NumberOperators.EQUALS));
			result = builder.get();
			if (!result.isEmpty()) {
				stopChain = true;
				moduleNames.add("Asset Readings");
			}
		}
		
		context.put(FacilioConstants.ContextNames.MODULE_NAMES, moduleNames);
		return stopChain;
	}


}
