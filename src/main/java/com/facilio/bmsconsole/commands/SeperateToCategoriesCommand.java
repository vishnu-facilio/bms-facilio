package com.facilio.bmsconsole.commands;

import com.facilio.bmsconsole.actions.ImportProcessContext;
import com.facilio.bmsconsole.context.ReadingContext;
import com.facilio.bmsconsole.exceptions.importExceptions.ImportParseException;
import com.facilio.bmsconsole.util.ImportAPI;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

public class SeperateToCategoriesCommand implements Command{
	
	private static Logger LOGGER = Logger.getLogger(SeperateToCategoriesCommand.class.getName());
	@Override
	public boolean execute(Context context) throws Exception{
		
		List<ReadingContext> readingsList = (List<ReadingContext>) context.get(ImportAPI.ImportProcessConstants.READINGS_LIST);
		ImportProcessContext importProcessContext = (ImportProcessContext) context.get(ImportAPI.ImportProcessConstants.IMPORT_PROCESS_CONTEXT);
		context.get(ImportAPI.ImportProcessConstants.FIELDS_MAPPING);
		Multimap<String, ReadingContext> categoryBasedAsset = ArrayListMultimap.create();
		Map<String, Object> category = null;
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