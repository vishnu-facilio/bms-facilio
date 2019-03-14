package com.facilio.bmsconsole.commands;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.context.AssetCategoryContext;
import com.facilio.bmsconsole.context.AssetContext;
import com.facilio.bmsconsole.util.AssetsAPI;
import com.facilio.constants.FacilioConstants;

public class SetAssetCategoryCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		AssetContext asset = (AssetContext) context.get(FacilioConstants.ContextNames.RECORD);
		String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
		if (moduleName != null && !moduleName.equals(FacilioConstants.ContextNames.ASSET) ) {
			AssetCategoryContext categoryFromModule = AssetsAPI.getCategory(moduleName);
			if (categoryFromModule != null) {
				AssetCategoryContext assetCategory = new AssetCategoryContext();
				assetCategory.setId(categoryFromModule.getId());
				asset.setCategory(assetCategory);
			}
		}
		return false;
	}

}
