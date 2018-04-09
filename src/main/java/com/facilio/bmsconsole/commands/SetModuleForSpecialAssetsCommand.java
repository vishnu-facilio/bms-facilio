package com.facilio.bmsconsole.commands;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.AssetCategoryContext;
import com.facilio.bmsconsole.util.AssetsAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;

public class SetModuleForSpecialAssetsCommand implements Command{

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		
		long categoryId = (long)context.get(FacilioConstants.ContextNames.PARENT_CATEGORY_ID);
		AssetCategoryContext assetCategory = AssetsAPI.getCategoryForAsset(categoryId);
		String assetName = assetCategory.getName();
		if(assetName.trim().equalsIgnoreCase("Energy Meter"))
		{
			context.put(FacilioConstants.ContextNames.MODULE_NAME, "energymeter");
			context.put(FacilioConstants.ContextNames.MODULE_DATA_TABLE_NAME, "Energy_Meter");
		}
		else if(assetName.trim().equalsIgnoreCase("Chiller"))
		{
			context.put(FacilioConstants.ContextNames.MODULE_NAME, "chiller");
			context.put(FacilioConstants.ContextNames.MODULE_DATA_TABLE_NAME, "Chiller");
		}
		else if(assetName.trim().equalsIgnoreCase("AHU"))
		{
			context.put(FacilioConstants.ContextNames.MODULE_NAME, "ahu");
			context.put(FacilioConstants.ContextNames.MODULE_DATA_TABLE_NAME, "AHU");
		}
		else
		{
			context.put(FacilioConstants.ContextNames.MODULE_NAME, "asset");
			context.put(FacilioConstants.ContextNames.MODULE_DATA_TABLE_NAME, "Assets");
		}
		return false;
	}
}
