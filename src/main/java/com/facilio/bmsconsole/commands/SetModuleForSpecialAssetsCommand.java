package com.facilio.bmsconsole.commands;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.facilio.bmsconsole.context.AssetCategoryContext;
import com.facilio.bmsconsole.util.AssetsAPI;
import com.facilio.constants.FacilioConstants;

public class SetModuleForSpecialAssetsCommand implements Command{

	private static final Logger LOGGER = LogManager.getLogger(SetModuleForSpecialAssetsCommand.class.getName());
	
	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		
		long startTime = System.currentTimeMillis();
		Long categoryId = (Long)context.get(FacilioConstants.ContextNames.PARENT_CATEGORY_ID);
		if (categoryId == null || categoryId <= 0)
		{
			context.put(FacilioConstants.ContextNames.MODULE_NAME, "asset");
			context.put(FacilioConstants.ContextNames.MODULE_DATA_TABLE_NAME, "Assets");
		}
		else 
		{
			AssetCategoryContext assetCategory = AssetsAPI.getCategoryForAsset(categoryId);
			String assetCategoryName = assetCategory.getName();
			if(assetCategoryName.trim().equalsIgnoreCase("Energy Meter"))
			{
				context.put(FacilioConstants.ContextNames.MODULE_NAME, FacilioConstants.ContextNames.ENERGY_METER);
				context.put(FacilioConstants.ContextNames.MODULE_DATA_TABLE_NAME, "Energy_Meter");
			}
			else if(assetCategoryName.trim().equalsIgnoreCase("Chiller"))
			{
				context.put(FacilioConstants.ContextNames.MODULE_NAME, FacilioConstants.ContextNames.CHILLER);
				context.put(FacilioConstants.ContextNames.MODULE_DATA_TABLE_NAME, "Chiller");
			}
			else if(assetCategoryName.trim().equalsIgnoreCase("Primary Pump"))
			{
				context.put(FacilioConstants.ContextNames.MODULE_NAME, FacilioConstants.ContextNames.CHILLER_PRIMARY_PUMP);
				context.put(FacilioConstants.ContextNames.MODULE_DATA_TABLE_NAME, "Chiller_Primary_Pump");
			}
			else if(assetCategoryName.trim().equalsIgnoreCase("Secondary Pump"))
			{
				context.put(FacilioConstants.ContextNames.MODULE_NAME, FacilioConstants.ContextNames.CHILLER_SECONDARY_PUMP);
				context.put(FacilioConstants.ContextNames.MODULE_DATA_TABLE_NAME, "Chiller_Secondary_Pump");
			}
			else if(assetCategoryName.trim().equalsIgnoreCase("Condenser Pump"))
			{
				context.put(FacilioConstants.ContextNames.MODULE_NAME, FacilioConstants.ContextNames.CHILLER_CONDENSER_PUMP);
				context.put(FacilioConstants.ContextNames.MODULE_DATA_TABLE_NAME, "Chiller_Condenser_Pump");
			}
			else if(assetCategoryName.trim().equalsIgnoreCase("AHU"))
			{
				context.put(FacilioConstants.ContextNames.MODULE_NAME, FacilioConstants.ContextNames.AHU);
				context.put(FacilioConstants.ContextNames.MODULE_DATA_TABLE_NAME, "AHU");
			}
			else if(assetCategoryName.trim().equalsIgnoreCase("Cooling Tower"))
			{
				context.put(FacilioConstants.ContextNames.MODULE_NAME, FacilioConstants.ContextNames.COOLING_TOWER);
				context.put(FacilioConstants.ContextNames.MODULE_DATA_TABLE_NAME, "Cooling_Tower");
			}
			else if(assetCategoryName.trim().equalsIgnoreCase("FCU"))
			{
				context.put(FacilioConstants.ContextNames.MODULE_NAME, FacilioConstants.ContextNames.FCU);
				context.put(FacilioConstants.ContextNames.MODULE_DATA_TABLE_NAME, "FCU");
			}
			else if(assetCategoryName.trim().equalsIgnoreCase("Heat Pump"))
			{
				context.put(FacilioConstants.ContextNames.MODULE_NAME, FacilioConstants.ContextNames.HEAT_PUMP);
				context.put(FacilioConstants.ContextNames.MODULE_DATA_TABLE_NAME, "Heat_Pump");
			}
			else if(assetCategoryName.trim().equalsIgnoreCase("Utility Meter"))
			{
				context.put(FacilioConstants.ContextNames.MODULE_NAME, FacilioConstants.ContextNames.UTILITY_METER);
				context.put(FacilioConstants.ContextNames.MODULE_DATA_TABLE_NAME, "Utility_Meters");
			}
			else
			{
				context.put(FacilioConstants.ContextNames.MODULE_NAME, "asset");
				context.put(FacilioConstants.ContextNames.MODULE_DATA_TABLE_NAME, "Assets");
			}
		}
		long timeTaken = System.currentTimeMillis() - startTime;
		LOGGER.debug("Time taken to execute SetModuleForSpecialAssetsCommand : "+timeTaken);
		return false;
	}
}
