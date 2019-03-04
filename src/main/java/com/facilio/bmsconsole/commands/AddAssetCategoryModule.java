package com.facilio.bmsconsole.commands;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.AssetCategoryContext;
import com.facilio.bmsconsole.criteria.CriteriaAPI;
import com.facilio.bmsconsole.criteria.NumberOperators;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.bmsconsole.modules.FieldUtil;
import com.facilio.bmsconsole.modules.FacilioModule.ModuleType;
import com.facilio.bmsconsole.modules.FieldFactory;
import com.facilio.bmsconsole.modules.ModuleBaseWithCustomFields;
import com.facilio.bmsconsole.modules.SelectRecordsBuilder;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;

public class AddAssetCategoryModule implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		AssetCategoryContext assetCategory = (AssetCategoryContext) context.get(FacilioConstants.ContextNames.RECORD);
		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule assetModule = modBean.getModule("asset");
		
//		long parentCategoryId = assetCategory.getParentCategoryId();
//		if (parentCategoryId > 0) {
//			List<FacilioField> assetCategoryFields = modBean.getAllFields("assetcategory");
//			FacilioModule assetCategoryModule = modBean.getModule("assetcategory");
//			SelectRecordsBuilder<AssetCategoryContext> selectRecordsBuilder = new SelectRecordsBuilder<AssetCategoryContext>()
//					.module(assetCategoryModule)
//					.beanClass(AssetCategoryContext.class)
//					.select(assetCategoryFields)
//					.andCondition(CriteriaAPI.getCondition(FieldFactory.getIdField(assetCategoryModule), String.valueOf(parentCategoryId), NumberOperators.EQUALS));
//			AssetCategoryContext parentCategory = selectRecordsBuilder.get().get(0);
//			assetModule = modBean.getModule(parentCategory.getAssetModuleID());
//		}
//		
//		if (assetModule == null) {
//			 assetModule = modBean.getModule("asset");
//		}
		
		String name = assetCategory.getName();
		
		FacilioModule module = new FacilioModule();
		module.setName(name.toLowerCase().replaceAll("[^a-zA-Z0-9]+",""));
		module.setDisplayName(name);
		module.setTableName("AssetCustomModuleData");
		module.setType(ModuleType.BASE_ENTITY);
		module.setExtendModule(assetModule);
		
		context.put(FacilioConstants.ContextNames.MODULE_LIST, Collections.singletonList(module));
		return false;
	}

}
