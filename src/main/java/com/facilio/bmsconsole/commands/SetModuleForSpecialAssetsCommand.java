package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.facilio.bmsconsole.context.ReadingContext;
import com.facilio.bmsconsole.util.AssetsAPI;
import com.facilio.bmsconsole.util.ImportAPI;
import com.facilio.constants.FacilioConstants;
import com.google.common.collect.ArrayListMultimap;

public class SetModuleForSpecialAssetsCommand implements Command{

	private static final Logger LOGGER = LogManager.getLogger(SetModuleForSpecialAssetsCommand.class.getName());
	
	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		
		Integer bulkSetting = (Integer) context.get("bulkSetting");
		long startTime = System.currentTimeMillis();
		String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
		if (moduleName != null) {
			return false;
		}
		
		if(bulkSetting == null) {
			Long categoryId = (Long)context.get(FacilioConstants.ContextNames.PARENT_CATEGORY_ID);
			Map<String,String> moduleInfo= AssetsAPI.getAssetModuleName(categoryId);
			context.put(FacilioConstants.ContextNames.MODULE_NAME, moduleInfo.get(FacilioConstants.ContextNames.MODULE_NAME));
			context.put(FacilioConstants.ContextNames.MODULE_DATA_TABLE_NAME,moduleInfo.get(FacilioConstants.ContextNames.MODULE_DATA_TABLE_NAME));			
			
		}
		else {
			ArrayListMultimap<String, ReadingContext> categoryBasedAsset = (ArrayListMultimap<String,ReadingContext>)context.get("categoryBasedAsset");
			List<String> assetCategoryNames = new ArrayList(categoryBasedAsset.keySet());
			Map<String, Map<String,String>> modulesInfo = new HashMap<>();
			
			
			for(int i=0;i<assetCategoryNames.size();i++) {
				if(assetCategoryNames.get(i).equals(ImportAPI.ImportProcessConstants.NO_CATEGORY_DEFINED)) {
					Map<String,String> moduleInfo = AssetsAPI.getAssetModuleName(null);
					modulesInfo.put(ImportAPI.ImportProcessConstants.NO_CATEGORY_DEFINED, moduleInfo);
				}
				else {
				ReadingContext readingContext= categoryBasedAsset.get(assetCategoryNames.get(i)).get(0);
				Map<String,Object> categoryInfo= (Map<String, Object>) readingContext.getData().get(ImportAPI.ImportProcessConstants.CATEGORY_FROM_CONTEXT);
				Long categoryId = (Long)categoryInfo.get(ImportAPI.ImportProcessConstants.ID_FIELD);
				String categoryName = assetCategoryNames.get(i);
				Map<String,String> moduleInfo = AssetsAPI.getAssetModuleName(categoryId);
				modulesInfo.put(categoryName, moduleInfo);
				}
			}
			context.put(ImportAPI.ImportProcessConstants.MODULES_INFO, modulesInfo);
		}

		long timeTaken = System.currentTimeMillis() - startTime;
		LOGGER.debug("Time taken to execute SetModuleForSpecialAssetsCommand : "+timeTaken);
		return false;
	}
}