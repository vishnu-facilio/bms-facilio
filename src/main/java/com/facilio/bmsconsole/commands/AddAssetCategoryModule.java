package com.facilio.bmsconsole.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.AssetCategoryContext;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.bmsconsole.modules.FacilioModule.ModuleType;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import java.util.Collections;

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
		module.setTrashEnabled(true);
		
		context.put(FacilioConstants.ContextNames.MODULE_LIST, Collections.singletonList(module));
		return false;
	}

}
