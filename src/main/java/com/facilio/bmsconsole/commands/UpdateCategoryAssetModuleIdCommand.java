package com.facilio.bmsconsole.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.AssetCategoryContext;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.modules.fields.FacilioField;;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.*;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class UpdateCategoryAssetModuleIdCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		AssetCategoryContext assetCategory = (AssetCategoryContext) context.get(FacilioConstants.ContextNames.RECORD);
		FacilioModule module = (FacilioModule) ((List<FacilioModule>) context.get(FacilioConstants.ContextNames.MODULE_LIST)).get(0);
		assetCategory.setAssetModuleID(module.getModuleId());
		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule assetCategoryModule = modBean.getModule("assetcategory");
		
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(modBean.getAllFields(ModuleFactory.getAssetCategoryModule().getName()));
		UpdateRecordBuilder<AssetCategoryContext> updateBuilder = new UpdateRecordBuilder<AssetCategoryContext>()
				.module(assetCategoryModule)
				.fields(Collections.singletonList(fieldMap.get("assetModuleID")))
				.andCondition(CriteriaAPI.getIdCondition(assetCategory.getId(), assetCategoryModule));
		updateBuilder.update(assetCategory);

		return false;
	}

}
