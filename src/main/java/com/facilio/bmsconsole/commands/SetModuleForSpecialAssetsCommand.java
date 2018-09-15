package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.google.common.collect.Multimap;
import com.google.common.collect.ArrayListMultimap;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.facilio.bmsconsole.context.AssetCategoryContext;
import com.facilio.bmsconsole.context.ReadingContext;
import com.facilio.bmsconsole.util.AssetsAPI;
import com.facilio.bmsconsole.util.ImportAPI;
import com.facilio.constants.FacilioConstants;

public class SetModuleForSpecialAssetsCommand implements Command{

	private static final Logger LOGGER = LogManager.getLogger(SetModuleForSpecialAssetsCommand.class.getName());
	
	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		
		Integer bulkSetting = (Integer) context.get("bulkSetting");
		long startTime = System.currentTimeMillis();
		
		if(bulkSetting == null) {
			Long categoryId = (Long)context.get(FacilioConstants.ContextNames.PARENT_CATEGORY_ID);
			Map<String,String> moduleInfo= getModuleName(categoryId);
			context.put(FacilioConstants.ContextNames.MODULE_NAME, moduleInfo.get(FacilioConstants.ContextNames.MODULE_NAME));
			context.put(FacilioConstants.ContextNames.MODULE_DATA_TABLE_NAME,moduleInfo.get(FacilioConstants.ContextNames.MODULE_DATA_TABLE_NAME));			
			
		}
		else {
			ArrayListMultimap<String, ReadingContext> categoryBasedAsset = (ArrayListMultimap<String,ReadingContext>)context.get("categoryBasedAsset");
			List<String> assetCategoryNames = new ArrayList(categoryBasedAsset.keySet());
			Map<String, Map<String,String>> modulesInfo = new HashMap<>();
			
			
			for(int i=0;i<assetCategoryNames.size();i++) {
				if(assetCategoryNames.get(i).equals(ImportAPI.ImportProcessConstants.NO_CATEGORY_DEFINED)) {
					Map<String,String> moduleInfo = getModuleName(null);
					modulesInfo.put(ImportAPI.ImportProcessConstants.NO_CATEGORY_DEFINED, moduleInfo);
				}
				else {
				ReadingContext readingContext= categoryBasedAsset.get(assetCategoryNames.get(i)).get(0);
				Map<String,Object> categoryInfo= (Map<String, Object>) readingContext.getData().get(ImportAPI.ImportProcessConstants.CATEGORY_FROM_CONTEXT);
				Long categoryId = (Long)categoryInfo.get(ImportAPI.ImportProcessConstants.ID_FIELD);
				String categoryName = assetCategoryNames.get(i);
				Map<String,String> moduleInfo = getModuleName(categoryId);
				modulesInfo.put(categoryName, moduleInfo);
				}
			}
			context.put(ImportAPI.ImportProcessConstants.MODULES_INFO, modulesInfo);
		}

