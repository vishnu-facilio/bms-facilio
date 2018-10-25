package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.chain.Chain;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.json.simple.JSONObject;

import com.facilio.bmsconsole.actions.ImportProcessContext;
import com.facilio.bmsconsole.context.ReadingContext;
import com.facilio.bmsconsole.util.ImportAPI;
import com.facilio.constants.FacilioConstants;
import com.google.common.collect.ArrayListMultimap;

public class InsertReadingCommand implements Command {

	private static final Logger LOGGER = Logger.getLogger(InsertReadingCommand.class.getName());
	@Override
	public boolean execute(Context context) throws Exception {
		context.get(ImportAPI.ImportProcessConstants.READINGS_LIST);
		ImportProcessContext importProcessContext =(ImportProcessContext) context.get(ImportAPI.ImportProcessConstants.IMPORT_PROCESS_CONTEXT);
		ArrayListMultimap<String, ReadingContext> groupedContext = (ArrayListMultimap<String, ReadingContext>) context.get(ImportAPI.ImportProcessConstants.GROUPED_READING_CONTEXT);
		ArrayListMultimap<String , String> groupedFields = (ArrayListMultimap<String, String>) context.get(ImportAPI.ImportProcessConstants.GROUPED_FIELDS);
		List<String> keys = new ArrayList(groupedFields.keySet());
		
		
		for(int i=0; i<keys.size(); i++) {
		insertReadings(keys.get(i),groupedContext.get(keys.get(i)));
		}
		StringBuilder emailMessage = new StringBuilder();
		emailMessage.append("Inserted Readings: " + groupedContext.size());
		JSONObject meta = importProcessContext.getImportJobMetaJson();
		if(meta == null) {
			JSONObject metainfo = new JSONObject();
			metainfo.put("Inserted", groupedContext.size());
			importProcessContext.setImportJobMeta(metainfo.toJSONString());
		}
		else {
			meta.put("Inserted", groupedContext.size());
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
		
		//TODO Have to check if this takes more time
//		context.put(FacilioConstants.ContextNames.UPDATE_LAST_READINGS,false);
		
		context.put(FacilioConstants.ContextNames.READINGS_MAP , readingMap);
		Chain importDataChain = FacilioChainFactory.getAddOrUpdateReadingValuesChain();
		importDataChain.execute(context);	
	}
}
