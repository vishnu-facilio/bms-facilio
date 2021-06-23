package com.facilio.bmsconsole.commands;

import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.AssetCategoryContext;
import com.facilio.bmsconsole.context.AssetContext;
import com.facilio.bmsconsole.util.AssetsAPI;
import com.facilio.bmsconsole.util.SpaceAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;

public class SetAssetCategoryCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		AssetContext asset = (AssetContext) context.get(FacilioConstants.ContextNames.RECORD);
		if(asset != null) {
			if(asset.getSiteId() > 0) {
				asset.setIdentifiedLocation(SpaceAPI.getSiteSpace(asset.getSiteId()));
			}
			if(asset.getSpace() != null) {
				asset.setCurrentSpaceId(asset.getSpace().getId());
			}
		}
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
