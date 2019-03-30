package com.facilio.bmsconsole.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.AssetCategoryContext;
import com.facilio.bmsconsole.context.AssetContext;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.bmsconsole.util.AssetsAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

public class SetAssetCategoryCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		AssetContext asset = (AssetContext) context.get(FacilioConstants.ContextNames.RECORD);
		String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
		if (moduleName != null && !moduleName.equals(FacilioConstants.ContextNames.ASSET) ) {
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			FacilioModule module = modBean.getModule(moduleName);
			AssetCategoryContext categoryFromModule = AssetsAPI.getCategoryByAssetModule(module.getModuleId());
			if (categoryFromModule != null) {
				AssetCategoryContext assetCategory = new AssetCategoryContext();
				long categoryId = categoryFromModule.getId();
				assetCategory.setId(categoryId);
				asset.setCategory(assetCategory);
				context.put(FacilioConstants.ContextNames.PARENT_CATEGORY_ID, categoryId);
			}
		}
		return false;
	}

}
