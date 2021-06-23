package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.actions.ImportProcessContext;
import com.facilio.bmsconsole.context.ReadingContext;
import com.facilio.bmsconsole.exceptions.importExceptions.ImportParseException;
import com.facilio.bmsconsole.util.ImportAPI;

public class SeperateToCategoriesCommand extends FacilioCommand {
	
	private static Logger LOGGER = Logger.getLogger(SeperateToCategoriesCommand.class.getName());
	@Override
	public boolean executeCommand(Context context) throws Exception{
		
		ImportProcessContext importProcessContext = (ImportProcessContext) context.get(ImportAPI.ImportProcessConstants.IMPORT_PROCESS_CONTEXT);
		HashMap<String, List<ReadingContext>> categoryBasedAsset = new HashMap<String, List<ReadingContext>>();
		HashMap<String, List<ReadingContext>> groupedContext = (HashMap<String, List<ReadingContext>>) context.get(ImportAPI.ImportProcessConstants.GROUPED_READING_CONTEXT);
		
		Map<String, Object> category = null;
		
		for(String module : groupedContext.keySet()) {
			List<ReadingContext> readingsList = groupedContext.get(module);
			for(int i =0;i< readingsList.size();i++) {
				try {
				LOGGER.info("Resolving Categories for row : " + i);
				LOGGER.info(readingsList.get(i).getData().get(ImportAPI.ImportProcessConstants.CATEGORY_FROM_CONTEXT).toString());
				category= (Map<String,Object>) readingsList.get(i).getData().get(ImportAPI.ImportProcessConstants.CATEGORY_FROM_CONTEXT);
				}catch(Exception e) {
					ImportParseException importException = new ImportParseException(i,importProcessContext.getFieldMapping().get("asset__category"), e);
					throw importException;
				}
				if(category == null) {
					String categoryName = ImportAPI.ImportProcessConstants.NO_CATEGORY_DEFINED;
					addCategory(categoryName, readingsList.get(i), categoryBasedAsset);
				}
				else {
				String categoryName = (String) category.get(ImportAPI.ImportProcessConstants.NAME_FIELD);
				addCategory(categoryName, readingsList.get(i), categoryBasedAsset);
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