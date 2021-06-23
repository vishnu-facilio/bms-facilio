package com.facilio.bmsconsole.commands;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.AssetCategoryContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.UpdateRecordBuilder;
import com.facilio.modules.fields.FacilioField;

;

public class UpdateCategoryAssetModuleIdCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		AssetCategoryContext assetCategory = (AssetCategoryContext) context.get(FacilioConstants.ContextNames.RECORD);
		FacilioModule module = ((List<FacilioModule>) context.get(FacilioConstants.ContextNames.MODULE_LIST)).get(0);
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