		long timeTaken = System.currentTimeMillis() - startTime;
		LOGGER.debug("Time taken to execute SetModuleForSpecialAssetsCommand : "+timeTaken);
		return false;
	}
	
	public static Map<String,String> getModuleName(Long categoryId) throws Exception{
		Map<String,String> moduleInfo = new HashMap<String,String>();
		if (categoryId == null || categoryId <= 0)
		{	
			moduleInfo.put(FacilioConstants.ContextNames.MODULE_NAME, "asset");
			moduleInfo.put(FacilioConstants.ContextNames.MODULE_DATA_TABLE_NAME, "Assets");
		}
		else 
		{
			AssetCategoryContext assetCategory = AssetsAPI.getCategoryForAsset(categoryId);
			String assetCategoryName = assetCategory.getName();
			if(assetCategoryName.trim().equalsIgnoreCase("Energy Meter"))
			{
				moduleInfo.put(FacilioConstants.ContextNames.MODULE_NAME, FacilioConstants.ContextNames.ENERGY_METER);
				moduleInfo.put(FacilioConstants.ContextNames.MODULE_DATA_TABLE_NAME, "Energy_Meter");
			}
			else if(assetCategoryName.trim().equalsIgnoreCase("Chiller"))
			{
				moduleInfo.put(FacilioConstants.ContextNames.MODULE_NAME, FacilioConstants.ContextNames.CHILLER);
				moduleInfo.put(FacilioConstants.ContextNames.MODULE_DATA_TABLE_NAME, "Chiller");
			}
			else if(assetCategoryName.trim().equalsIgnoreCase("Primary Pump"))
			{
				moduleInfo.put(FacilioConstants.ContextNames.MODULE_NAME, FacilioConstants.ContextNames.CHILLER_PRIMARY_PUMP);
				moduleInfo.put(FacilioConstants.ContextNames.MODULE_DATA_TABLE_NAME, "Chiller_Primary_Pump");
			}
			else if(assetCategoryName.trim().equalsIgnoreCase("Secondary Pump"))
			{
				moduleInfo.put(FacilioConstants.ContextNames.MODULE_NAME, FacilioConstants.ContextNames.CHILLER_SECONDARY_PUMP);
				moduleInfo.put(FacilioConstants.ContextNames.MODULE_DATA_TABLE_NAME, "Chiller_Secondary_Pump");
			}
			else if(assetCategoryName.trim().equalsIgnoreCase("Condenser Pump"))
			{
				moduleInfo.put(FacilioConstants.ContextNames.MODULE_NAME, FacilioConstants.ContextNames.CHILLER_CONDENSER_PUMP);
				moduleInfo.put(FacilioConstants.ContextNames.MODULE_DATA_TABLE_NAME, "Chiller_Condenser_Pump");
			}
			else if(assetCategoryName.trim().equalsIgnoreCase("AHU"))
			{
				moduleInfo.put(FacilioConstants.ContextNames.MODULE_NAME, FacilioConstants.ContextNames.AHU);
				moduleInfo.put(FacilioConstants.ContextNames.MODULE_DATA_TABLE_NAME, "AHU");
			}
			else if(assetCategoryName.trim().equalsIgnoreCase("Cooling Tower"))
			{
				moduleInfo.put(FacilioConstants.ContextNames.MODULE_NAME, FacilioConstants.ContextNames.COOLING_TOWER);
				moduleInfo.put(FacilioConstants.ContextNames.MODULE_DATA_TABLE_NAME, "Cooling_Tower");
			}
			else if(assetCategoryName.trim().equalsIgnoreCase("FCU"))
			{
				moduleInfo.put(FacilioConstants.ContextNames.MODULE_NAME, FacilioConstants.ContextNames.FCU);
				moduleInfo.put(FacilioConstants.ContextNames.MODULE_DATA_TABLE_NAME, "FCU");
			}
			else if(assetCategoryName.trim().equalsIgnoreCase("Heat Pump"))
			{
				moduleInfo.put(FacilioConstants.ContextNames.MODULE_NAME, FacilioConstants.ContextNames.HEAT_PUMP);
				moduleInfo.put(FacilioConstants.ContextNames.MODULE_DATA_TABLE_NAME, "Heat_Pump");
			}
			else if(assetCategoryName.trim().equalsIgnoreCase("Utility Meter"))
			{
				moduleInfo.put(FacilioConstants.ContextNames.MODULE_NAME, FacilioConstants.ContextNames.UTILITY_METER);
				moduleInfo.put(FacilioConstants.ContextNames.MODULE_DATA_TABLE_NAME, "Utility_Meters");
			}
			else
			{
				moduleInfo.put(FacilioConstants.ContextNames.MODULE_NAME, "asset");
				moduleInfo.put(FacilioConstants.ContextNames.MODULE_DATA_TABLE_NAME, "Assets");
			}
		}
		
	return moduleInfo;
	}
}