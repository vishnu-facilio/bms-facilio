package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.chain.Chain;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.json.simple.JSONObject;

import com.facilio.bmsconsole.actions.ImportProcessContext;
import com.facilio.bmsconsole.actions.ImportTemplateContext;
import com.facilio.bmsconsole.context.ReadingContext;
import com.facilio.bmsconsole.context.ReadingContext.SourceType;
import com.facilio.bmsconsole.util.ImportAPI;
import com.facilio.constants.FacilioConstants;
import com.google.common.collect.ArrayListMultimap;

public class InsertReadingCommand implements Command {

	private static final Logger LOGGER = Logger.getLogger(InsertReadingCommand.class.getName());
	@Override
	public boolean execute(Context context) throws Exception {
		ImportProcessContext importProcessContext =(ImportProcessContext) context.get(ImportAPI.ImportProcessConstants.IMPORT_PROCESS_CONTEXT);
		ImportTemplateContext importTemplateContext = (ImportTemplateContext) context.get(ImportAPI.ImportProcessConstants.IMPORT_TEMPLATE_CONTEXT);
		JSONObject modulesJSON = importTemplateContext.getModuleJSON();
		Map<String, List<ReadingContext>> groupedContext = (Map<String, List<ReadingContext>>) context.get(ImportAPI.ImportProcessConstants.GROUPED_READING_CONTEXT);
		ArrayListMultimap<String , String> groupedFields = (ArrayListMultimap<String, String>) context.get(ImportAPI.ImportProcessConstants.GROUPED_FIELDS);
		List<String> keys = new ArrayList(groupedFields.keySet());
		HashMap<Integer, HashMap<String, Object>> nullUniqueFields = (HashMap<Integer,HashMap<String,Object>>) context.get(ImportAPI.ImportProcessConstants.NULL_UNIQUE_FIELDS);
		HashMap<Integer, HashMap<String, Object>> nullResources = (HashMap<Integer,HashMap<String,Object>>) context.get(ImportAPI.ImportProcessConstants.NULL_RESOURCES);
		int nullFields = 0;
		for(int i=0; i<keys.size(); i++) {
		insertReadings(keys.get(i),groupedContext.get(keys.get(i)));
		}
		StringBuilder emailMessage = new StringBuilder();
		emailMessage.append("Inserted Readings: " + groupedContext.size());
		if(nullUniqueFields != null) {
			nullFields = nullFields + nullUniqueFields.size();
		}
		if(nullResources !=  null) {
			nullFields = nullFields + nullResources.size();
		}
		emailMessage.append("Skipped Readings:" + nullFields);
		JSONObject meta = importProcessContext.getImportJobMetaJson();
		if(meta == null) {
			JSONObject metainfo = new JSONObject();
			if(modulesJSON.get("baseModule").equals(FacilioConstants.ContextNames.SPACE)) {
				metainfo.put("Inserted", (groupedContext.size()/ groupedFields.keySet().size()));
			}
			else {
				metainfo.put("Inserted", groupedContext.size());
			}
			metainfo.put("Skipped", nullFields);
			importProcessContext.setImportJobMeta(metainfo.toJSONString());
		}
		else {
			if(modulesJSON.get("baseModule").equals(FacilioConstants.ContextNames.SPACE)) {
				meta.put("Inserted", (groupedContext.size()/ groupedFields.keySet().size()));
			}
			else {
				meta.put("Inserted", groupedContext.size());
			}
			meta.put("Skipped", nullFields);
			importProcessContext.setImportJobMeta(meta.toJSONString());
		}
//		ImportAPI.updateImportProcess(importProcessContext);
		context.put(ImportAPI.ImportProcessConstants.EMAIL_MESSAGE, emailMessage);
		return false;
	}
	
	public static void insertReadings(String moduleName,List<ReadingContext> readingsContext) throws Exception {
		
		if(readingsContext == null || readingsContext.isEmpty()) {
			return;
		}
		LOGGER.log(Level.SEVERE, "moduleName - "+moduleName +" readingsContext count -- "+readingsContext.size());
		
		Map<String, List<ReadingContext>> readingMap= Collections.singletonMap(moduleName, readingsContext);
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.HISTORY_READINGS,true);
		context.put(FacilioConstants.ContextNames.READINGS_SOURCE, SourceType.IMPORT);
		//TODO Have to check if this takes more time
//		context.put(FacilioConstants.ContextNames.UPDATE_LAST_READINGS,false);
		
		context.put(FacilioConstants.ContextNames.READINGS_MAP , readingMap);
		Chain importDataChain = ReadOnlyChainFactory.getAddOrUpdateReadingValuesChain();
		importDataChain.execute(context);	
	}
}
