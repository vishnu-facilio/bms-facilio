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
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;

public class InsertReadingCommand implements Command {

	private static final Logger LOGGER = Logger.getLogger(InsertReadingCommand.class.getName());
	@Override
	public boolean execute(Context context) throws Exception {
		int insertSize = 0;
		ImportProcessContext importProcessContext =(ImportProcessContext) context.get(ImportAPI.ImportProcessConstants.IMPORT_PROCESS_CONTEXT);
		ImportTemplateContext importTemplateContext = (ImportTemplateContext) context.get(ImportAPI.ImportProcessConstants.IMPORT_TEMPLATE_CONTEXT);
		JSONObject modulesJSON = importTemplateContext.getModuleJSON();

		Map<String, List<String>> groupedFields = (Map<String, List<String>>) context.get(ImportAPI.ImportProcessConstants.GROUPED_FIELDS);

		HashMap<String, HashMap<String, ReadingContext>> groupedContext = (HashMap<String, HashMap<String, ReadingContext>>) context.get(ImportAPI.ImportProcessConstants.GROUPED_ROW_CONTEXT);
		List<String> keys = new ArrayList(groupedContext.keySet());
		int nullFields = (int)context.get(ImportAPI.ImportProcessConstants.NULL_COUNT);
		for(int i=0; i<keys.size(); i++) {
		insertSize = insertSize + groupedContext.get(keys.get(i)).size();
		LOGGER.severe("---Insert size----" + insertSize);
		List<ReadingContext> readingContexts = new ArrayList(groupedContext.get(keys.get(i)).values());
		insertReadings(keys.get(i),readingContexts);
		}
		StringBuilder emailMessage = new StringBuilder();
		emailMessage.append("Inserted Readings: " + groupedContext.size());
		emailMessage.append("Skipped Readings:" + nullFields);
		JSONObject meta = importProcessContext.getImportJobMetaJson();
		if(meta == null) {
			JSONObject metainfo = new JSONObject();
			if(modulesJSON.get("baseModule").equals(FacilioConstants.ContextNames.SPACE)) {
				metainfo.put("Inserted", (insertSize/ groupedFields.keySet().size()));
			}
			else {
				metainfo.put("Inserted", insertSize);
			}
			metainfo.put("Skipped", nullFields);
			importProcessContext.setImportJobMeta(metainfo.toJSONString());
		}
		else {
			if(modulesJSON.get("baseModule").equals(FacilioConstants.ContextNames.SPACE)) {
				meta.put("Inserted", (insertSize/ groupedFields.keySet().size()));
			}
			else {
				meta.put("Inserted", insertSize);
			}
			meta.put("Skipped", nullFields);
			importProcessContext.setImportJobMeta(meta.toJSONString());
		}
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
		
		context.put(FacilioConstants.ContextNames.READINGS_MAP , readingMap);
		Chain importDataChain = ReadOnlyChainFactory.getAddOrUpdateReadingValuesChain();
		importDataChain.execute(context);	
	}
}
