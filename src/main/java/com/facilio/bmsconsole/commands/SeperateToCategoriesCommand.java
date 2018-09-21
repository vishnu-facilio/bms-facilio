package com.facilio.bmsconsole.commands;

import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.context.ReadingContext;
import com.facilio.bmsconsole.util.ImportAPI;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

public class SeperateToCategoriesCommand implements Command{
	
	@Override
	public boolean execute(Context context) throws Exception{
		
		List<ReadingContext> readingsList = (List<ReadingContext>) context.get(ImportAPI.ImportProcessConstants.READINGS_LIST);
		Map<String,String> fieldMapping  = (Map<String,String>)context.get(ImportAPI.ImportProcessConstants.FIELDS_MAPPING);
		Multimap<String, ReadingContext> categoryBasedAsset = ArrayListMultimap.create();
		for(int i =0;i< readingsList.size();i++) {
			Map<String,Object> category= (Map<String,Object>) readingsList.get(i).getData().get(ImportAPI.ImportProcessConstants.CATEGORY_FROM_CONTEXT);
			if(category == null) {
				String CategoryName = ImportAPI.ImportProcessConstants.NO_CATEGORY_DEFINED;
				categoryBasedAsset.put(CategoryName, readingsList.get(i));
			}
			else {
			String categoryName = (String) category.get(ImportAPI.ImportProcessConstants.NAME_FIELD);
			categoryBasedAsset.put(categoryName, readingsList.get(i));
			}
			
		}
		Integer bulkSetting =1;
		context.put(ImportAPI.ImportProcessConstants.CATEGORY_BASED_ASSETS, categoryBasedAsset);
		context.put(ImportAPI.ImportProcessConstants.BULK_SETTING, bulkSetting);
		return false;
	}

}