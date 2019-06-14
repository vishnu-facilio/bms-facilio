package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.map.HashedMap;

import com.facilio.bmsconsole.actions.ImportProcessContext;
import com.facilio.bmsconsole.context.ReadingContext;
import com.facilio.bmsconsole.exceptions.importExceptions.ImportParseException;
import com.facilio.bmsconsole.util.ImportAPI;
import com.mysql.fabric.xmlrpc.base.Array;

public class SeperateToCategoriesCommand implements Command{
	
	private static Logger LOGGER = Logger.getLogger(SeperateToCategoriesCommand.class.getName());
	@Override
	public boolean execute(Context context) throws Exception{
		
		// List<ReadingContext> readingsList = (List<ReadingContext>) context.get(ImportAPI.ImportProcessConstants.READINGS_LIST);
		ImportProcessContext importProcessContext = (ImportProcessContext) context.get(ImportAPI.ImportProcessConstants.IMPORT_PROCESS_CONTEXT);
		// Multimap<String, ReadingContext> categoryBasedAsset = ArrayListMultimap.create();
		HashMap<String, List<ReadingContext>> categoryBasedAsset = new HashMap<String, List<ReadingContext>>();
		HashMap<String, List<ReadingContext>> groupedContext = (HashMap<String, List<ReadingContext>>) context.get(ImportAPI.ImportProcessConstants.GROUPED_READING_CONTEXT);
		
		Map<String, Object> category = null;
		
		for(String module : groupedContext.keySet()) {
			List<ReadingContext> readingsList = groupedContext.get(module);
			for(int i =0;i< readingsList.size();i++) {
				try {
				LOGGER.severe("Resolving Categories for row : " + i);
				LOGGER.severe(readingsList.get(i).getData().get(ImportAPI.ImportProcessConstants.CATEGORY_FROM_CONTEXT).toString());
				category= (Map<String,Object>) readingsList.get(i).getData().get(ImportAPI.ImportProcessConstants.CATEGORY_FROM_CONTEXT);
				}catch(Exception e) {
					ImportParseException importException = new ImportParseException(i,importProcessContext.getFieldMapping().get("asset__category"), e);
					throw importException;
				}
				if(category == null) {
					String categoryName = ImportAPI.ImportProcessConstants.NO_CATEGORY_DEFINED;
					addCategory(categoryName, readingsList.get(i), categoryBasedAsset);
					//categoryBasedAsset.put(CategoryName, readingsList.get(i));
				}
				else {
				String categoryName = (String) category.get(ImportAPI.ImportProcessConstants.NAME_FIELD);
				addCategory(categoryName, readingsList.get(i), categoryBasedAsset);
				//categoryBasedAsset.put(categoryName, readingsList.get(i));
				}
				
			}
			
		}
		
		Integer bulkSetting =1;
		context.put(ImportAPI.ImportProcessConstants.CATEGORY_BASED_ASSETS, categoryBasedAsset);
		context.put(ImportAPI.ImportProcessConstants.BULK_SETTING, bulkSetting);
		return false;
	}
	
	private void addCategory(String categoryName,ReadingContext reading, HashMap<String, List<ReadingContext>> categoryBasedAsset) {
		if(categoryBasedAsset.containsKey(categoryName)) {
			categoryBasedAsset.get(categoryName).add(reading);
		}
		else {
			ArrayList<ReadingContext> readingList = new ArrayList<ReadingContext>();
			readingList.add(reading);
			categoryBasedAsset.put(categoryName, readingList);
		}
	}

}